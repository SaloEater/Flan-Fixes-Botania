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

    public static boolean canLensProjectileHit(ManaBurstEntity burst, HitResult hit) {
        var owner = burst.getOwner();
        if (!(owner instanceof ServerPlayer)) {
            return false;
        }

        return ClaimHandler.canInteract((ServerPlayer) owner, new BlockPos(new Vec3i((int) hit.getLocation().x, (int) hit.getLocation().y, (int) hit.getLocation().z)), PROJECTILE);
    }
}
