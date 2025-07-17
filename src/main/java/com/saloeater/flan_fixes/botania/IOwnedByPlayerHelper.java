package com.saloeater.flan_fixes.botania;

import java.util.UUID;

public class IOwnedByPlayerHelper {
    public static UUID getOwnerID(IOwnedByPlayer ownedByPlayer) {
        try {
            return ownedByPlayer.getOwnerID();
        } catch (AbstractMethodError ignored) {
            return null;
        }
    }

    public static void setOwnerID(IOwnedByPlayer ownedByPlayer, UUID uuid) {
        try {
            ownedByPlayer.setOwnerID(uuid);
        } catch (AbstractMethodError ignored) {}
    }
}