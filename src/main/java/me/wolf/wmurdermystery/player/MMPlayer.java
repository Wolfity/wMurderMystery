package me.wolf.wmurdermystery.player;

import me.wolf.wmurdermystery.role.Role;

import java.util.Objects;
import java.util.UUID;


public class MMPlayer {

    private final UUID uuid;
    private int kills;
    private boolean isSpectator;
    private Role role;
    private UUID killer;

    public MMPlayer(final UUID uuid) {
        this.role = Role.UNASSIGNED;
        this.uuid = uuid;
        this.kills = 0;
        this.isSpectator = false;

    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(final boolean spectator) {
        isSpectator = spectator;
    }

    public void incrementKills() {
        this.kills++;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getKills() {
        return kills;
    }

    public UUID getKiller() {
        return killer;
    }

    public void setKiller(final UUID killer) {
        this.killer = killer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MMPlayer mmPlayer = (MMPlayer) o;
        return uuid.equals(mmPlayer.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
