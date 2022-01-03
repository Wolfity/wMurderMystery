package me.wolf.wmurdermystery.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, MMPlayer> mmPlayers = new HashMap<>();

    public void addMMPlayer(final UUID uuid) {
        this.mmPlayers.put(uuid, new MMPlayer(uuid));
    }

    public void removeMMPlayer(final UUID uuid) {
        this.mmPlayers.remove(uuid);
    }

    public MMPlayer getMMPlayer(final UUID uuid) {
        return mmPlayers.get(uuid);
    }

    public Map<UUID, MMPlayer> getMmPlayers() {
        return mmPlayers;
    }
}
