package com.saloeater.flan_fixes.botania;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IRedStringBlock {
    void SetLevel(Level level);
    Level GetLevel();
}
