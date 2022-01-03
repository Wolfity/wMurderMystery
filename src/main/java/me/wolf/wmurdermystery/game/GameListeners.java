package me.wolf.wmurdermystery.game;

import com.cryptomorin.xseries.XSound;
import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.arena.Arena;
import me.wolf.wmurdermystery.arena.ArenaState;
import me.wolf.wmurdermystery.constants.Messages;
import me.wolf.wmurdermystery.player.MMPlayer;
import me.wolf.wmurdermystery.role.Role;
import me.wolf.wmurdermystery.shop.ShopEffect;
import me.wolf.wmurdermystery.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
public class GameListeners implements Listener {

    private final MurderMysteryPlugin plugin;

    public GameListeners(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    // checking if the player is interacting with the shop NPC
    @EventHandler
    public void shopInteract(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getMmPlayers().containsKey(player.getUniqueId())) return;
        final MMPlayer mmPlayer = plugin.getMmPlayers().get(player.getUniqueId());
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(mmPlayer);
        if (arena.getArenaState() == ArenaState.INGAME || arena.getArenaState() == ArenaState.GRACE) {
            if (event.getRightClicked() instanceof Villager) {
                event.setCancelled(true);
            }
            final Villager npc = (Villager) event.getRightClicked();
            if (!npc.getCustomName().equalsIgnoreCase(Utils.colorize("&eGold Shop &7(2 gold, 1 random effect!)")))
                return;
            if (canAfford(player)) {
                applyRandomEffect(player);
            }
        }
    }

    // checking if someone is killed, and if it's the murderer, if you kill an innocent/detective, both die
    @EventHandler
    public void onKill(final EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        final Player killed = (Player) event.getEntity();

        if (plugin.getMmPlayers().containsKey(killed.getUniqueId())) {
            final MMPlayer mmKilled = plugin.getMmPlayers().get(killed.getUniqueId());
            final Arena arena = plugin.getArenaManager().getArenaByPlayer(mmKilled);

            if (event.getDamager().getType() == EntityType.ARROW) {
                if (!(((Arrow) event.getDamager()).getShooter() instanceof Player)) return;
                final LivingEntity killer = (LivingEntity) ((Arrow) event.getDamager()).getShooter();
                final MMPlayer mmKiller = plugin.getMmPlayers().get(killer.getUniqueId());

                if (mmKilled.getRole() == Role.MURDERER) {
                    mmKilled.setKiller(killer.getUniqueId());
                    setSpectator(mmKilled);
                    plugin.getGameManager().setGameState(GameState.END, arena);
                }

                // checking if detectives gets killed by an innocent, only checking for innocent, because the murderer can't get a bow + arrow
                if (mmKilled.getRole() == Role.DETECTIVE && mmKiller.getRole() == Role.INNOCENT) {
                    setSpectator(mmKiller, mmKilled);
                    spawnDetectiveBow(mmKilled);
                }
                // checking if an innocent gets killed by an innocent or detective
                if (mmKilled.getRole() == Role.INNOCENT && mmKiller.getRole() != Role.MURDERER) {
                    setSpectator(mmKiller, mmKilled);
                }
                playDamageSound(arena);

            } else if (event.getDamager() instanceof Player) {
                final Player killer = (Player) event.getDamager();
                final MMPlayer mmKiller = plugin.getMmPlayers().get(killer.getUniqueId());
                // checking if someone got killed by a sword (murderer)
                if (killer.getItemInHand().getType() == Material.IRON_SWORD) {

                    //checking if it's the detective
                    if (mmKilled.getRole() == Role.DETECTIVE) {
                        playDetectiveKilledMessage(arena, killed);
                        spawnDetectiveBow(mmKilled);
                    }

                    mmKiller.incrementKills();
                    setSpectator(mmKilled);
                    playDamageSound(arena);
                }

            }

            // filtering out all arena members that are spectators, if there's 1 left, end game, this can only happen to the murderer
            if (arena.getArenaMembers().stream().filter(mmPlayer -> !mmPlayer.isSpectator() && Utils.getLastPlayer(arena).getRole() == Role.MURDERER).count() == 1) {
                plugin.getGameManager().setGameState(GameState.END, arena);
            }
            event.setCancelled(true);
        }
    }


