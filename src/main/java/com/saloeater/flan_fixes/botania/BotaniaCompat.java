package com.saloeater.flan_fixes.botania;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.claim.PermHelper;
import io.github.flemmli97.flan.config.ConfigHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import vazkii.botania.common.entity.ManaBurstEntity;

public class BotaniaCompat {
    public static final ResourceLocation PROJECTILE = new ResourceLocation(FlanFixesBotania.MODID, "lens_projectile");

    public static boolean canLensProjectileHit(ManaBurstEntity burst, BlockPos blockPos) {
        var player = burst.getOwner();
        if (!(player instanceof ServerPlayer owner)) {
            return false;
        }

        return canPlayerHit(owner, blockPos);
    }

    public static boolean canPlayerHit(ServerPlayer owner, BlockPos blockPos) {
        var canHit = ClaimHandler.canInteract(owner, blockPos, PROJECTILE);
        if (!canHit) {
            owner.displayClientMessage(PermHelper.simpleColoredText(ConfigHandler.LANG_MANAGER.get("noPermissionSimple"), ChatFormatting.DARK_RED), true);
        }
        return canHit;
    }
}
