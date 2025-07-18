package com.saloeater.flan_fixes.botania;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.GameProfileCache;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class LevelUtils {

    public static @Nullable FakePlayer getFakePlayer(ServerLevel level, @Nullable UUID owner) {
        if (owner != null) {
            GameProfileCache profileCache = level.getServer().getProfileCache();

            Optional<GameProfile> profile = profileCache.get(owner);

            if (profile.isPresent()) {
                return FakePlayerFactory.get(level, profile.get());
            }
        }

        return null;
    }

}
