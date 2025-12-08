package com.saloeater.flan_fixes.botania.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.item.rod.ShiftingCrustRodItem;

import java.util.List;

@Mixin(value = ShiftingCrustRodItem.class, remap = false)
public abstract class ShiftingCrustRodItemMixin {
    ServerPlayer owner;

    @Inject(
        method = "m_6225_", //useOn
        at = @At(value = "HEAD")
    )
    public void flan_fixes$onUse(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        if (ctx.getPlayer() instanceof ServerPlayer serverPlayer) {
            owner = serverPlayer;
        }
    }

    @Inject(
        method = "m_6225_", //useOn
        at = @At(value = "RETURN")
    )
    public void flan_fixes$onUseReturn(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        owner = null;
    }

    @Inject(
        method = "m_6883_", //inventoryTick
        at = @At(value = "HEAD")
    )
    public void flan_fixes$onInventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean equipped, CallbackInfo ci) {
        if (entity instanceof ServerPlayer serverPlayer) {
            owner = serverPlayer;
        }
    }

    @Inject(
        method = "m_6883_", //inventoryTick
        at = @At(value = "RETURN")
    )
    public void flan_fixes$onInventoryTickReturn(ItemStack stack, Level world, Entity entity, int slot, boolean equipped, CallbackInfo ci) {
        owner = null;
    }

    @Inject(
        method = "getWireframesToDraw",
        at = @At(value = "HEAD")
    )
    public void flan_fixes$getWireframesToDraw(Player player, ItemStack stack, CallbackInfoReturnable<List<BlockPos>> cir) {
        if (player instanceof ServerPlayer serverPlayer) {
            owner = serverPlayer;
        }
    }

    @Inject(
        method = "getWireframesToDraw",
        at = @At(value = "RETURN")
    )
    public void flan_fixes$getWireframesToDrawReturn(Player player, ItemStack stack, CallbackInfoReturnable<List<BlockPos>> cir) {
        owner = null;
    }

    @Inject(
        method = "getTargetPositions",
        at = @At(value = "RETURN"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void flan_fixes$getTargetPositions(Level world, ItemStack stack, Item toPlace, BlockPos pos, Block toReplace, Direction clickedSide, CallbackInfoReturnable<List<BlockPos>> cir, List<BlockPos> coordsList) {
        if (owner == null) {
            coordsList.clear();
            return;
        }

        var player = owner;
        coordsList.removeIf(blockPos -> MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, world.getBlockState(blockPos), player)));
    }
}
