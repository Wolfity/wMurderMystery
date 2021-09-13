package me.wolf.wmurdermystery.arena;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.player.MMPlayer;
import me.wolf.wmurdermystery.utils.CustomLocation;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Arena {

    private final MurderMysteryPlugin plugin;

    private final String name;
    private ArenaState arenaState = ArenaState.READY;
    private CustomLocation waitingRoomLoc;
    private FileConfiguration arenaConfig;
    private final Set<MMPlayer> arenaMembers = new HashSet<>();
    private final List<CustomLocation> spawnLocations = new ArrayList<>();
    private final List<CustomLocation> goldSpotLocations = new ArrayList<>();
    private final List<CustomLocation> shopNPCLocations = new ArrayList<>();

    private int lobbyCountdown;
    private int gameTimer;
    private final int maxPlayers, minPlayers;
   // private final List<Role> roleList;
    private int graceTimer, goldInterval, newDetectiveArrow;

    public File arenaConfigFile;

    protected Arena(final String name, final int lobbyCountdown, final int graceTimer, final int goldInterval, final int newDetectiveArrow, final int gameTimer, final int minPlayer, final int maxPlayers, final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
        this.name = name;
        createConfig(name);
        this.lobbyCountdown = lobbyCountdown;
        this.gameTimer = gameTimer;
        this.minPlayers = minPlayer;
        this.maxPlayers = maxPlayers;
        this.graceTimer = graceTimer;
        this.goldInterval = goldInterval;
        this.newDetectiveArrow = newDetectiveArrow;
//        this.roleList = Arrays.asList(
//                new Role("Murderer", Utils.colorize("&cMurderer"), 1),
//                new Role("Detective", Utils.colorize("&aDetective"), 1),
//                new Role("Innocent", Utils.colorize("&aInnocent"), 1)
//
//
//        );

    }


    public void saveArena(final String arenaName) {

        arenaConfig.set("LobbySpawn", waitingRoomLoc.serialize());

        final Arena arena = plugin.getArenaManager().getArena(arenaName);

            int i = 1;
            for (final CustomLocation location : arena.getSpawnLocations()) {
                arenaConfig.set("spawn-locations." + i, location.serialize());
                i++;
            }


            int j = 1;
            for (final CustomLocation location : arena.getGoldSpotLocations()) {
                arenaConfig.set("gold-spot-locations." + j, location.serialize());
                j++;
            }


            int k = 1;
            for (final CustomLocation location : arena.getShopNPCLocations()) {
                arenaConfig.set("shop-npc-locations." + k, location.serialize());
                k++;
            }


        try {
            arenaConfig.save(arenaConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfig(final String cfgName) {
        arenaConfigFile = new File(plugin.getDataFolder() + "/arenas", cfgName.toLowerCase() + ".yml");
        arenaConfig = new YamlConfiguration();
        try {
            arenaConfig.load(arenaConfigFile);
            arenaConfig.save(arenaConfigFile);
        } catch (IOException | InvalidConfigurationException ignore) {

        }
        if (!arenaConfigFile.exists()) {
            arenaConfigFile.getParentFile().mkdirs();
            try {
                arenaConfigFile.createNewFile();
                arenaConfig.load(arenaConfigFile);
                arenaConfig.set("min-players", 3);
                arenaConfig.set("max-players", 10);
                arenaConfig.set("lobby-countdown", 10);
                arenaConfig.set("grace-timer", 20);
                arenaConfig.set("game-timer", 300);
                arenaConfig.set("gold-interval", 15);
                arenaConfig.set("detective-new-arrow",10);
                arenaConfig.save(arenaConfigFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public void decrementGameTimer() {
        gameTimer--;
    }

    public void decrementLobbyCountdown() {
        lobbyCountdown--;
    }

    public void resetGameTimer() {
        this.gameTimer = arenaConfig.getInt("game-timer");
    }

    public void resetLobbyCountdownTimer() {
        this.lobbyCountdown = arenaConfig.getInt("lobby-countdown");
    }

    public void decrementGraceTimer() {
        graceTimer--;
    }

    public void resetGraceTimer() {
        this.graceTimer = arenaConfig.getInt("grace-timer");
    }

    public void resetGoldInterval() {this.goldInterval = arenaConfig.getInt("gold-interval");}

    public void decrementGoldInterval() {goldInterval--;}

    public void decrementNewDetectiveArrow() {newDetectiveArrow--;}

    public void resetNewDetectiveArrow() {this.newDetectiveArrow = arenaConfig.getInt("detective-new-arrow");}
    public void addSpawnLocation(final CustomLocation customLocation) {
        if (!spawnLocations.contains(customLocation)) {
            spawnLocations.add(customLocation);
        }
    }

    public void addGoldSpotLocation(final CustomLocation customLocation) {
        if (!goldSpotLocations.contains(customLocation)) {
            goldSpotLocations.add(customLocation);
        }
    }

    public void addShopNPCLocation(final CustomLocation customLocation) {
        if (!shopNPCLocations.contains(customLocation)) {
            shopNPCLocations.add(customLocation);
        }
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return name.equals(arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void setArenaState(final ArenaState arenaState) {
        this.arenaState = arenaState;
    }

    public void setWaitingRoomLoc(final CustomLocation waitingRoomLoc) {
        this.waitingRoomLoc = waitingRoomLoc;
    }

    public String getName() {
        return name;
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public CustomLocation getWaitingRoomLoc() {
        return waitingRoomLoc;
    }

    public FileConfiguration getArenaConfig() {
        return arenaConfig;
    }

    public Set<MMPlayer> getArenaMembers() {
        return arenaMembers;
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public int getGoldInterval() {return goldInterval;}

    public File getArenaConfigFile() {
        return arenaConfigFile;
    }

    public List<CustomLocation> getSpawnLocations() {
        return spawnLocations;
    }

    public List<CustomLocation> getGoldSpotLocations() {
        return goldSpotLocations;
    }



    public int getGraceTimer() {
        return graceTimer;
    }

    public List<CustomLocation> getShopNPCLocations() {
        return shopNPCLocations;
    }

    public int getNewDetectiveArrow() {return newDetectiveArrow;}
}
