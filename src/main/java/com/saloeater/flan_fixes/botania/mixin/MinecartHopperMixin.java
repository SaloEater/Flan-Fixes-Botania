package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.permission.BuiltinPermission;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = MinecartHopper.class)
public abstract class MinecartHopperMixin implements IOwnedByPlayer {
    UUID owner;

    @Override
    public void setOwnerID(UUID uuid) {
        this.owner = uuid;
    }

    @Override
    public UUID getOwnerID() {
        return owner;
    }

    @Inject(
        method = "m_38592_", //suckInItems
        at = @At("HEAD"),
        cancellable = true
    )
    public void flan_fixes_vanilla$suckInItems(CallbackInfoReturnable<Boolean> cir) {
        if (this.owner == null) {
            cir.setReturnValue(false);
            return;
        }

        MinecartHopper hopper = (MinecartHopper) (Object) this;
        if (!(hopper.level() instanceof ServerLevel)) {
            cir.setReturnValue(false);
            return;
        }

        ServerLevel level = (ServerLevel) hopper.level();
        var minecartOwner = ManaBurstEntityHelper.getPlayerByUUID(level, owner);
        if (minecartOwner == null) {
            cir.setReturnValue(false);
            return;
        }

        BlockPos containerPos = hopper.blockPosition().relative(Direction.UP);
        var canInteract = ClaimHandler.canInteract(minecartOwner, containerPos, BuiltinPermission.OPENCONTAINER);
        if (!canInteract) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "m_7378_", //readAdditionalSaveData
        at = @At("TAIL")
    )
    private void flan_fixes_vanilla$readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.hasUUID("Flan:PlayerOrigin")) {
            owner = tag.getUUID("Flan:PlayerOrigin");
        }
    }

    @Inject(
        method = "m_7380_", //addAdditionalSaveData
        at = @At("TAIL")
    )
    private void flan_fixes_vanilla$addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (owner != null) {
            tag.putUUID("Flan:PlayerOrigin", owner);
        }
    }
}
