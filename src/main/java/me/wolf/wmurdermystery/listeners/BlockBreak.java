package me.wolf.wmurdermystery.listeners;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    private final MurderMysteryPlugin plugin;
    public BlockBreak(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        //cancelling for all players that have a custom player object, no need to break blocks while ingame
        if(plugin.getMmPlayers().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
