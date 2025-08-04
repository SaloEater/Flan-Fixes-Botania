package com.saloeater.flan_fixes.botania.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.item.rod.LandsRodItem;

@Mixin(value = LandsRodItem.class, remap = false)
public abstract class LandsRodItemMixin {
    @Inject(
        method = "place",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private static void flan_fixes_botania$place(UseOnContext ctx, Block block, int cost, float r, float g, float b, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = ctx.getPlayer();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = world.getBlockState(pos);
        var cancel = MinecraftForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(world.dimension(), world, pos), state, player));
        if (cancel) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
