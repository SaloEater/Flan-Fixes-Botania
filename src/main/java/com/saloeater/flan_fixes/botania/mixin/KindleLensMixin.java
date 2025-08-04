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
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.item.lens.KindleLens;

@Mixin(value = KindleLens.class, remap = false)
public abstract class KindleLensMixin {
    @Inject(
        method = "collideBurst",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;m_7471_(Lnet/minecraft/core/BlockPos;Z)Z"), //removeBlock
        cancellable = true
    )
    public void flan_fixes_botania$collideBurst_remove(ManaBurst burst, HitResult rtr, boolean isManaBlock, boolean shouldKill, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
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

    @Inject(
        method = "collideBurst",
        at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/level/Level;m_46597_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"), //setBlockAndUpdate
            cancellable = true)
    public void flan_fixes_botania$collideBurst_setAndUpdate(ManaBurst burst, HitResult rtr, boolean isManaBlock, boolean shouldKill, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = burst.entity();
        Level world = entity.level();
        BlockPos collidePos = ((BlockHitResult) rtr).getBlockPos();
        BlockPos blockPos2 = collidePos.relative(((BlockHitResult) rtr).getDirection());
        BlockState state2 = world.getBlockState(blockPos2);
        var owner = ManaBurstEntityHelper.getOwner((ManaBurstEntity) entity);
        if (owner == null) {
            cir.setReturnValue(false);
        }
        var cancel = MinecraftForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(world.dimension(), world, collidePos), state2, owner));
        if (cancel) {
            cir.setReturnValue(false);
        }
    }
}
