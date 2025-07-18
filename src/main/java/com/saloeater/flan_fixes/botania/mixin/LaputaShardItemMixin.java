package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.IOwnedByPlayerHelper;
import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.item.LaputaShardItem;

import java.util.UUID;

@Mixin(value = LaputaShardItem.class, remap = false)
public abstract class LaputaShardItemMixin {
    ServerPlayer player;
    UUID uuid;

    @Inject(
            method = "m_6225_", 
            at = @At("HEAD"), 
            cancellable = true
    )
    public void flan_fixes$onUseOn_Head(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            cir.cancel();
            return;
        }
        player = serverPlayer;
        uuid = player.getUUID();
    }

    @Inject(method = "m_6225_", at = @At("RETURN"))
    public void flan_fixes$onUseOn_Return(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        player = null;
        uuid = null;
    }

    @Inject(method ="updateBurst", at = @At("HEAD"))
    public void flan_fixes$updateBurst_Head(ManaBurst burst, ItemStack stack, CallbackInfo ci) {
        if (!(burst.entity() instanceof ManaBurstEntity)) {
            return;
        }

        player = burst.entity().getOwner() instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        uuid = player != null ? player.getUUID() : (burst instanceof IOwnedByPlayer owner ? IOwnedByPlayerHelper.getOwnerID(owner) : null);
    }

    @Inject(method = "updateBurst", at = @At("RETURN"))
    public void flan_fixes$updateBurst_Return(ManaBurst burst, ItemStack stack, CallbackInfo ci) {
        player = null;
        uuid = null;
    }

    @Shadow
    private static boolean canMove(BlockState state, Level world, BlockPos pos) {
        throw new AbstractMethodError("Shadow method should not be called directly!");
    }

    @Redirect(method = "spawnNextBurst(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;ZD)V",
            at = @At(value = "INVOKE", target = "Lvazkii/botania/common/item/LaputaShardItem;canMove(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
    public boolean flan_fixes$spawnNextBurst_canMove(BlockState state, Level world, BlockPos pos) {
        var canHit = false;

        if (player != null) {
            canHit = ManaBurstEntityHelper.evaluateCanPlayerHitByPlayer(world, pos, player);
        } else if( uuid != null) {
            canHit = ManaBurstEntityHelper.evaluateCanHitByUUID(world, pos, uuid);
        }

        return canHit && canMove(state, world, pos);
    }

    @Inject(
            method="getBurst",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void flan_fixes$getBurst(Level world, BlockPos pos, ItemStack stack, CallbackInfoReturnable<ManaBurstEntity> cir, ManaBurstEntity burst) {
        if (player != null) {
            burst.setOwner(player);
        }
    }
}
