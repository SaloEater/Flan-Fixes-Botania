package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IStorage;
import com.saloeater.flan_fixes.botania.IStorageHelper;
import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.item.lens.LensItem;

@Mixin(value = LensItem.class, remap = false)
public abstract class LensItemMixin {
    @Inject(method = "collideBurst", at = @At(value = "HEAD"), cancellable = true)
    public void flan_fixes$collideBurst(ManaBurst burst, HitResult hit, boolean isManaBlock, boolean shouldKill, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(burst.entity() instanceof ManaBurstEntity burstEntity)) {
            return;
        }
        var pos = this.getPos(hit, burstEntity);

        if (!this.canHitAtPos(burstEntity, pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateBurst", at = @At(value = "HEAD"), cancellable = true)
    public void flan_fixes$updateBurst(ManaBurst burst, ItemStack stack, CallbackInfo ci) {
        if (!(burst.entity() instanceof ManaBurstEntity burstEntity)) {
            return;
        }
        var pos = burstEntity.getOnPos();

        if (!this.canHitAtPos(burstEntity, pos)) {
            ci.cancel();
        }
    }

    public boolean canHitAtPos(ManaBurstEntity burstEntity, BlockPos pos) {
        var cachedValue = this.fromCache(burstEntity, pos);
        if (cachedValue != null) {
            return cachedValue;
        }

        var canHit = ManaBurstEntityHelper.evaluateCanPlayerHitByManaBurst(pos, burstEntity);
        this.setCache(burstEntity, pos, canHit);
        return canHit;
    }

    public void setCache(ManaBurstEntity burstEntity, BlockPos pos, boolean canHit) {
        if (!(burstEntity instanceof IStorage storage)) {
            return;
        }

        String key = IStorageHelper.getBlockPosKey(pos);
        IStorageHelper.set(storage, key, canHit);
    }

    public Boolean fromCache(ManaBurst burst, BlockPos pos) {
        if (!(burst instanceof IStorage storage)) {
            return null;
        }

        String key = IStorageHelper.getBlockPosKey(pos);
        if (IStorageHelper.has(storage, key)) {
            return Boolean.FALSE.equals(IStorageHelper.get(storage, key));
        }
        return null;
    }

    public BlockPos getPos(HitResult hit, ManaBurstEntity burstEntity) {
        if (hit.getType() == HitResult.Type.BLOCK) {
            return ((BlockHitResult) hit).getBlockPos();
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult) hit).getEntity().getOnPos();
        }
        return burstEntity.getOnPos();
    }
}
