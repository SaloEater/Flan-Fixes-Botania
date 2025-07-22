package com.saloeater.flan_fixes.botania;

import io.github.flemmli97.flan.claim.ClaimStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.entity.ManaBurstEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public class ManaBurstEntityHelper {
    public static boolean evaluateCanPlayerHitByManaBurst(BlockPos pos, ManaBurst entity) {
        if (!(entity instanceof ManaBurstEntity burst) || !(burst.level() instanceof ServerLevel world)) {
            return false;
        }

        if (burst.getOwner() instanceof ServerPlayer serverPlayer) {
            return ManaBurstEntityHelper.evaluateCanPlayerHitByPlayer(world, pos, serverPlayer);
        }

        var serverPlayer = ManaBurstEntityHelper.getOwner(burst);
        if (serverPlayer == null) {
            return false;
        }

        burst.setOwner(serverPlayer);
        return BotaniaCompat.canLensProjectileHit(burst, pos);
    }

    public static boolean evaluateCanPlayerHitByPlayer(Level level, BlockPos pos, ServerPlayer player) {
        if (pos == null) {
            return false;
        }
        if (player == null) {
            return !ManaBurstEntityHelper.isClaimExist(pos, level);
        }
        return BotaniaCompat.canPlayerHit(player, pos);
    }

    public static boolean evaluateCanHitByUUID(Level world, BlockPos pos, UUID ownerID) {
        return ManaBurstEntityHelper.evaluateCanPlayerHitByPlayer(world, pos, ManaBurstEntityHelper.getPlayerByUUID(world, ownerID));
    }

    private static @Nullable ServerPlayer getOwner(ManaBurstEntity burst) {
        if (!(burst instanceof IOwnedByPlayer owner)) {
            return null;
        }

        var ownerID = IOwnedByPlayerHelper.getOwnerID(owner);
        if (ownerID == null) {
            return null;
        }

        return getPlayerByUUID(burst.level(), ownerID);
    }

    public static @Nullable ServerPlayer getPlayerByUUID(Level level, UUID ownerID) {
        var server = level.getServer();
        if (server == null) {
            return null;
        }

        var onlinePlayer = server.getPlayerList().getPlayer(ownerID);
        if (onlinePlayer != null) {
            return onlinePlayer;
        }

        return LevelUtils.getFakePlayer((ServerLevel) level, ownerID);
    }

    private static boolean isClaimExist(BlockPos pos, Level level) {
        if (!(level instanceof ServerLevel world)) {
            return false;
        }
        var storage = ClaimStorage.get(world);
        return storage.getClaimAt(pos) != null;
    }
}
