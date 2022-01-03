package me.wolf.wmurdermystery.arena;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.player.MMPlayer;
import me.wolf.wmurdermystery.role.Role;
import me.wolf.wmurdermystery.utils.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class ArenaManager {

    private final MurderMysteryPlugin plugin;
    private final Set<Arena> arenas = new HashSet<>();

    public ArenaManager(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }


    public Arena createArena(final String arenaName) {
        for (final Arena arena : arenas)
            if (arena.getName().equalsIgnoreCase(arenaName))
                return getArena(arenaName);

        final Arena arena =
                new Arena(arenaName, plugin)
                        .setLobbyCountdown(10)
                        .setGraceTimer(20)
                        .setGoldInterval(15)
                        .setNewDetectiveArrow(10)
                        .setGameTimer(10)
                        .setMinPlayers(3)
                        .setMaxPlayers(10);

        arena.createConfig(arenaName);

        arenas.add(arena);
        return arena;
    }

    public void deleteArena(final String name) {
        final Arena arena = getArena(name);
        if (arena == null) return;

        arena.getArenaConfigFile().delete();
        arenas.remove(arena);

    }

    // clear the arena from possible left over gold, bows, NPC's and invis armor stands
    public void clearArena(final Arena arena) {
        arena.getGoldSpotLocations().forEach(customLocation -> Arrays.stream(customLocation.toBukkitLocation().getChunk().getEntities())
                .filter(entity -> entity instanceof Item ||
                        entity instanceof Villager)
                .forEach(Entity::remove));

    }

    // get an arena by passing in it's name
    public Arena getArena(final String name) {
        return arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    // getting an arena by passing in a player, looping over all arenas to see if the player is in there
    public Arena getArenaByPlayer(final MMPlayer mmPlayer) {
        return arenas.stream().filter(arena -> arena.getArenaMembers().contains(mmPlayer)).findFirst().orElse(null);
    }

    // getting the murderer
    public MMPlayer getMurderer(final Arena arena) {
        return arena.getArenaMembers().stream().filter(mmPlayer -> mmPlayer.getRole() == Role.MURDERER).findFirst().orElse(null);
    }

    public Arena getFreeArena() {
        return arenas.stream().filter(arena -> arena.getArenaState() == ArenaState.READY).findFirst().orElse(null);
    }

    public boolean isGameActive(final Arena arena) {
        return arena.getArenaState() == ArenaState.INGAME ||
                arena.getArenaState() == ArenaState.END ||
                arena.getArenaState() == ArenaState.COUNTDOWN;
    }

    public void loadArenas() {
        final File folder = new File(plugin.getDataFolder() + "/arenas");

        if (folder.listFiles() == null) {
            Bukkit.getLogger().info("&3No arenas has been found!");
            return;
        }

        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            final Arena arena = createArena(file.getName().replace(".yml", ""));
            final FileConfiguration cfg = arena.getArenaConfig();
            final int maxPlayers = arena.getArenaConfig().getInt("max-players");
            final int minPlayers = arena.getArenaConfig().getInt("min-players");
            final int gracePeriod = arena.getArenaConfig().getInt("grace-timer");
            final int lobbyCountdown = arena.getArenaConfig().getInt("lobby-countdown");
            final int gameTimer = arena.getArenaConfig().getInt("game-timer");
            final int goldInterval = arena.getArenaConfig().getInt("gold-interval");
            final int detectiveArrow = arena.getArenaConfig().getInt("detective-new-arrow");

            arena.setWaitingRoomLoc(CustomLocation.fromBukkitLocation(new Location(
                    Bukkit.getWorld(cfg.getString("LobbySpawn.world")),
                    cfg.getDouble("LobbySpawn.x"),
                    cfg.getDouble("LobbySpawn.y"),
                    cfg.getDouble("LobbySpawn.z"),
                    (float) cfg.getDouble("LobbySpawn.pitch"),
                    (float) cfg.getDouble("LobbySpawn.yaw"))
            ));


            for (final String key : arena.getArenaConfig().getConfigurationSection("spawn-locations").getKeys(false)) {
                arena.addSpawnLocation(CustomLocation.deserialize(arena.getArenaConfig().getString("spawn-locations." + key)));
            }

            for (final String key : arena.getArenaConfig().getConfigurationSection("gold-spot-locations").getKeys(false)) {
                arena.addGoldSpotLocation(CustomLocation.deserialize(arena.getArenaConfig().getString("gold-spot-locations." + key)));
            }

            for (final String key : arena.getArenaConfig().getConfigurationSection("shop-npc-locations").getKeys(false)) {
                arena.addShopNPCLocation(CustomLocation.deserialize(arena.getArenaConfig().getString("shop-npc-locations." + key)));
            }
            arena.setMaxPlayers(maxPlayers);
            arena.setMinPlayers(minPlayers);
            arena.setGraceTimer(gracePeriod);
            arena.setLobbyCountdown(lobbyCountdown);
            arena.setGameTimer(gameTimer);
            arena.setGoldInterval(goldInterval);
            arena.setNewDetectiveArrow(detectiveArrow);

            Bukkit.getLogger().info("&aLoaded arena &e" + arena.getName());
            System.out.println("TEST + " + arena.getGameTimer());

        }
    }



    public Set<Arena> getArenas() {
        return arenas;
    }
}
