package me.wolf.wmurdermystery.listeners;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final MurderMysteryPlugin plugin;

    public PlayerQuit(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getPlayerManager().getMMPlayer(player.getUniqueId()) == null) return;

        plugin.getGameManager().removePlayer(player);

    }

}
