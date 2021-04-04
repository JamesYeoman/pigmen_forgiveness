package com.degenerate_human.pigmen_forgiveness.events;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

public class AngryAtPlayerEvent extends Event {
    private final Integer pigZombieID;
    private final UUID playerUUID;

    public AngryAtPlayerEvent(int pigZombieID, UUID playerUUID) {
        this.pigZombieID = pigZombieID;
        this.playerUUID = playerUUID;
    }

    public Integer getPigZombieID() {
        return pigZombieID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
