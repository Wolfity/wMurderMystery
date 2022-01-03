package me.wolf.wmurdermystery;

import me.wolf.wmurdermystery.arena.Arena;
import me.wolf.wmurdermystery.arena.ArenaManager;
import me.wolf.wmurdermystery.commands.impl.MurderMysteryCommand;
import me.wolf.wmurdermystery.files.FileManager;
import me.wolf.wmurdermystery.game.GameListeners;
import me.wolf.wmurdermystery.game.GameManager;
import me.wolf.wmurdermystery.listeners.*;
import me.wolf.wmurdermystery.player.MMPlayer;
import me.wolf.wmurdermystery.player.PlayerManager;
import me.wolf.wmurdermystery.scoreboard.Scoreboards;
import me.wolf.wmurdermystery.shop.IShopNPC;
import me.wolf.wmurdermystery.shop.ShopEffectManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class MurderMysteryPlugin extends JavaPlugin {


    private ArenaManager arenaManager;
    private GameManager gameManager;
    private Scoreboards scoreboard;
    private FileManager fileManager;
    private ShopEffectManager shopEffectManager;
    private IShopNPC iShopNPC;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        MurderMysteryPlugin plugin = this;
        loadNMS();

        final File folder = new File(plugin.getDataFolder() + "/arenas");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        registerCommands();
        registerListeners();
        registerManagers();

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {

        //clearing out all arenas incase there were ongoing game
        for (final Arena arena : getArenaManager().getArenas()) {
            getArenaManager().clearArena(arena);
        }

    }

    private void registerCommands() {
        Collections.singletonList(
                new MurderMysteryCommand(this)
        ).forEach(this::registerCommand);

    }

    private void registerListeners() {
        Arrays.asList(
                new GameListeners(this),
                new PlayerQuit(this),
                new EntityDamage(this),
                new FoodChange(this),
                new BlockBreak(this),
                new BlockPlace(this),
                new ItemDrop(this)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerManagers() {
        this.fileManager = new FileManager(this);
        this.arenaManager = new ArenaManager(this);
        this.arenaManager.loadArenas();
        this.gameManager = new GameManager(this);
        this.scoreboard = new Scoreboards(this);
        this.shopEffectManager = new ShopEffectManager(this);
        this.playerManager = new PlayerManager();

        shopEffectManager.loadEffects();

    }

    private void registerCommand(final Command command) {
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getLabel(), command);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public Scoreboards getScoreboard() {
        return scoreboard;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public IShopNPC getiShopNPC() {
        return iShopNPC;
    }

    public ShopEffectManager getShopEffectManager() {
        return shopEffectManager;
    }

    private void loadNMS() {
        final String version = getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            iShopNPC = (IShopNPC) Class.forName("me.wolf.wmurdermystery.shop.versions." + version).newInstance();
            System.out.println("&fSuccessfully loaded support for &a" + version + "&f!");
        } catch (Exception ignored) {
        }
    }
}
