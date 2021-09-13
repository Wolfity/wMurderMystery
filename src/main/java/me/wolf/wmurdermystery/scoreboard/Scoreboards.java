package me.wolf.wmurdermystery.scoreboard;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.arena.Arena;
import me.wolf.wmurdermystery.player.MMPlayer;
import me.wolf.wmurdermystery.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

@SuppressWarnings("ConstantConditions")
public class Scoreboards {

    private final MurderMysteryPlugin plugin;

    public Scoreboards(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    public void lobbyScoreboard(final Player player, final Arena arena) {
        final int maxPlayers = arena.getArenaConfig().getInt("max-players");
        final String name = arena.getName();
        final int currentPlayers = arena.getArenaMembers().size();

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("mm", "mm");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&6&lMM Waiting Room"));

        final Team players = scoreboard.registerNewTeam("players");
        players.addEntry(Utils.colorize("&bPlayers: "));
        players.setPrefix("");
        players.setSuffix(Utils.colorize("&b" + currentPlayers + "&3/&b" + maxPlayers));
        objective.getScore(Utils.colorize("&bPlayers: ")).setScore(1);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(2);

        final Team map = scoreboard.registerNewTeam("map");
        map.addEntry(Utils.colorize("&bMap: &2"));
        map.setPrefix("");
        map.setSuffix(Utils.colorize(name));
        objective.getScore(Utils.colorize("&bMap: &2")).setScore(3);

        final Team empty2 = scoreboard.registerNewTeam("empty2");
        empty2.addEntry("  ");
        empty2.setPrefix("");
        empty2.setSuffix("");
        objective.getScore("  ").setScore(4);


        player.setScoreboard(scoreboard);
    }

    public void gameScoreboard(final Player player, final Arena arena) {
        final int maxPlayers = arena.getArenaConfig().getInt("max-players");
        final String name = arena.getName();
        final int currentPlayers = (int) arena.getArenaMembers().stream().filter(mmPlayer -> !mmPlayer.isSpectator()).count();
        final MMPlayer mmPlayer = plugin.getMmPlayers().get(player.getUniqueId());

        final String roleName = mmPlayer.getRole().getDisplay();

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("mmgame", "mm");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&6&lMurder Mystery"));

        final Team players = scoreboard.registerNewTeam("players");
        players.addEntry(Utils.colorize("&bPlayers: "));
        players.setPrefix("");
        players.setSuffix(Utils.colorize("&b" + currentPlayers + "&3/&b" + maxPlayers));
        objective.getScore(Utils.colorize("&bPlayers: ")).setScore(1);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(2);

        final Team time = scoreboard.registerNewTeam("time");
        time.addEntry(Utils.colorize("&bTime Left: "));
        time.setPrefix("");
        time.setSuffix(Utils.colorize("&3" + arena.getGameTimer()));
        objective.getScore(Utils.colorize("&bTime Left: ")).setScore(3);

        final Team empty2 = scoreboard.registerNewTeam("empty2");
        empty2.addEntry("  ");
        empty2.setPrefix("");
        empty2.setSuffix("");
        objective.getScore("  ").setScore(4);

        final Team kills = scoreboard.registerNewTeam("kills");
        kills.addEntry(Utils.colorize("&bKills: "));
        kills.setPrefix("");
        kills.setSuffix(Utils.colorize("&3" + mmPlayer.getKills()));
        objective.getScore(Utils.colorize("&bKills: ")).setScore(5);


        final Team empty3 = scoreboard.registerNewTeam("empty3");
        empty3.addEntry("   ");
        empty3.setPrefix("");
        empty3.setSuffix("");
        objective.getScore("   ").setScore(6);

        final Team map = scoreboard.registerNewTeam("map");
        map.addEntry(Utils.colorize("&bMap: &2"));
        map.setPrefix("");
        map.setSuffix(Utils.colorize(name));
        objective.getScore(Utils.colorize("&bMap: &2")).setScore(7);

        final Team empty4 = scoreboard.registerNewTeam("empty4");
        empty4.addEntry("    ");
        empty4.setPrefix("");
        empty4.setSuffix("");
        objective.getScore("    ").setScore(8);

        final Team role = scoreboard.registerNewTeam("Role");
        role.addEntry(Utils.colorize("&bRole: "));
        role.setPrefix("");
        role.setSuffix(Utils.colorize(roleName));
        objective.getScore(Utils.colorize("&bRole: ")).setScore(9);


        final Team empty5 = scoreboard.registerNewTeam("empty5");
        empty5.addEntry("     ");
        empty5.setPrefix("");
        empty5.setSuffix("");
        objective.getScore("     ").setScore(10);
        player.setScoreboard(scoreboard);


        final Team empty6 = scoreboard.registerNewTeam("empty6");
        empty6.addEntry("      ");
        empty6.setPrefix("");
        empty6.setSuffix("");
        objective.getScore("      ").setScore(11);
        player.setScoreboard(scoreboard);

    }

}
