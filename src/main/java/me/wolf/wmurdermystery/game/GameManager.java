package me.wolf.wmurdermystery.game;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.arena.Arena;
import me.wolf.wmurdermystery.arena.ArenaState;
import me.wolf.wmurdermystery.constants.Messages;
import me.wolf.wmurdermystery.exception.NotEnoughSpawnsException;
import me.wolf.wmurdermystery.player.MMPlayer;
import me.wolf.wmurdermystery.role.Role;
import me.wolf.wmurdermystery.utils.CustomLocation;
import me.wolf.wmurdermystery.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class GameManager {

    private final MurderMysteryPlugin plugin;

    public GameManager(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    private GameState gameState;

    public void setGameState(final GameState gameState, final Arena arena) {
        this.gameState = gameState;
        switch (gameState) {
            case RECRUITING:
                arena.setArenaState(ArenaState.READY);
                enoughPlayers(arena);
                break;
            case LOBBY_COUNTDOWN:
                arena.setArenaState(ArenaState.COUNTDOWN);
                lobbyCountdown(arena);
                break;
            case GRACE:
                arena.setArenaState(ArenaState.GRACE);
                spawnShopNPCs(arena);
                teleportToSpawns(arena);
                spawnGold(arena);
                gracePeriodCountdown(arena);
                break;
            case ACTIVE:
                arena.setArenaState(ArenaState.INGAME);
                gameTimer(arena);
                break;
            case END:
                arena.setArenaState(ArenaState.END);
                sendGameEndNotification(arena);
                Bukkit.getScheduler().runTaskLater(plugin, () -> endGame(arena), 200L);
                break;
        }
    }

    // handles the lobby countdown timer, if this ends the gamestate will be set to active and players will get notified
    private void lobbyCountdown(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.LOBBY_COUNTDOWN) {
                    this.cancel();
                }
                if (arena.getLobbyCountdown() > 0) {
                    arena.decrementLobbyCountdown();
                    arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(mmPlayer -> {
                        final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
                        player.sendMessage(Messages.LOBBY_COUNTDOWN.replace("{countdown}", String.valueOf(arena.getLobbyCountdown())));
                    });
                } else {
                    this.cancel();
                    arena.resetLobbyCountdownTimer();
                    assignRoles(arena);
                    arena.getArenaMembers().forEach(mmPlayer -> {
                        final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
                        player.sendMessage(Messages.GRACE_PERIOD.replace(" {seconds}", String.valueOf(arena.getGraceTimer())));
                        plugin.getScoreboard().gameScoreboard(player, arena);
                    });
                    setGameState(GameState.GRACE, arena);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // the game timer, handles the time of the game
    private void gameTimer(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.ACTIVE) {
                    this.cancel();
                }
                if (arena.getGameTimer() > 0) {
                    arena.decrementGameTimer();
                    arena.getArenaMembers().forEach(mmPlayer -> plugin.getScoreboard().gameScoreboard(Bukkit.getPlayer(mmPlayer.getUuid()), arena));
                    updateDetectiveInventory(arena);
                } else {
                    this.cancel();
                    setGameState(GameState.END, arena);
                    arena.resetGameTimer();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // after the game has just started, there is a grace period, where players can't fight. After this ends the murderer and detective get their items
    private void gracePeriodCountdown(final Arena arena) {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (arena.getGraceTimer() > 0) {
                    arena.decrementGraceTimer();
                } else {
                    this.cancel();
                    setGameState(GameState.ACTIVE, arena);
                    arena.getArenaMembers().forEach(mmPlayer -> {
                        final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
                        player.sendMessage(Messages.GAME_STARTED);
                        player.sendMessage(Messages.GRACE_PERIOD_END);
                        giveGameInventory(player); //give murder & detective their items
                    });
                    arena.getArenaMembers().forEach(mmPlayer -> {
                        final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
                        player.sendMessage(Messages.GAME_WILL_START_SOON);
                        plugin.getScoreboard().gameScoreboard(Bukkit.getPlayer(mmPlayer.getUuid()), arena);
                    });
                    arena.resetGraceTimer();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void endGame(final Arena arena) {

        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(mmPlayer -> {

            final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
            player.getInventory().clear();
            teleportToWorld(arena);

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SURVIVAL);

            plugin.getMmPlayers().remove(player.getUniqueId());

            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            player.setPlayerListName(player.getName());
            player.getActivePotionEffects().clear();

            mmPlayer.setSpectator(false);
            mmPlayer.setRole(Role.UNASSIGNED);
        });
        arena.getSpawnLocations().get(0).toBukkitLocation().getWorld();
        resetTimers(arena);
        arena.getArenaMembers().clear();
        plugin.getArenaManager().clearArena(arena);
        setGameState(GameState.RECRUITING, arena);
    }

    // after the game has ended, teleport all players back to the "world spawn"
    private void teleportToWorld(final Arena arena) {
        arena.getArenaMembers().forEach(mmPlayer -> {
            final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
            final Location worldLoc = (Location) plugin.getConfig().get("WorldSpawn");
            player.teleport(worldLoc);
            plugin.getMmPlayers().remove(player.getUniqueId());
        });
    }

    // teleport the players to their team spawns
    private void teleportToSpawns(final Arena arena) {
        final Queue<CustomLocation> remainingSpawns = new ArrayDeque<>(arena.getSpawnLocations());
        for (final MMPlayer mmPlayer : arena.getArenaMembers()) {
            final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
            if (arena.getArenaMembers().size() > arena.getSpawnLocations().size()) {
                throw new NotEnoughSpawnsException("There are more players then spawn positions!");
            }
            player.teleport(remainingSpawns.poll().toBukkitLocation());
        }
    }

    public void teleportToLobby(final Player player, final Arena arena) {
        player.teleport(arena.getWaitingRoomLoc().toBukkitLocation());
    }

    // add a player to the game
    public void addPlayer(final Player player, final Arena arena) {
        if (plugin.getArenaManager().isGameActive(arena)) {
            player.sendMessage(Messages.GAME_IN_PROGRESS);
        }
        if (!arena.getArenaMembers().contains(plugin.getMmPlayers().get(player.getUniqueId()))) {
            if (arena.getArenaMembers().isEmpty()) {
                setGameState(GameState.RECRUITING, arena);
            }
            if (arena.getArenaMembers().size() >= arena.getArenaConfig().getInt("max-players")) {
                player.sendMessage(Messages.ARENA_IS_FULL);
            }
            //create new MMPlayer object
            plugin.getMmPlayers().put(player.getUniqueId(), new MMPlayer(player.getUniqueId(), plugin));
            final MMPlayer mmPlayer = plugin.getMmPlayers().get(player.getUniqueId());
            arena.getArenaMembers().add(mmPlayer);

            plugin.getScoreboard().lobbyScoreboard(player, arena);
            teleportToLobby(player, arena);
            giveLobbyInventory(player);

            arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(arenaMembers -> {
                final Player arenaPlayers = Bukkit.getPlayer(arenaMembers.getUuid());
                plugin.getScoreboard().lobbyScoreboard(arenaPlayers, arena);
                arenaPlayers.sendMessage(Messages.PLAYER_JOINED_GAME.replace("{player}", player.getDisplayName()));
            });
            enoughPlayers(arena);
            player.sendMessage(Messages.JOINED_ARENA.replace("{arena}", arena.getName()));
        } else player.sendMessage(Messages.ALREADY_IN_ARENA);
    }

    // remove a player from the game, teleport them, clear the custom player object
    public void removePlayer(final Player player) {
        for (final Arena arena : plugin.getArenas()) {
            if (!arena.getArenaMembers().contains(plugin.getMmPlayers().get(player.getUniqueId()))) {
                player.sendMessage(Messages.NOT_IN_ARENA);
            }
            if(plugin.getConfig().get("WorldSpawn") == null) {player.sendMessage(Utils.colorize("&cSomething went wrong, no world spawn set!"));}

            final MMPlayer mmPlayer = plugin.getMmPlayers().get(player.getUniqueId());
            arena.getArenaMembers().remove(mmPlayer);
            plugin.getMmPlayers().remove(player.getUniqueId());

            player.teleport((Location) plugin.getConfig().get("WorldSpawn"));
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            player.getInventory().clear();
            player.sendMessage(Messages.LEFT_ARENA.replace("{arena}", arena.getName()));

            leaveGameCheck(arena);
            arena.getArenaMembers()
                    .stream().filter(Objects::nonNull)
                    .forEach(arenaMember -> Bukkit.getPlayer(arenaMember.getUuid()).sendMessage(Messages.PLAYER_LEFT_GAME.replace("{player}", player.getDisplayName())));
        }
    }

    // If someone leaves, check if there are any players left, else reset the game
    private void leaveGameCheck(final Arena arena) {
        if (gameState == GameState.LOBBY_COUNTDOWN) {
            if (arena.getArenaMembers().size() <= 1) {
                setGameState(GameState.RECRUITING, arena);
                resetTimers(arena);
            }
        } else if (gameState == GameState.GRACE) {
            if (arena.getArenaMembers().size() <= 1) {
                setGameState(GameState.END, arena);
                resetTimers(arena);
            }
        } else if (gameState == GameState.ACTIVE) {
            if (arena.getArenaMembers().size() <= 1) {
                setGameState(GameState.END, arena);
                resetTimers(arena);
            }
        }
    }

    // check if there are enough players, if so start the lobby countdown
    private void enoughPlayers(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState == GameState.RECRUITING) {
                    if (arena.getArenaMembers().size() >= arena.getArenaConfig().getInt("min-players")) {
                        setGameState(GameState.LOBBY_COUNTDOWN, arena);
                    } else {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // sends the players who are in the arena the notification that the game has endedo
    private void sendGameEndNotification(final Arena arena) {
        arena.getArenaMembers().stream().filter(Objects::nonNull).forEach(mmPlayer -> {
            final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
            if(Utils.getLastPlayer(arena).getRole() == Role.MURDERER) {
                player.sendMessage(Messages.MURDERER_WON.replace("{murderer}", Bukkit.getPlayer(Utils.getLastPlayer(arena).getUuid()).getDisplayName()));
            } else {
                player.sendMessage(Messages.GAME_ENDED
                        .replace("{murderer}", Bukkit.getPlayer(plugin.getArenaManager().getMurderer(arena).getUuid()).getDisplayName())
                .replace("{murdererkiller}", Bukkit.getPlayer(plugin.getArenaManager().getMurderer(arena).getKiller()).getDisplayName()));
            }
        });
    }

    private void resetTimers(final Arena arena) {
        arena.resetLobbyCountdownTimer();
        arena.resetGameTimer();
        arena.resetGraceTimer();
    }

    private void spawnGold(final Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState == GameState.ACTIVE || gameState == GameState.GRACE) {
                    if (arena.getGoldInterval() > 0) {

                        arena.decrementGoldInterval();
                    } else {
                        // spawn the gold
                        arena.getGoldSpotLocations().forEach(goldSpot -> {
                            final Location location = goldSpot.toBukkitLocation();
                            final Location newLoc = location.add(0, 2, 0);
                            newLoc.getWorld().dropItemNaturally(newLoc, new ItemStack(Material.GOLD_INGOT));
                            arena.resetGoldInterval();
                        });
                    }
                } else this.cancel();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void spawnShopNPCs(final Arena arena) {
        /*arena.getShopNPCLocations().forEach(location -> new NPCShop_v_1_16_R3(location.toBukkitLocation()));*/
        arena.getShopNPCLocations().forEach(location -> plugin.getiShopNPC().spawnShopNPC(location.toBukkitLocation()));

    }
    // assigning the roles to the players
    private void assignRoles(final Arena arena) {
        final List<MMPlayer> arenaMemberList = new ArrayList<>(arena.getArenaMembers());
        if (arenaMemberList.size() < 3) {
            arenaMemberList.forEach(mmPlayer -> {
                final Player player = Bukkit.getPlayer(mmPlayer.getUuid());
                player.sendMessage(Utils.colorize("&cCancelled the game, not enough players!"));
                teleportToLobby(player, arena);
            });

            setGameState(GameState.RECRUITING, arena);
        } // shuffling the list so there is a random first and second element, assigning 0 to the murderer, 1 to the detective
        Collections.shuffle(arenaMemberList);
        arenaMemberList.get(0).setRole(Role.MURDERER);
        Bukkit.getPlayer(arenaMemberList.get(0).getUuid()).sendTitle(Utils.colorize("&cYou are the murderer!"), "");
        arenaMemberList.get(1).setRole(Role.DETECTIVE);
        Bukkit.getPlayer(arenaMemberList.get(1).getUuid()).sendTitle(Utils.colorize("&bYou are the detective!"), "");

        arenaMemberList.stream().filter(mmPlayer -> mmPlayer.getRole() == Role.UNASSIGNED).forEach(mmPlayer -> mmPlayer.setRole(Role.INNOCENT));
        arenaMemberList.stream().filter(mmPlayer -> mmPlayer.getRole() == Role.INNOCENT).forEach(mmPlayer -> Bukkit.getPlayer(mmPlayer.getUuid()).sendTitle(Utils.colorize("&aYou are Innocent"), ""));

    }

    public GameState getGameState() {
        return gameState;
    }

    public void giveLobbyInventory(final Player player) {
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
    }

    // give the game inventory to the murderer and detective
    public void giveGameInventory(final Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);

        if (plugin.getMmPlayers().containsKey(player.getUniqueId())) {
            final MMPlayer mmPlayer = plugin.getMmPlayers().get(player.getUniqueId());
            if (mmPlayer.getRole() == Role.MURDERER) {
                player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
            } else if (mmPlayer.getRole() == Role.DETECTIVE) {
                player.getInventory().addItem(new ItemStack(Material.BOW), new ItemStack(Material.ARROW));
            }
        }
    }

    // method to continuously update the detectives inventory
    private void updateDetectiveInventory(final Arena arena) {
        if (gameState == GameState.ACTIVE) {
            arena.getArenaMembers().stream().filter(detective -> detective.getRole() == Role.DETECTIVE).forEach(detective -> {
                final Player player = Bukkit.getPlayer(detective.getUuid());
                if (arena.getNewDetectiveArrow() > 0) {
                    arena.decrementNewDetectiveArrow();
                } else {
                    arena.resetNewDetectiveArrow();
                    player.getInventory().addItem(new ItemStack(Material.ARROW));
                }
            });
        }
    }
}
