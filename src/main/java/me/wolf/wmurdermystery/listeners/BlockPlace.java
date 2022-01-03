package me.wolf.wmurdermystery.listeners;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {

    private final MurderMysteryPlugin plugin;

    public BlockPlace(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        //cancelling for all players that have a custom player obj, no need to place blocks anywhere
        event.setCancelled(plugin.getPlayerManager().getMMPlayer(event.getPlayer().getUniqueId()) != null);
    }


}
