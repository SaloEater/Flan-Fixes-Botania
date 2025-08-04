package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.item.lens.WeightLens;

@Mixin(value = WeightLens.class, remap = false)
public abstract class WeightLensMixin {
    @Inject(
        method = "collideBurst",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;m_201971_(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/entity/item/FallingBlockEntity;"//fall
        ), cancellable = true
    )
    public void flan_fixes_botania$collideBurst(ManaBurst burst, HitResult rtr, boolean isManaBlock, boolean shouldKill, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = burst.entity();
        Level world = entity.level();
        BlockPos collidePos = ((BlockHitResult) rtr).getBlockPos();
        BlockState state = world.getBlockState(collidePos);
        var owner = ManaBurstEntityHelper.getOwner((ManaBurstEntity) entity);
        if (owner == null) {
            cir.setReturnValue(false);
        }
        var cancel = MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, collidePos, state, owner));
        if (cancel) {
            cir.setReturnValue(false);
        }
    }
}
