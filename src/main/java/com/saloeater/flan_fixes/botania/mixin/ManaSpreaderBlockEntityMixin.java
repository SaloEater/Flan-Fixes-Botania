package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.block_entity.mana.ManaSpreaderBlockEntity;
import vazkii.botania.common.entity.ManaBurstEntity;

import java.util.UUID;

@Mixin(ManaSpreaderBlockEntity.class)
public abstract class ManaSpreaderBlockEntityMixin implements IOwnedByPlayer {
    public UUID ownerID;

    @Inject(method = "getBurst", at = @At("RETURN"), remap = false)
    public void flan_fixes$getBurst(boolean fake, CallbackInfoReturnable<ManaBurstEntity> info) {
        ManaBurstEntity burst = info.getReturnValue();
        if (burst == null || burst.getOwner() != null) {
            return;
        }

        if (!(burst instanceof IOwnedByPlayer ownedByPlayer)) {
            return;
        }

        ownedByPlayer.setOwnerID(this.ownerID);
    }

    @Inject(method = "readPacketNBT", at = @At("RETURN"), remap = false)
    public void flan_fixes$readPacketNBT(CompoundTag tag, CallbackInfo info) {
        if (tag.contains("Flan:PlayerOrigin"))
            this.ownerID = tag.getUUID("Flan:PlayerOrigin");
    }

    @Inject(method = "writePacketNBT", at = @At("RETURN"), remap = false)
    public void flan_fixes$writePacketNBT(CompoundTag tag, CallbackInfo info) {
        if (this.ownerID != null)
            tag.putUUID("Flan:PlayerOrigin", this.ownerID);
    }

    @Unique
    public void setOwnerID(UUID uuid) {
        this.ownerID = uuid;
    }

    @Unique
    public UUID getOwnerID() {
        return this.ownerID;
    }
}
