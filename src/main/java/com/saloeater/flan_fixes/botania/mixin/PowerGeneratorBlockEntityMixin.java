package com.saloeater.flan_fixes.botania.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.block_entity.mana.PowerGeneratorBlockEntity;

@Mixin(value = PowerGeneratorBlockEntity.class, remap = false)
public abstract class PowerGeneratorBlockEntityMixin {
    @Shadow @Final
    private static int MANA_TO_FE;
    @Shadow
    private int energy;
    int flan_fixes_botania$lastIncome = 0;

    @Inject(
        method = "canReceiveManaFromBursts",
        at = @At("HEAD"),
        cancellable = true
    )
    private void flan_fixes_botania$canReceiveManaFromBursts(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(PowerGeneratorBlockEntity.MAX_ENERGY - energy > flan_fixes_botania$lastIncome);
    }

    @Inject(
        method = "receiveMana",
        at = @At("HEAD")
    )
    private void flan_fixes_botania$receiveMana(int mana, CallbackInfo ci) {
        flan_fixes_botania$lastIncome = mana * MANA_TO_FE;
    }
}
