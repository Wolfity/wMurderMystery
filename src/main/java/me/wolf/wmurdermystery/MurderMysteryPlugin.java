package me.wolf.wmurdermystery;

import me.wolf.wmurdermystery.arena.Arena;
import me.wolf.wmurdermystery.arena.ArenaManager;
import me.wolf.wmurdermystery.commands.impl.MurderMysteryCommand;
import me.wolf.wmurdermystery.files.FileManager;
import me.wolf.wmurdermystery.game.GameListeners;
import me.wolf.wmurdermystery.game.GameManager;
import me.wolf.wmurdermystery.listeners.*;
import me.wolf.wmurdermystery.player.MMPlayer;
import me.wolf.wmurdermystery.scoreboard.Scoreboards;
import me.wolf.wmurdermystery.shop.IShopNPC;
import me.wolf.wmurdermystery.shop.ShopEffectManager;
import me.wolf.wmurdermystery.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class MurderMysteryPlugin extends JavaPlugin {

    private MurderMysteryPlugin plugin;

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private Scoreboards scoreboard;
    private FileManager fileManager;
    private ShopEffectManager shopEffectManager;
    private IShopNPC iShopNPC;

    private final Set<Arena> arenas = new HashSet<>();
    private final Map<UUID, MMPlayer> mmPlayers = new HashMap<>();


    @Override
    public void onEnable() {
        plugin = this;
        if(!loadNMS()) {
            Bukkit.getLogger().info(Utils.colorize("&4No NMS Support for this version... shutting down murder mystery"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
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
        arenaManager.saveArenas();

        //clearing out all arenas incase there were ongoing game
        for (final Arena arena : getArenas()) {
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

    public Set<Arena> getArenas() {
        return arenas;
    }

    public Map<UUID, MMPlayer> getMmPlayers() {
        return mmPlayers;
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

    private boolean loadNMS() {
        final String version = getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            iShopNPC = (IShopNPC) Class.forName("me.wolf.wmurdermystery.shop.versions." + version).newInstance();
            System.out.println("&fSuccessfully loaded support for &a" + version + "&f!");
            return true;
        } catch (Exception ex) {
            System.out.println("&fFailed to load &f. &4MurderMystery &fdoes not support version " + version + ". &cShutting down now...");
            return false;
        }
    }
}
