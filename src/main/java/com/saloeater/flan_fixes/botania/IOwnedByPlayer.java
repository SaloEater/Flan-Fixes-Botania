package com.saloeater.flan_fixes.botania;

import java.util.UUID;

public interface IOwnedByPlayer {
    void setOwnerID(UUID uuid);
    UUID getOwnerID();
}
