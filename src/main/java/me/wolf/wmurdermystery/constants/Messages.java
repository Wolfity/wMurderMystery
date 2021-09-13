package me.wolf.wmurdermystery.constants;

import me.wolf.wmurdermystery.utils.Utils;

public final class Messages {

    public static final String ADMIN_HELP = Utils.colorize(
            "&7[-------&bMM &cAdmin &bHelp&7-------]\n" +
                    "&b/mm createarena <arena> &7- Creates a new arena \n" +
                    "&b/mm deletearena <arena> &7- Deletes an arena\n" +
                    "&b/mm setworldspawn &7- Sets the world spawn \n" +
                    "&b/mm setlobby <arena> &7- Sets the lobby for the arena \n" +
                    "&b/mm setspawn <arena> &7- Sets a spawn point for the arena\n" +
                    "&b/mm setgoldspot <arena> &7- Sets a gold spawn spot for the arena\n" +
                    "&b/mm setnpcshop <arena> &7- Sets a shop NPC spawn\n" +
                    "&b/mm deletenpcshop <arena> &7- Deletes a shop NPC spawn\n" +
                    "&b/mm deletespawn <arena> &7- Deletes a spawn point\n" +
                    "&b/mm deletegoldspot <arena> &7- Deletes a gold spawn spot\n" +
                    "&b/mm tp <arena> &7- Teleports you to the arena\n" +
                    "&b/mm admin &7- Shows the admin help message\n" +
                    "&7[-------&bMM &cAdmin &bHelp&7-------]");

    public static final String HELP = Utils.colorize(
            "&7[------- &bMM Help &7-------]\n" +
                    "&b/mm join <arena> &7- Join the arena\n" +
                    "&b/mm leave &7- Leaves the arena\n" +
                    "&b/mm help &7- Displays the help command\n" +
                    "&7[------- &bMM Help &7-------]");

    public static final String ARENA_CREATED = Utils.colorize(
            "&aSuccessfully created the arena {arena}");

    public static final String ARENA_DELETED = Utils.colorize(
            "&cSuccessfully deleted the arena {arena}");

    public static final String SET_LOBBY_SPAWN = Utils.colorize(
            "&aSuccessfully set the lobby spawn");

    public static final String SET_WORLD_SPAWN = Utils.colorize(
            "&aSuccessfully set the world spawn");

    public static final String SET_NPC_SHOP = Utils.colorize(
            "&aSuccessfully set an NPC shop spawn point");

    public static final String DELETED_NPC_SHOP = Utils.colorize(
            "&cSuccessfully deleted an NPC spawn location");

    public static final String DELETED_GOLD_SPOT = Utils.colorize(
            "&cSuccessfully deleted a gold spot location");

    public static final String SET_GAME_SPAWN = Utils.colorize(
            "&aSuccessfully set a game spawn");

    public static final String JOINED_ARENA = Utils.colorize(
            "&aSuccessfully joined the arena &2{arena}");

    public static final String LEFT_ARENA = Utils.colorize(
            "&cSuccessfully left the arena &2{arena}");

    public static final String NOT_IN_ARENA = Utils.colorize(
            "&cYou are not in this arena!");

    public static final String ARENA_NOT_FOUND = Utils.colorize(
            "&cThis arena does not exist!");

    public static final String LOBBY_COUNTDOWN = Utils.colorize(
            "&bThe game will start in &3{countdown}&b seconds!");

    public static final String ARENA_IS_FULL = Utils.colorize(
            "&cThis arena is full!");

    public static final String ALREADY_IN_ARENA = Utils.colorize(
            "&cYou are already in this arena!");

    public static final String CAN_NOT_MODIFY = Utils.colorize(
            "&cThis game is going on, you can not modify the arena");

    public static final String ARENA_EXISTS = Utils.colorize(
            "&cThis arena already exists!");

    public static final String TELEPORTED_TO_ARENA = Utils.colorize(
            "&aTeleported to the arena");

    public static final String GAME_IN_PROGRESS = Utils.colorize(
            "&cThis game is in progress");

    public static final String PLAYER_LEFT_GAME = Utils.colorize(
            "&b{player} &ahas left the game!");

    public static final String PLAYER_JOINED_GAME = Utils.colorize(
            "&b{player} &ahas joined the game!");

    public static final String NO_PERMISSION = Utils.colorize(
            "&cSomething went wrong, you do not have the permissions!");


    public static final String GRACE_PERIOD = Utils.colorize("&c[GAME] &eThe grace period has started, the murderer and detective will receive their items in {seconds}  seconds!");

    public static final String GRACE_PERIOD_END = Utils.colorize("&c[GAME] &eThe murderer and detectives have received their items!\n" +
            "&eGood luck!");


    public static final String GAME_STARTED = Utils.colorize(
            "  &a===========================================\n" +
                    "&a=                   &a&lGame Started! \n" +
                    "&a=  Find and kill the murderer before he killed you!!\n" +
                    "&a=                    &2Good luck!\n" +
                    "&a===========================================");

    public static final String GAME_WILL_START_SOON = Utils.colorize(
            "&aYou have been teleported to your places, the game will start soon!");

    public static final String GAME_ENDED = Utils.colorize(
            "&a=======================================\n" +
                    "&a=         &a&lGame Ended!\n\n" +
                    "&a=       &7&lMurderer {murderer} \n" +
                    "&a=  &6Winners: &aInnocents -> Killer: {murdererkiller} \n" +
                    "&a=====================================\n\n\n" +
                    "&2Players will be teleported out in 10 seconds!");

    public static final String MURDERER_WON = Utils.colorize(
            "&a=======================================\n" +
                    "&a=         &a&lGame Ended!\n\n" +
                    "&a=       &7&lMurderer: {murderer} \n" +
                    "&a=        &6Winner: &c&lMurderer \n" +
                    "&a=====================================\n\n\n" +
                    "&2Players will be teleported out in 10 seconds!");

    public static final String SET_GOLD_SPOT = Utils.colorize(
            "&aSuccessfully set a gold spawn point!");

    public static final String CANT_AFFORD_SHOP = Utils.colorize(
            "&cYou can not afford this!");

    public static final String NO_NPC_FOUND = Utils.colorize(
            "&cNo NPC location found to remove \n" +
                    "Make sure you are in the exact x,y,z");

    public static final String DELETED_SPAWN = Utils.colorize("&aSuccessfully deleted a spawn point!");

    public static final String NO_SPAWN_FOUND = Utils.colorize("&cNo spawn point found in this location\n" +
            "Make sure you are in the exact x,y,z!");

    public static final String NO_GOLD_SPOT_FOUND = Utils.colorize("&cNo gold spot found in this location\n" +
            "Make sure you are in the exact x,y,z!");

    public static final String DETECTIVE_DIED = Utils.colorize("&bThe detective died\n" +
            "Pick up the bow a &3{x} {y} {z}");

    private Messages() {

    }

}
