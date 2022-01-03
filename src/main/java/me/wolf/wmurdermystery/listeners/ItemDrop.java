package me.wolf.wmurdermystery.listeners;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDrop implements Listener {

    private final MurderMysteryPlugin plugin;

    public ItemDrop(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {

        if (plugin.getMmPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
