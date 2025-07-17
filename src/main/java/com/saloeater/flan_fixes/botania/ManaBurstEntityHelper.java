package com.saloeater.flan_fixes.botania;

import io.github.flemmli97.flan.api.permission.PermissionManager;
import io.github.flemmli97.flan.claim.Claim;
import io.github.flemmli97.flan.claim.ClaimStorage;
import io.github.flemmli97.flan.claim.GlobalClaim;
import io.github.flemmli97.flan.config.Config;
import io.github.flemmli97.flan.config.ConfigHandler;
import io.github.flemmli97.flan.player.LogoutTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import vazkii.botania.common.entity.ManaBurstEntity;

import java.util.Iterator;
import java.util.UUID;

import static com.saloeater.flan_fixes.botania.BotaniaCompat.PROJECTILE;

public class ManaBurstEntityHelper {
    public static boolean evaluateCanPlayerHitByManaBurst(BlockPos pos, ManaBurstEntity entity, IOwnedByPlayer owner) {
        if (owner == null || IOwnedByPlayerHelper.getOwnerID(owner) == null) {
            return !ManaBurstEntityHelper.isClaimExist(pos, entity.level());
        }
        boolean canHit;
        var serverPlayer = ManaBurstEntityHelper.getOwner(entity);
        if (serverPlayer != null) {
            entity.setOwner(serverPlayer);
            canHit = BotaniaCompat.canLensProjectileHit(entity, pos);
        } else {
            canHit = ManaBurstEntityHelper.evaluateCanOfflinePlayerHit(entity.level(), pos, IOwnedByPlayerHelper.getOwnerID(owner));
        }
        return canHit;
    }

    public static boolean evaluateCanPlayerHit(BlockPos pos, ServerPlayer owner, Level level) {
        if (owner == null) {
            return !ManaBurstEntityHelper.isClaimExist(pos, level);
        }
        return BotaniaCompat.canPlayerHit(owner, pos);
    }

    public static boolean evaluateCanPlayerHitByUUID(Level world, BlockPos pos, UUID ownerID) {
        return ManaBurstEntityHelper.evaluateCanOfflinePlayerHit(world, pos, ownerID);
    }

    private static ServerPlayer getOwner(ManaBurstEntity burst) {
        if (burst.getOwner() instanceof ServerPlayer) {
            return (ServerPlayer) burst.getOwner();
        }

        var owner = burst instanceof IOwnedByPlayer o ? o : null;
        if (owner == null) {
            return null;
        }

        var ownerID = IOwnedByPlayerHelper.getOwnerID(owner);
        if (ownerID == null) {
            return null;
        }
        var server = burst.level().getServer();
        if (server == null) {
            return null;
        }

        return server.getPlayerList().getPlayer(ownerID);
    }

    private static boolean isClaimExist(BlockPos pos, Level level) {
        if (!(level instanceof ServerLevel world)) {
            return false;
        }
        var storage = ClaimStorage.get(world);
        return storage.getClaimAt(pos) == null;
    }

    private static boolean evaluateCanOfflinePlayerHit(Level level, BlockPos pos, UUID ownerID) {
        if (!(level instanceof ServerLevel world)) {
            return false;
        }

        var storage = ClaimStorage.get(world);
        var claim = storage.getClaimAt(pos);
        if (claim == null) {
            if (!(storage.getForPermissionCheck(pos) instanceof GlobalClaim)) {
                return false;
            }
            return getGlobalClaimPermission(world);
        }

        return getClaimPermission(claim, ownerID, world, pos);
    }

    private static boolean getGlobalClaimPermission(ServerLevel serverLevel) {
        Config.GlobalType global = ConfigHandler.CONFIG.getGlobal(serverLevel, PROJECTILE);
        return global == Config.GlobalType.NONE || global.getValue();
    }

    private static boolean getClaimPermission(Claim claim, UUID ownerID, ServerLevel world, BlockPos pos) {
        if (ownerID == null) {
            return false;
        }
        if (claim.getOwner().equals(ownerID) || claim.getAllowedFakePlayerUUID().contains(ownerID.toString())) {
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
                    return claimHasPerm(claim, perm);
                }

                claim = (Claim) subClaims.next();
            } while (!claim.insideClaim(pos));

            return getClaimPermission(claim, ownerID, world, pos);
        }

        subClaims = claim.getAllSubclaims().iterator();

        while (subClaims.hasNext()) {
            var subClaim = (Claim) subClaims.next();
            if (subClaim.insideClaim(pos)) {
                return getClaimPermission(subClaim, ownerID, world, pos);
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

        return claimHasPerm(claim, perm);
    }

    private static boolean claimHasPerm(Claim claim, ResourceLocation perm) {
        if (claim.parentClaim() == null) {
            return claim.permEnabled(perm) == 1;
        } else if (claim.permEnabled(perm) == -1) {
            return claim.parentClaim().permEnabled(perm) == 1;
        }
        return claim.permEnabled(perm) == 1;
    }
}
