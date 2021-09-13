package me.wolf.wmurdermystery.arena;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.player.MMPlayer;
import me.wolf.wmurdermystery.role.Role;
import me.wolf.wmurdermystery.utils.CustomLocation;
import me.wolf.wmurdermystery.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;

import java.io.File;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class ArenaManager {

    private final MurderMysteryPlugin plugin;

    public ArenaManager(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }


    public Arena createArena(final String arenaName) {
        for (final Arena arena : plugin.getArenas())
            if (arena.getName().equalsIgnoreCase(arenaName))
                return getArena(arenaName);

        final Arena arena = new Arena(arenaName, 10, 20, 15, 10 ,300, 3, 10, plugin);
        final World arenaWorld = Bukkit.createWorld(new WorldCreator(arenaName));
        arenaWorld.setAutoSave(false);
        plugin.getArenas().add(arena);
        return arena;
    }

    public void deleteArena(final String name) {
        final Arena arena = getArena(name);
        if (arena == null) return;

        arena.getArenaConfigFile().delete();
        plugin.getArenas().remove(arena);

        Bukkit.getWorld(name).getPlayers().stream().filter(Objects::nonNull).forEach(player -> player.teleport((Location) plugin.getConfig().get("WorldSpawn")));
        final World world = Bukkit.getWorld(name);
        Bukkit.unloadWorld(world, false);
        final File world_folder = new File(plugin.getServer().getWorldContainer() + File.separator + name + File.separator);
        deleteMap(world_folder);

    }
    // clear the arena from possible left over gold, bows, NPC's and invis armor stands
    public void clearArena(final Arena arena) {
        final World world = arena.getGoldSpotLocations().get(0).toBukkitLocation().getWorld(); // getting a spawn location to get the world
        for (final Entity entity : world.getEntities()) {
            if (entity instanceof Item) { // looping through entities to get all the gold ingots to then remove them
                if (((Item) entity).getItemStack().getType() == Material.GOLD_INGOT) {
                    entity.remove();
                } else if (((Item) entity).getItemStack().getType() == Material.BOW) {
                    entity.remove();
                }
            } else if (entity instanceof Villager) {
                if (entity.getCustomName().equalsIgnoreCase(Utils.colorize("&eGold Shop &7(2 gold, 1 random effect!)"))) {
                    entity.remove();
                }
            }
        }
        Bukkit.unloadWorld(arena.getWaitingRoomLoc().toBukkitLocation().getWorld(), false);
        final World arenaWorld = Bukkit.createWorld(new WorldCreator(arena.getName()));
        arenaWorld.setAutoSave(false);
    }

    // get an arena by passing in it's name
    public Arena getArena(final String name) {
        for (final Arena arena : plugin.getArenas())
            if (arena.getName().equalsIgnoreCase(name))
                return arena;

        return null;
    }

    // getting an arena by passing in a player, looping over all arenas to see if the player is in there
    public Arena getArenaByPlayer(final MMPlayer mmPlayer) {
        for (final Arena arena : plugin.getArenas()) {
            if (arena.getArenaMembers().contains(mmPlayer)) {
                return arena;
            }
        }
        return null;
    }

    // getting the murderer
    public MMPlayer getMurderer(final Arena arena) {
        return arena.getArenaMembers().stream().filter(mmPlayer -> mmPlayer.getRole() == Role.MURDERER).findFirst().orElse(null);
    }

    public Arena getFreeArena() {
        return plugin.getArenas().stream().filter(arena -> arena.getArenaState() == ArenaState.READY).findFirst().orElse(null);
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


            arena.setWaitingRoomLoc(CustomLocation.deserialize(arena.getArenaConfig().getString("LobbySpawn")));


            for (final String key : arena.getArenaConfig().getConfigurationSection("spawn-locations").getKeys(false)) {
                arena.addSpawnLocation(CustomLocation.deserialize(arena.getArenaConfig().getString("spawn-locations." + key)));
            }

            for (final String key : arena.getArenaConfig().getConfigurationSection("gold-spot-locations").getKeys(false)) {
                arena.addGoldSpotLocation(CustomLocation.deserialize(arena.getArenaConfig().getString("gold-spot-locations." + key)));
            }

            for(final String key : arena.getArenaConfig().getConfigurationSection("shop-npc-locations").getKeys(false)) {
                arena.addShopNPCLocation(CustomLocation.deserialize(arena.getArenaConfig().getString("shop-npc-locations." + key)));
            }

            Bukkit.getLogger().info("&aLoaded arena &e" + arena.getName());

        }
    }
    public void saveArenas() {
        for (final Arena arena : plugin.getArenas()) {
            arena.saveArena(arena.getName());
        }
    }
    private void deleteMap(final File dir) {
        File[] files = dir.listFiles();

        for (final File file : files) {
            if (file.isDirectory()) {
                this.deleteMap(file);
            }
            file.delete();
        }
        dir.delete();
    }
}
