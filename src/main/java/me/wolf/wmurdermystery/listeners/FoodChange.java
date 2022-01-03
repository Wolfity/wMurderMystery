package me.wolf.wmurdermystery.listeners;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodChange implements Listener {

    private final MurderMysteryPlugin plugin;

    public FoodChange(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent event) {
        //cancelling for everyone that has a custom player object, in no way are you supposed to lose a food level
        if (plugin.getMmPlayers().containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }

    }

}
