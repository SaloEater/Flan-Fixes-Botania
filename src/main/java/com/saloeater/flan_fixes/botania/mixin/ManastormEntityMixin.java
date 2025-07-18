package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.IOwnedByPlayerHelper;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.entity.ManaStormEntity;

import java.util.UUID;

@Mixin(value = ManaStormEntity.class, remap = false)
public abstract class ManastormEntityMixin implements IOwnedByPlayer {
    public UUID ownerID;

    @Inject(method = "m_7378_", at = @At("TAIL"))
    public void flan_fixes$readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("Flan:PlayerOrigin")) {
            this.setOwnerID(tag.getUUID("Flan:PlayerOrigin"));
        }
    }

    @Inject(method = "m_7380_", at = @At("TAIL"))
    public void flan_fixes$addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (this.getOwnerID() != null) {
            tag.putUUID("Flan:PlayerOrigin", this.getOwnerID());
        }
    }

    @Inject(
            method="spawnBurst",
            at = @At(
                    value = "TAIL"
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void flan_fixes$spawnBurst(CallbackInfo ci, ManaBurstEntity burst) {
        if (IOwnedByPlayerHelper.getOwnerID(this) != null && burst instanceof IOwnedByPlayer burstOwnedByPlayer) {
            IOwnedByPlayerHelper.setOwnerID(burstOwnedByPlayer, IOwnedByPlayerHelper.getOwnerID(this));
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
