package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.IStorage;
import com.saloeater.flan_fixes.botania.IStorageHelper;
import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import vazkii.botania.common.entity.ManaBurstEntity;

import java.util.*;

@Mixin(value = ManaBurstEntity.class, remap = false)
public abstract class ManaBurstEntityMixin extends Projectile implements IOwnedByPlayer, IStorage {
    public UUID ownerID;
    public BlockPos pos;
    private final Map<UUID, Boolean> entityMap = new HashMap<>();

    public ManaBurstEntityMixin(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    @Shadow
    protected void onHitBlock(@NotNull BlockHitResult hit) {
    }

    @Shadow
    protected void onHitEntity(@NotNull EntityHitResult hit) {
    }

    @Override
    public void onHit(@NotNull HitResult hit) {
        var burst = (ManaBurstEntity) (Object) this;
        if (!this.canHitResultCached(burst, hit)) {
            return;
        }
        if (burst.isFake()) {
            if (hit.getType() == HitResult.Type.BLOCK) {
                this.onHitBlock((BlockHitResult) hit);
            } else if (hit.getType() == HitResult.Type.ENTITY) {
                this.onHitEntity((EntityHitResult) hit);
            }
        } else {
            super.onHit(hit);
        }
    }

    private boolean canHitResultCached(ManaBurstEntity burst, @NotNull HitResult hit) {
        var pos = getPos(hit, burst);
        var cached = this.get(IStorageHelper.getKey(pos));
        if (cached != null) {
            return cached;
        }

        var canHit = this.canHitResult(hit);
        this.set(IStorageHelper.getKey(pos), canHit);
        return canHit;
    }

    private BlockPos getPos(HitResult hit, ManaBurstEntity burstEntity) {
        if (hit.getType() == HitResult.Type.BLOCK) {
            return ((BlockHitResult) hit).getBlockPos();
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult) hit).getEntity().getOnPos();
        }
        return burstEntity.getOnPos();
    }

    private boolean canHitResult(HitResult hit) {
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            return this.canLensHit(pos);
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            return this.canLensHit(entityHit.getEntity().getOnPos());
        }
        return false;
    }

    public boolean canLensHit(BlockPos pos) {
        if (pos == null) {
            return true;
        }

        var entity = (ManaBurstEntity) (Object) this;
        return ManaBurstEntityHelper.evaluatePlayer(pos, entity, this.ownerID);
    }

    /*@Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (tag.contains("Flan:PlayerOrigin"))
            this.ownerID = tag.getUUID("Flan:PlayerOrigin");
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (this.ownerID != null)
            tag.putUUID("Flan:PlayerOrigin", this.ownerID);
    }*/

    @Unique
    public void setOwnerID(UUID uuid) {
        this.ownerID = uuid;
    }

    @Unique
    public UUID getOwnerID() {
        return this.ownerID;
    }

    public void set(UUID uuid, Boolean value) {
        this.entityMap.put(uuid, value);
    }

    public Boolean get(UUID uuid) {
        return this.entityMap.get(uuid);
    }

    public boolean has(UUID uuid) {
        return this.entityMap.containsKey(uuid);
    }
}
