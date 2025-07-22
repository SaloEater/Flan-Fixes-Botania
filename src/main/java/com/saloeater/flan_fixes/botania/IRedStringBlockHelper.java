package com.saloeater.flan_fixes.botania;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class IRedStringBlockHelper {
    public static void SetLevel(Level level, IRedStringBlock redStringBlock) {
        try {
            redStringBlock.SetLevel(level);
        } catch (AbstractMethodError ignored) {}
    }
    public static Level GetLevel(IRedStringBlock redStringBlock) {
        try {
            return redStringBlock.GetLevel();
        } catch (AbstractMethodError e) {
            return null;
        }
    }
}