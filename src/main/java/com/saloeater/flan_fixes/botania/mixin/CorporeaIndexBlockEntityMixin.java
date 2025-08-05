package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.BotaniaCompat;
import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntity;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;

import java.util.UUID;

@Mixin(value = CorporeaIndexBlockEntity.class, remap = false)
public abstract class CorporeaIndexBlockEntityMixin extends BotaniaBlockEntity implements IOwnedByPlayer {
    public UUID ownerID;

    public CorporeaIndexBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
        method = "performPlayerRequest",
        at = @At("HEAD"),
        cancellable = true
    )
    private void flan_fixes_botania$performPlayerRequest(ServerPlayer player, CorporeaRequestMatcher request, int count, CallbackInfo ci) {
        if (this.ownerID == null) {
            player.displayClientMessage(Component.literal("Unable to use owner-less Corporea Index").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), true);
            ci.cancel();
        }

        var index = ((CorporeaIndexBlockEntity)(Object) this);
        if (!BotaniaCompat.canPlayerUseCorporea(player, index.getBlockPos())) {
            ci.cancel();
        }
    }

    @Override
    public void writePacketNBT(CompoundTag cmp) {
        super.writePacketNBT(cmp);
        if (this.ownerID != null) {
            cmp.putUUID("Flan:PlayerOrigin", this.ownerID);
        }
    }

    @Override
    public void readPacketNBT(CompoundTag cmp) {
        super.readPacketNBT(cmp);
        if (cmp.contains("Flan:PlayerOrigin")) {
            this.ownerID = cmp.getUUID("Flan:PlayerOrigin");
        }
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.ownerID = uuid;
    }

    @Override
    public UUID getOwnerID() {
        return this.ownerID;
    }
}
