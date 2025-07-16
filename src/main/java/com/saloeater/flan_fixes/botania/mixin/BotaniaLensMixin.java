package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.BotaniaCompat;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.entity.ManaBurstEntity;

@Mixin(ManaBurstEntity.class)
public abstract class BotaniaLensMixin  {
    @Inject(method = "onHitCommon(Lnet/minecraft/core/BlockPos;Z)V", at = @At("HEAD"), cancellable = true)
    private void canLensHit(HitResult hit, boolean shouldKill, CallbackInfo info) {
        if (!BotaniaCompat.canLensProjectileHit((ManaBurstEntity) (Object) this, hit))
            info.cancel();
    }
}
