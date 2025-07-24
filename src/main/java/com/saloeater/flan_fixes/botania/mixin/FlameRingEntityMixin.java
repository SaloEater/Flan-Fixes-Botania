package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.IStorage;
import com.saloeater.flan_fixes.botania.IStorageHelper;
import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.entity.FlameRingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(value =  FlameRingEntity.class, remap = false)
public class FlameRingEntityMixin implements IOwnedByPlayer, IStorage {
    UUID owner;
    public final Map<String, Boolean> positionsCache = new HashMap<>();

    @Inject(
        method = "baseTick",
        at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void baseTick(CallbackInfo ci, float radius, float renderRadius, AABB boundingBox, List<LivingEntity> entities) {
        var ring = (FlameRingEntity) (Object) this;
        entities.removeIf(entity -> !canHitCached(ring.level(), entity.blockPosition()));
    }

    private boolean canHitCached(Level level, BlockPos pos) {
        var key = IStorageHelper.getBlockPosKey(pos);
        if (IStorageHelper.has(this, key)) {
            return Boolean.TRUE.equals(IStorageHelper.get(this, key));
        }
        var canHit = ManaBurstEntityHelper.evaluateCanHitByUUID(level, pos, owner);
        IStorageHelper.set(this, key, canHit);
        return canHit;
    }

    @Inject(
        method = "readAdditionalSaveData",
        at = @At("TAIL")
    )
    public void readAdditionalSaveData(CompoundTag cmp, CallbackInfo ci) {
        if (cmp.hasUUID("Flan:PlayerOrigin")) {
            owner = cmp.getUUID("Flan:PlayerOrigin");
        }
    }

    @Inject(
        method = "addAdditionalSaveData",
        at = @At("TAIL")
    )
    public  void addAdditionalSaveData(CompoundTag cmp, CallbackInfo ci) {
        if (owner != null) {
            cmp.putUUID("Flan:PlayerOrigin", owner);
        }
    }

    @Override
    public void setOwnerID(UUID uuid) {
        owner = uuid;
    }

    @Override
    public UUID getOwnerID() {
        return owner;
    }

    public void set(String uuid, Boolean value) {
        this.positionsCache.put(uuid, value);
    }

    public Boolean get(String uuid) {
        return this.positionsCache.get(uuid);
    }

    public boolean has(String uuid) {
        return this.positionsCache.containsKey(uuid);
    }
}
