package com.saloeater.flan_fixes.botania;

import java.util.UUID;

public class IOwnedByPlayerHelper {
    public static UUID getOwnerID(IOwnedByPlayer ownedByPlayer) {
        if (ownedByPlayer == null) {
            return null;
        }
        try {
            return ownedByPlayer.getOwnerID();
        } catch (AbstractMethodError ignored) {
            return null;
        }
    }

    public static void setOwnerID(IOwnedByPlayer ownedByPlayer, UUID uuid) {
        if (ownedByPlayer == null) {
            return;
        }
        try {
            ownedByPlayer.setOwnerID(uuid);
        } catch (AbstractMethodError ignored) {}
    }
}