package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.item.lens.PaintslingerLens;

import java.util.List;

@Mixin(value = PaintslingerLens.class, remap = false)
public abstract class PaintslingerLensMixin {
    @Inject(
            method = "collideBurst", 
            at = @At(
                    value = "INVOKE", 
                    target = "java/util/List.iterator()Ljava/util/Iterator;", 
                    shift = At.Shift.BEFORE, 
                    ordinal = 1,
                    remap = false 
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION 
    )
    public void botania$modifyCoordsToPaint(ManaBurst burst, HitResult hit, boolean isManaBlock, boolean shouldKill, ItemStack stack, CallbackInfoReturnable<Boolean> cir, Entity entity, int storedColor, BlockPos hitPos, Block hitBlock, ResourceLocation blockId, List<BlockPos> coordsToPaint) {
        coordsToPaint.removeIf(pos -> !ManaBurstEntityHelper.evaluateCanPlayerHitByManaBurst(pos, burst));
    }
}