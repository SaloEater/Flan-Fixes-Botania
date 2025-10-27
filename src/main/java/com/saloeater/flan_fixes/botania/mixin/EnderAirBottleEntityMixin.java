package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.BotaniaCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.entity.EnderAirBottleEntity;

import java.util.List;

@Mixin(value = EnderAirBottleEntity.class, remap = false)
public abstract class EnderAirBottleEntityMixin {
    @Inject(
        method = "getCoordsToPut",
        at = @At("RETURN"),
        cancellable = true
    )
    private void flan_fixes_botania$getCoordsToPut(BlockPos pos, CallbackInfoReturnable<List<BlockPos>> cir) {
        List<BlockPos> blocksToPut = cir.getReturnValue();
        EnderAirBottleEntity enderAirBottleEntity = (EnderAirBottleEntity) (Object) this;
        if (!(enderAirBottleEntity.getOwner() instanceof ServerPlayer player)) {
            return;
        }
        blocksToPut.forEach(blockPos -> {
            if (!BotaniaCompat.canPlayerInteract(player, blockPos)) {
                blocksToPut.remove(blockPos);
            }
        });
        cir.setReturnValue(blocksToPut);
    }
}
