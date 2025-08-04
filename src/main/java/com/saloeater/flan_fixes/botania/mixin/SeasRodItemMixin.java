package com.saloeater.flan_fixes.botania.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.item.rod.SeasRodItem;

@Mixin(value = SeasRodItem.class, remap = false)
public abstract class SeasRodItemMixin {
    @Inject(
        method = "m_7203_", //use
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BucketItem;m_142073_(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;)Z"), //emptyContents
        locals = LocalCapture.CAPTURE_FAILEXCEPTION,
        cancellable = true
    )
    private void flan_fixes_botania$use(Level level, Player player, @NotNull InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack itemStack, BlockHitResult blockHitResult, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState, BlockPos blockPos3){
        var cancel = MinecraftForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(level.dimension(), level, blockPos3), blockState, player));
        if (cancel) {
            cir.setReturnValue(InteractionResultHolder.fail(itemStack));
        }
    }
}
