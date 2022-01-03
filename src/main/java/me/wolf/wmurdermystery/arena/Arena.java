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
    private final Set<MMPlayer> arenaMembers = new HashSet<>();
    private final List<CustomLocation> spawnLocations = new ArrayList<>();
    private final List<CustomLocation> goldSpotLocations = new ArrayList<>();
    private final List<CustomLocation> shopNPCLocations = new ArrayList<>();
    public File arenaConfigFile;
    private int maxPlayers, minPlayers;
    private ArenaState arenaState = ArenaState.READY;
    private CustomLocation waitingRoomLoc;
    private FileConfiguration arenaConfig;
    private int lobbyCountdown;
    private int gameTimer;
    private int graceTimer, goldInterval, newDetectiveArrow;

    protected Arena(final String name, final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
        this.name = name;
    }

    public void createConfig(final String cfgName) {
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
                arenaConfig.set("detective-new-arrow", 10);
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

    public void resetGoldInterval() {
        this.goldInterval = arenaConfig.getInt("gold-interval");
    }

    public void decrementGoldInterval() {
        goldInterval--;
    }

    public void decrementNewDetectiveArrow() {
        newDetectiveArrow--;
    }

    public void resetNewDetectiveArrow() {
        this.newDetectiveArrow = arenaConfig.getInt("detective-new-arrow");
    }

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

    public String getName() {
        return name;
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public void setArenaState(final ArenaState arenaState) {
        this.arenaState = arenaState;
    }

    public CustomLocation getWaitingRoomLoc() {
        return waitingRoomLoc;
    }

    public void setWaitingRoomLoc(final CustomLocation waitingRoomLoc) {
        this.waitingRoomLoc = waitingRoomLoc;
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

    public Arena setLobbyCountdown(int lobbyCountdown) {
        this.lobbyCountdown = lobbyCountdown;
        return this;
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public Arena setGameTimer(int gameTimer) {
        this.gameTimer = gameTimer;
        return this;
    }

    public int getGoldInterval() {
        return goldInterval;
    }

    public Arena setGoldInterval(int goldInterval) {
        this.goldInterval = goldInterval;
        return this;
    }

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

    public Arena setGraceTimer(int graceTimer) {
        this.graceTimer = graceTimer;
        return this;
    }

    public List<CustomLocation> getShopNPCLocations() {
        return shopNPCLocations;
    }

    public int getNewDetectiveArrow() {
        return newDetectiveArrow;
    }

    public Arena setNewDetectiveArrow(int newDetectiveArrow) {
        this.newDetectiveArrow = newDetectiveArrow;
        return this;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public Arena setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        return this;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Arena setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }
}
