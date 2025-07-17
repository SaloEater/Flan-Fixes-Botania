package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.IOwnedByPlayerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.block.ManastormChargeBlock;
import vazkii.botania.common.entity.ManaStormEntity;

@Mixin(value = ManastormChargeBlock.ManaTriggerImpl.class, remap = false)
public class ManastormChargeBlockManaTriggerImplMixin {
    @Inject(
            method = "onBurstCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lvazkii/botania/api/internal/ManaBurst;getColor()I"
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void flan_fixes$ManastormChargeBlock_ManaTriggerImpl_addFreshEntity(ManaBurst burst, CallbackInfo ci, ManaStormEntity storm) {
        if (burst instanceof IOwnedByPlayer burstOwnedByPlayer && storm instanceof IOwnedByPlayer stormOwnedByPlayer) {
            IOwnedByPlayerHelper.setOwnerID(stormOwnedByPlayer, IOwnedByPlayerHelper.getOwnerID(burstOwnedByPlayer));
        }
    }
}
