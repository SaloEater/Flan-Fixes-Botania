package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.IOwnedByPlayerHelper;
import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.block.mana.DrumBlock;

import java.util.UUID;

@Mixin(value = DrumBlock.ManaTriggerImpl.class, remap = false)
public abstract class DrumBlockManaTriggerImpl implements IOwnedByPlayer {
    public UUID ownerID;

    ManaBurst manaBurst;

    @Inject(
            method = "onBurstCollision",
            at = @At("HEAD")
    )
    public void flan_fixes$onBurstCollision(ManaBurst burst, CallbackInfo ci){
        this.manaBurst = burst;
    }

    @ModifyArg(
            method = "onBurstCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lvazkii/botania/common/item/HornItem;breakGrass(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)V"
            ),
            index = 3
    )
    public @Nullable LivingEntity modifyHornItemBreakGrassLastArg(@Nullable LivingEntity user) {
        if (this.manaBurst == null) {
            return user;
        }
        if (!(this.manaBurst instanceof IOwnedByPlayer owned)) {
            return user;
        }

        return ManaBurstEntityHelper.getPlayerByUUID(this.manaBurst.entity().level(), IOwnedByPlayerHelper.getOwnerID(owned));
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
