package me.wolf.wmurdermystery.commands.impl;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.arena.Arena;
import me.wolf.wmurdermystery.commands.BaseCommand;
import me.wolf.wmurdermystery.constants.Messages;
import me.wolf.wmurdermystery.game.GameState;
import me.wolf.wmurdermystery.utils.CustomLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class MurderMysteryCommand extends BaseCommand {

    private final MurderMysteryPlugin plugin;
    private final List<String> firstArg = Arrays.asList("createarena", "deletearena", "setlobby", "setspawn", "setgoldshop", "setnpcshop", "deletenpcshop",
            "deletespawn", "deletegoldspot", "tp");

    public MurderMysteryCommand(final MurderMysteryPlugin plugin) {
        super("mm");
        this.plugin = plugin;
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        final Player player = (Player) sender;

        if (args.length < 1 || args.length > 2) {
            tell(Messages.HELP);
        }
        if (isAdmin()) {
            if (args.length == 1) {
                if (firstArg.contains(args[0])) {
                    tell("&bPlease specify an arena!");
                }
                if (args[0].equalsIgnoreCase("admin")) {
                    tell(Messages.ADMIN_HELP);
                } else if (args[0].equalsIgnoreCase("setworldspawn")) {
                    plugin.getConfig().set("WorldSpawn", player.getLocation());
                    plugin.saveConfig();
                    tell(Messages.SET_WORLD_SPAWN);
                }
            } else if (args.length == 2) {
                final String arenaName = args[1];
                if (args[0].equalsIgnoreCase("createarena")) {
                    if(plugin.getArenaManager().getArena(arenaName) == null) {
                        plugin.getArenaManager().createArena(arenaName);
                        tell(Messages.ARENA_CREATED.replace("{arena}", arenaName));
                    } else tell(Messages.ARENA_EXISTS);

                } else if (args[0].equalsIgnoreCase("deletearena")) {
                    if (plugin.getArenaManager().getArena(arenaName) != null) {
                        plugin.getArenaManager().deleteArena(arenaName);
                        tell(Messages.ARENA_DELETED.replace("{arena}", arenaName));
                    } else tell(Messages.ARENA_NOT_FOUND);
                } else if (args[0].equalsIgnoreCase("setlobby")) {
                    setArenaLobby(player, arenaName);
                } else if (args[0].equalsIgnoreCase("setspawn")) {
                    setGameSpawn(player, arenaName);
                } else if (args[0].equalsIgnoreCase("setgoldspot")) {
                    setGoldSpot(player, arenaName);
                } else if (args[0].equalsIgnoreCase("setnpcshop")) {
                    setNPCShop(player, arenaName);
                } else if (args[0].equalsIgnoreCase("deletenpcshop")) {
                    deleteNPCShop(plugin.getArenaManager().getArena(arenaName), player);
                } else if (args[0].equalsIgnoreCase("deletespawn")) {
                    deleteSpawnPoint(plugin.getArenaManager().getArena(arenaName), player);
                } else if (args[0].equalsIgnoreCase("deletegoldspot")) {
                    deleteGoldSpot(plugin.getArenaManager().getArena(arenaName), player);
                } else if (args[0].equalsIgnoreCase("forcestart")) {
                    plugin.getGameManager().setGameState(GameState.LOBBY_COUNTDOWN, plugin.getArenaManager().getArena(arenaName));
                } else if (args[0].equalsIgnoreCase("tp")) {
                    if (plugin.getArenaManager().getArena(arenaName) != null) {
                        player.teleport(plugin.getArenaManager().getArena(arenaName).getWaitingRoomLoc().toBukkitLocation());
                        tell(Messages.TELEPORTED_TO_ARENA);
                    } else tell(Messages.ARENA_NOT_FOUND);
                }
            }
        }

        if (args.length == 2) {
            final String arenaName = args[1];
            if (args[0].equalsIgnoreCase("join")) {
                if (plugin.getArenaManager().getArena(arenaName) != null) {
                    plugin.getGameManager().addPlayer(player, plugin.getArenaManager().getArena(arenaName));
                } else tell(Messages.ARENA_NOT_FOUND);
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("leave")) {
                plugin.getGameManager().removePlayer(player);
            } else if (args[0].equalsIgnoreCase("join")) {
                tell("&bPlease specify an arena!");
            } else if (args[0].equalsIgnoreCase("help")) {
                tell(Messages.HELP);
            }
        }
    }

    // setting game spawns and saving them in the arena config
    private void setGameSpawn(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) {
            tell(Messages.ARENA_NOT_FOUND);
        }
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (plugin.getArenaManager().isGameActive(arena)) {
            tell(Messages.CAN_NOT_MODIFY);
        }

        arena.getSpawnLocations().add(CustomLocation.fromBukkitLocation(player.getLocation()));
        int i = 1;
        for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getSpawnLocations()) {
            plugin.getArenaManager().getArena(arenaName).getArenaConfig().set("spawn-locations." + i, location.serialize());
            i++;
        }
        tell(Messages.SET_GAME_SPAWN);
        try {
            arena.getArenaConfig().save(arena.getArenaConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method takes care of storing the gold spawn spots in the config
    private void setGoldSpot(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) tell(Messages.ARENA_NOT_FOUND);
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (plugin.getArenaManager().isGameActive(arena)) tell(Messages.CAN_NOT_MODIFY);
        arena.getGoldSpotLocations().add(CustomLocation.fromBukkitLocation(player.getLocation()));
        int i = 1;
        for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getSpawnLocations()) {
            plugin.getArenaManager().getArena(arenaName).getArenaConfig().set("gold-spot-locations." + i, location.serialize());
            i++;
        }
        try {
            arena.getArenaConfig().save(arena.getArenaConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin.getArenaManager().getArena(arenaName).addGoldSpotLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
        tell(Messages.SET_GOLD_SPOT);

    }

    //Creating and saving a gold shop NPC
    private void setNPCShop(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) {
            tell(Messages.ARENA_NOT_FOUND);
        }
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (plugin.getArenaManager().isGameActive(arena)) {
            tell(Messages.CAN_NOT_MODIFY);
        }
        arena.getShopNPCLocations().add(CustomLocation.fromBukkitLocation(player.getLocation()));
        int i = 1;
        for (final CustomLocation location : plugin.getArenaManager().getArena(arenaName).getShopNPCLocations()) {
            plugin.getArenaManager().getArena(arenaName).getArenaConfig().set("shop-npc-locations." + i, location.serialize());
            i++;
        }

        try {
            arena.getArenaConfig().save(arena.getArenaConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        tell(Messages.SET_NPC_SHOP);
        plugin.getArenaManager().getArena(arenaName).addShopNPCLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
    }

    // setting an arena's waiting room lobby location
    private void setArenaLobby(final Player player, final String arenaName) {
        if (plugin.getArenaManager().getArena(arenaName) == null) {
            tell(Messages.ARENA_NOT_FOUND);
        }
        final Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (plugin.getArenaManager().isGameActive(arena)) {
            tell(Messages.CAN_NOT_MODIFY);
        }
        arena.getArenaConfig().set("LobbySpawn", player.getLocation().serialize());
        arena.setWaitingRoomLoc(CustomLocation.fromBukkitLocation(player.getLocation()));
        try {
            arena.getArenaConfig().save(arena.getArenaConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getArenaManager().getArena(arenaName).setWaitingRoomLoc(CustomLocation.fromBukkitLocation(player.getLocation()));
        tell(Messages.SET_LOBBY_SPAWN);
    }

    // checking if the players location is an existing NPC location, if so remove the location
    private void deleteNPCShop(final Arena arena, final Player player) {
        if (!arena.getShopNPCLocations().contains(CustomLocation.fromBukkitLocation(player.getLocation()))) {
            player.sendMessage(Messages.NO_NPC_FOUND);
        }

        arena.getShopNPCLocations().remove(CustomLocation.fromBukkitLocation(player.getLocation()));
        arena.getArenaConfig().set("shop-npc-locations", null); // deleting it so it can be re-written
        int i = 1;
        for (final CustomLocation location : arena.getShopNPCLocations()) {
            arena.getArenaConfig().set("shop-npc-locations." + i, location.serialize());
            i++;
        }

        try {
            arena.getArenaConfig().save(arena.getArenaConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        arena.getShopNPCLocations().remove(CustomLocation.fromBukkitLocation(player.getLocation()));
        tell(Messages.DELETED_NPC_SHOP);

    }

    // method that takes care of deleting spawnpoints
    private void deleteSpawnPoint(final Arena arena, final Player player) {
        if (!arena.getSpawnLocations().contains(CustomLocation.fromBukkitLocation(player.getLocation()))) {
            player.sendMessage(Messages.NO_SPAWN_FOUND);
        }

        arena.getSpawnLocations().remove(CustomLocation.fromBukkitLocation(player.getLocation()));
        arena.getArenaConfig().set("spawn-locations", null); // deleting it so it can be re-written
        int i = 1;
        for (final CustomLocation location : arena.getSpawnLocations()) {
            arena.getArenaConfig().set("spawn-locations." + i, location.serialize());
            i++;
        }

        try {
            arena.getArenaConfig().save(arena.getArenaConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        arena.getSpawnLocations().remove(CustomLocation.fromBukkitLocation(player.getLocation()));
        tell(Messages.DELETED_SPAWN);
    }


    // method that takes care of deleting gold spots
    private void deleteGoldSpot(final Arena arena, final Player player) {
        if (!arena.getGoldSpotLocations().contains(CustomLocation.fromBukkitLocation(player.getLocation()))) {
            player.sendMessage(Messages.NO_GOLD_SPOT_FOUND);
        }
        arena.getGoldSpotLocations().remove(CustomLocation.fromBukkitLocation(player.getLocation()));
        arena.getArenaConfig().set("gold-spot-locations", null); // deleting it so it can be re-written
        int i = 1;
        for (final CustomLocation location : arena.getGoldSpotLocations()) {
            arena.getArenaConfig().set("gold-spot-locations." + i, location.serialize());
            i++;
        }

        try {
            arena.getArenaConfig().save(arena.getArenaConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        arena.getGoldSpotLocations().remove(CustomLocation.fromBukkitLocation(player.getLocation()));
        tell(Messages.DELETED_GOLD_SPOT);
    }
}


