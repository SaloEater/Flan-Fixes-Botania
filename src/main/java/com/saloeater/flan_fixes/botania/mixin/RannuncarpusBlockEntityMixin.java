package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.IOwnedByPlayerHelper;
import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.block.flower.functional.RannuncarpusBlockEntity;

import java.util.List;
import java.util.UUID;

@Mixin(value = RannuncarpusBlockEntity.class, remap = false)
public abstract class RannuncarpusBlockEntityMixin implements IOwnedByPlayer {
    UUID owner;

    @Inject(
        method = "getCandidatePosition",
        at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Ljava/util/List;isEmpty()Z", ordinal = 0),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void flan_fixes$onGetCandidatePosition(RandomSource rand, ItemStack stack, CallbackInfoReturnable<BlockPos> cir, int rangePlace, int rangePlaceY, BlockPos center, BlockState filter, List<BlockPos> emptyPositions, List<BlockPos> additivePositions) {
        var flower = (RannuncarpusBlockEntity) (Object) this;
        var level = flower.getLevel();
        filterOutPositions(level, emptyPositions);
        filterOutPositions(level, additivePositions);
    }

    @Inject(
        method = "readFromPacketNBT",
        at = @At(value = "TAIL")
    )
    public void flan_fixes$onReadFromPacketNBT(CompoundTag cmp, CallbackInfo ci) {
        if (cmp.contains("Flan:PlayerOrigin")) {
            IOwnedByPlayerHelper.setOwnerID(this, cmp.getUUID("Flan:PlayerOrigin"));
        }
    }

    @Inject(
        method = "writeToPacketNBT",
        at = @At(value = "TAIL")
    )
    public void flan_fixes$onWriteToPacketNBT(CompoundTag cmp, CallbackInfo ci) {
        if (owner != null) {
            cmp.putUUID("Flan:PlayerOrigin", owner);
        }
    }

    private void filterOutPositions(Level level, List<BlockPos> emptyPositions) {
        emptyPositions.removeIf(blockPos -> !(ManaBurstEntityHelper.evaluateCanHitByUUID(level, blockPos, owner)));
    }

    public void setOwnerID(UUID uuid) {
        owner = uuid;
    }

    public UUID getOwnerID() {
        return owner;
    }
}
