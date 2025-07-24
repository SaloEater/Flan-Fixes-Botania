package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.block.HornHarvestable;
import vazkii.botania.common.item.HornItem;

@Mixin(value = HornItem.class, remap = false)
public abstract class HornItemMixin {
    @Inject(
            method="canHarvest",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void flan_fixes$canHarvest(Level level, ItemStack stack, BlockPos pos, LivingEntity user, HornHarvestable.EnumHornType type, CallbackInfoReturnable<Boolean> cir) {
        if (!(user instanceof ServerPlayer serverPlayer) || !ManaBurstEntityHelper.evaluateCanHitByPlayer(level, pos, serverPlayer)) {
            cir.setReturnValue(false);
        }
    }
}
