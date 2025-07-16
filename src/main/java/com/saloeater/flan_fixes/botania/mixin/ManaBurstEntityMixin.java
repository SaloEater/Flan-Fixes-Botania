package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.BotaniaCompat;
import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import io.github.flemmli97.flan.api.permission.PermissionManager;
import io.github.flemmli97.flan.claim.Claim;
import io.github.flemmli97.flan.claim.ClaimStorage;
import io.github.flemmli97.flan.claim.GlobalClaim;
import io.github.flemmli97.flan.config.Config;
import io.github.flemmli97.flan.config.ConfigHandler;
import io.github.flemmli97.flan.player.LogoutTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.entity.ManaBurstEntity;

import java.util.Iterator;
import java.util.UUID;

import static com.saloeater.flan_fixes.botania.BotaniaCompat.PROJECTILE;

@Mixin(value = ManaBurstEntity.class, remap = false)
public abstract class ManaBurstEntityMixin extends Projectile implements IOwnedByPlayer {
    public UUID ownerID;
    public BlockPos pos;

    protected ManaBurstEntityMixin(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        this.pos = hit.getBlockPos();

        if (!canLensHit(this.pos)) {
            return;
        }

        super.onHitBlock(hit);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        this.pos = hit.getEntity().getOnPos();

        if (!canLensHit(this.pos)) {
            return;
        }
        super.onHitEntity(hit);
    }

    public boolean canLensHit(BlockPos pos) {
        var canHit = true;
        if (pos != null) {
            var entity = (ManaBurstEntity) (Object) this;

            var serverPlayer = this.lookupOwner(entity.level(), ownerID);
            if (serverPlayer != null) {
                entity.setOwner(serverPlayer);
                canHit = BotaniaCompat.canLensProjectileHit(entity, pos);
            } else {
                canHit = this.evaluateOfflinePlayer(entity.level(), pos, ownerID);
            }
        }

        return canHit;
    }

    public boolean evaluateOfflinePlayer(Level level, BlockPos blockPos, UUID ownerID) {
        if (!(level instanceof ServerLevel world)) {
            return true;
        }

        var storage = ClaimStorage.get(world);
        var claim = storage.getClaimAt(blockPos);
        if (claim == null) {
            var globalClaim = storage.getForPermissionCheck(blockPos);
            if (!(globalClaim instanceof GlobalClaim)) {
                return true;
            }
            return getGlobalClaimPermission((GlobalClaim) globalClaim, ownerID, storage, world);
        }

        return getClaimPermission(claim, ownerID, storage, world);
    }

    public boolean getGlobalClaimPermission(GlobalClaim claim, UUID ownerID, ClaimStorage storage, ServerLevel serverLevel) {
        Config.GlobalType global = ConfigHandler.CONFIG.getGlobal(serverLevel, PROJECTILE);
        return global == Config.GlobalType.NONE || global.getValue();
    }

    public boolean getClaimPermission(Claim claim, UUID ownerID, ClaimStorage storage, ServerLevel world) {
        if (ownerID == null || claim.getOwner().equals(ownerID) || claim.getAllowedFakePlayerUUID().contains(ownerID.toString())) {
            return true;
        }

        var perm = PROJECTILE;
        if (!claim.isAdminClaim()) {
            Config.GlobalType global = ConfigHandler.CONFIG.getGlobal(world, perm);

            if (ConfigHandler.CONFIG.offlineProtectActivation != -1 && (LogoutTracker.getInstance(world.getServer()).justLoggedOut(ownerID) || claim.getOwnerPlayer().isPresent())) {
                return global == Config.GlobalType.NONE || global.getValue();
            }
        }

        Iterator subClaims;
        if (PermissionManager.INSTANCE.isGlobalPermission(perm)) {
            subClaims = claim.getAllSubclaims().iterator();

            do {
                if (!subClaims.hasNext()) {
                    return this.claimHasPerm(claim, perm);
                }

                claim = (Claim) subClaims.next();
            } while (!claim.insideClaim(pos));

            return this.getClaimPermission(claim, ownerID, storage, world);
        }

        subClaims = claim.getAllSubclaims().iterator();

        while (subClaims.hasNext()) {
            var subClaim = (Claim) subClaims.next();
            if (subClaim.insideClaim(pos)) {
                return this.getClaimPermission(subClaim, ownerID, storage, world);
            }
        }

        var groups = claim.groups();
        var server = world.getServer();

        Claim finalClaim = claim;
        Claim finalClaim1 = claim;
        var ownerIsInGroup = groups.stream()
                .filter(group -> finalClaim.groupHasPerm(group, perm) == 1)
                .map(group -> finalClaim1.playersFromGroup(server, group).contains(ownerID.toString())).findAny().isPresent();
        if (ownerIsInGroup) {
            return true;
        }

        return this.claimHasPerm(claim, perm);
    }

    public boolean claimHasPerm(Claim claim, ResourceLocation perm) {
        if (claim.parentClaim() == null) {
            return claim.permEnabled(perm) == 1;
        } else if (claim.permEnabled(perm) == -1) {
            return claim.parentClaim().permEnabled(perm) == 1;
        }
        return claim.permEnabled(perm) == 1;
    }

    public ServerPlayer lookupOwner(Level level, UUID ownerID) {
        if (this.getOwner() != null && this.getOwner() instanceof ServerPlayer serverPlayer) {
            return serverPlayer;
        }

        if (ownerID == null || level.isClientSide) {
            return null;
        }

        var server = level.getServer();
        if (server == null) {
            return null;
        }

        return server.getPlayerList().getPlayer(ownerID);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (tag.contains("Flan:PlayerOrigin"))
            this.ownerID = tag.getUUID("Flan:PlayerOrigin");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (this.ownerID != null)
            tag.putUUID("Flan:PlayerOrigin", this.ownerID);
    }

    @Unique
    public void setOwnerID(UUID uuid) {
        this.ownerID = uuid;
    }

    @Unique
    public UUID getOwnerID() {
        return this.ownerID;
    }
}