    // only allowing spectators to chat with eachother, and not with alive players
    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getMmPlayers().containsKey(player.getUniqueId())) return;
        final MMPlayer mmPlayer = plugin.getMmPlayers().get(player.getUniqueId());
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(mmPlayer);
        final Set<MMPlayer> spectators = arena.getArenaMembers().stream().filter(MMPlayer::isSpectator).collect(Collectors.toSet());

        if (!spectators.contains(mmPlayer)) return;
        event.setCancelled(true);
        for (final MMPlayer spec : spectators) {
            final Player receivers = Bukkit.getPlayer(spec.getUuid());
            receivers.sendMessage(Utils.colorize("&7[Dead] " + event.getPlayer().getDisplayName() + " ") + event.getMessage());
        }
    }

    // if a player picks up an item, check if it's gold, if he has more than 10, add a bow and arrow

    @EventHandler
    public void onGoldPickup(final PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getMmPlayers().containsKey(player.getUniqueId())) return;
        final MMPlayer mmPlayer = plugin.getMmPlayers().get(player.getUniqueId());
        if (mmPlayer.getRole() == Role.MURDERER) return;
        if (event.getItem().getItemStack().getType() != Material.GOLD_INGOT) return;
        for (final ItemStack is : player.getInventory()) {
            if (is == null) continue;
            if (is.getAmount() + event.getItem().getItemStack().getAmount() >= 10) { // if the player has more than 10 gold, remove gold add arrow and bow if they dont have a bow yet
                Bukkit.getScheduler().runTaskLater(plugin,
                        () -> player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 10)), 1L);
                player.getInventory().addItem(new ItemStack(Material.ARROW));
                if (!player.getInventory().contains(Material.BOW)) {
                    player.getInventory().addItem(new ItemStack(Material.BOW));
                }
            }
        }
    }

    // checking if a player picks up the dead detective's bow
    @EventHandler
    public void onDetectiveBowPickup(final PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getMmPlayers().containsKey(player.getUniqueId())) return;
        final MMPlayer mmPlayer = plugin.getMmPlayers().get(player.getUniqueId());
        if (mmPlayer.getRole() != Role.MURDERER) {
            if (event.getItem().getItemStack().getType() == Material.BOW) {
                for (final Entity stand : player.getNearbyEntities(2, 2, 2)) {
                    if (stand instanceof ArmorStand) {
                        stand.remove(); // to clear out the invis armor stand
                    }
                }
                mmPlayer.setRole(Role.DETECTIVE); //updating the role
            }
        } else event.setCancelled(true);


    }


    // check if the player has 2 or more gold ingots, if so return true
    private boolean canAfford(final Player player) {
        for (final ItemStack is : player.getInventory()) {
            if (is == null) continue;
            if (is.getType() == Material.GOLD_INGOT) {
                if (is.getAmount() >= 2) {
                    is.setAmount(is.getAmount() - 2);
                    return true;
                }
            }
        }
        player.sendMessage(Messages.CANT_AFFORD_SHOP);
        player.playSound(player.getLocation(), XSound.ENTITY_VILLAGER_NO.parseSound(), 1f, 1f);
        return false;
    }

    // looping over the List<ShopEffects> to apply a random effect
    private void applyRandomEffect(final Player player) {
        final List<ShopEffect> activeEffects = plugin.getShopEffectManager().getShopEffects().stream().filter(ShopEffect::isEnabled).collect(Collectors.toList());
        final int randomIndex = new Random().nextInt(activeEffects.size());
        final ShopEffect effect = activeEffects.get(randomIndex);
        player.addPotionEffect(new PotionEffect(effect.getEffect(), effect.getDuration() * 20, effect.getAmplifier()));
        player.sendMessage(Utils.colorize("&eYou received the effect &c" + effect.getEffect().getName() + "&e!"));
        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1f, 1f);
    }

    private void setSpectator(final MMPlayer... mmPlayer) {
        for (final MMPlayer mmKilled : mmPlayer) {
            final Player player = Bukkit.getPlayer(mmKilled.getUuid());
            mmKilled.setSpectator(true);
            player.setHealth(20);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle(Utils.colorize("&cYou died!"), "");
            player.getInventory().clear();
        }

    }

    // if the detective dies, allow the person to pick this bow up the become the new detective
    private void spawnDetectiveBow(final MMPlayer mmKilled) {
        mmKilled.setRole(Role.UNASSIGNED);// removing the role, this way he won't receive arrows anymore

        final Player killed = Bukkit.getPlayer(mmKilled.getUuid());
        final ArmorStand bowIcon = killed.getLocation().getWorld().spawn(killed.getLocation(), ArmorStand.class);
        bowIcon.setVisible(false);
        bowIcon.setGravity(false);
        bowIcon.setCustomNameVisible(true); // spawning the armor stand
        bowIcon.setCustomName(Utils.colorize("&b&lDetective's Bow"));
        killed.getWorld().dropItem(killed.getLocation(), new ItemStack(Material.BOW));

    }

    private void playDetectiveKilledMessage(final Arena arena, final Player killed) {
        arena.getArenaMembers().stream().filter(mmPlayer -> !mmPlayer.isSpectator()).forEach(mmPlayer -> {
            final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
            player.sendTitle(Utils.colorize("&bDetective Was Killed"), "");
            player.sendMessage(Messages.DETECTIVE_DIED
                    .replace("{x}", String.valueOf(Math.round(killed.getLocation().getX())))
                    .replace("{y}", String.valueOf(Math.round(killed.getLocation().getY())))
                    .replace("{z}", String.valueOf(Math.round(killed.getLocation().getZ()))));
        });

    }

    private void playDamageSound(final Arena arena) {
        arena.getArenaMembers().forEach(mmPlayer -> {
            final Player alLPlayers = Bukkit.getPlayer(mmPlayer.getUuid());
            alLPlayers.playSound(alLPlayers.getLocation(), XSound.ENTITY_PLAYER_HURT.parseSound(), 1f, 1f);
        });
    }

}
