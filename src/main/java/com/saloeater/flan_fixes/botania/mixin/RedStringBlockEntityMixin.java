package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntity;
import vazkii.botania.common.block.block_entity.red_string.RedStringBlockEntity;

import java.util.UUID;

@Mixin(value =  RedStringBlockEntity.class, remap = false)
public abstract class RedStringBlockEntityMixin extends BotaniaBlockEntity implements IRedStringBlock, IOwnedByPlayer {
    Level level;
    public UUID ownerID;

    public RedStringBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

    @Inject(
            method = "commonTick",
            at = @At("HEAD"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private static void flan_fixes$commonTick_Head(Level level, BlockPos pos, BlockState state, RedStringBlockEntity self, CallbackInfo ci) {
        if (!(self instanceof IRedStringBlock redStringBlock)) {
            return;
        }
        IRedStringBlockHelper.SetLevel(level, redStringBlock);
    }

    @Inject(
            method = "commonTick",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private static void flan_fixes$commonTick_Tail(Level level, BlockPos pos, BlockState state, RedStringBlockEntity self, CallbackInfo ci) {
        if (!(self instanceof IRedStringBlock redStringBlock)) {
            return;
        }
        IRedStringBlockHelper.SetLevel(null, redStringBlock);
    }


    @Inject(
            method = "setBinding",
            at = @At("HEAD"),
            cancellable = true
    )
    public void flan_fixes$setBinding(BlockPos pos, CallbackInfo ci) {
        var level = IRedStringBlockHelper.GetLevel(this);
        if (level == null) {
            ci.cancel();
            return;
        }
        var owner = IOwnedByPlayerHelper.getOwnerID(this);
        if (!ManaBurstEntityHelper.evaluateCanHitByUUID(level, pos, owner)) {
            ci.cancel();
        }
    }

    public void SetLevel(Level level) {
        this.level = level;
    }

    public Level GetLevel() {
        return this.level;
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
