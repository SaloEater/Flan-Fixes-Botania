package com.saloeater.flan_fixes.botania;

import io.github.flemmli97.flan.api.ClaimHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import vazkii.botania.common.entity.ManaBurstEntity;

public class BotaniaCompat {
    public static final ResourceLocation PROJECTILE = new ResourceLocation(FlanFixesBotania.MODID, "lens_projectile");

    public static boolean canLensProjectileHit(ManaBurstEntity burst, BlockPos blockPos) {
        var owner = burst.getOwner();
        if (!(owner instanceof ServerPlayer)) {
            return true;
        }

        return ClaimHandler.canInteract((ServerPlayer) owner, blockPos, PROJECTILE);
    }
}
