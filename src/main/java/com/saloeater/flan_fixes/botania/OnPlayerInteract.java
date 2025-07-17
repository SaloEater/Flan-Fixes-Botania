package com.saloeater.flan_fixes.botania;

import net.minecraftforge.event.level.BlockEvent;
import vazkii.botania.common.block.mana.ManaSpreaderBlock;

public class OnPlayerInteract {
    public static final Class<?>[] OWNED_BLOCKS = {
            ManaSpreaderBlock.class,
    };

    public static boolean isPlayerOwnedBlock(Object block) {
        for (Class<?> clazz : OWNED_BLOCKS) {
            if (clazz.isInstance(block)) {
                return true;
            }
        }
        return false;
    }

    public static void onPlayerInteract(BlockEvent.EntityPlaceEvent e) {
        if (!isPlayerOwnedBlock(e.getPlacedBlock().getBlock()) || e.getEntity() == null) {
            return;
        }

        if (e.getPlacedBlock().hasBlockEntity() && e.getLevel().getBlockEntity(e.getPos()) instanceof IOwnedByPlayer ownedByPlayer) {
            IOwnedByPlayerHelper.setOwnerID(ownedByPlayer, e.getEntity().getUUID());
        }
    }
}
