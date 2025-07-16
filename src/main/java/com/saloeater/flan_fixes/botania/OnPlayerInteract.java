package com.saloeater.flan_fixes.botania;

import net.minecraftforge.event.level.BlockEvent;
import vazkii.botania.common.block.mana.ManaSpreaderBlock;

public class OnPlayerInteract {
    public static void onPlayerInteract(BlockEvent.EntityPlaceEvent e) {
        if (e.getEntity() != null && e.getPlacedBlock().getBlock() instanceof ManaSpreaderBlock) {
            if (e.getPlacedBlock().hasBlockEntity() && e.getLevel().getBlockEntity(e.getPos()) instanceof IOwnedByPlayer spreader) {
                spreader.setOwnerID(e.getEntity().getUUID());
            }
        }
    }
}
