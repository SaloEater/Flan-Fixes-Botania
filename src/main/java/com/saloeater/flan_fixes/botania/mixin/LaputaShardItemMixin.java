package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.item.LaputaShardItem;

import java.util.UUID;

@Mixin(LaputaShardItem.class)
public abstract class LaputaShardItemMixin extends Item {
    public ServerPlayer owner;
    public UUID ownerID;

    public LaputaShardItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        owner = ctx.getPlayer() instanceof ServerPlayer ? (ServerPlayer) ctx.getPlayer() : null;
        ownerID = ctx.getPlayer() != null ? ctx.getPlayer().getUUID() : null;
        return super.useOn(ctx);
    }

    @Inject(method = "getBurst", at = @At("RETURN"), remap = false)
    public void setBurstOwner(Level world, BlockPos pos, ItemStack stack, CallbackInfoReturnable<ManaBurstEntity> info) {
        ManaBurstEntity burst = info.getReturnValue();
        if (burst == null || burst.getOwner() != null) {
            return;
        }

        if (this.owner != null) {
            ServerPlayer originalOwner = this.owner;
            burst.setOwner(originalOwner);
        }

        if (!(burst instanceof IOwnedByPlayer ownedByPlayer)) {
            return;
        }

        ownedByPlayer.setOwnerID(this.ownerID);
    }

    @Inject(method = "canMove", at = @At("RETURN"), remap = false)
    public static void canMove(BlockState state, Level world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {

    }
}
