package me.wolf.wmurdermystery.listeners;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.arena.Arena;
import me.wolf.wmurdermystery.arena.ArenaState;
import me.wolf.wmurdermystery.player.MMPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {

    private MurderMysteryPlugin plugin;

    public EntityDamage(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLobbyDamage(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (plugin.getMmPlayers().containsKey(player.getUniqueId())) {
                final MMPlayer mmPlayer = plugin.getMmPlayers().get(player.getUniqueId());
                final Arena arena = plugin.getArenaManager().getArenaByPlayer(mmPlayer);
                if (arena.getArenaState() == ArenaState.READY || arena.getArenaState() == ArenaState.COUNTDOWN || arena.getArenaState() == ArenaState.GRACE) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
