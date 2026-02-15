package de.fabian.server2026.money.economy;

import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.stats.SalesStatsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.UUID;

public class MoneyScoreboard {

    private static final String OBJECTIVE_NAME = "money";

    public static void update(
            Player player,
            EconomyManager economy,
            PlayerSettingsManager settings,
            SalesStatsManager stats
    ) {
        UUID uuid = player.getUniqueId();

        if (!settings.isScoreboardEnabled(uuid)) {
            // entferne Sidebar falls vorhanden
            try {
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            } catch (Exception ignored) {}
            return;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective(
                OBJECTIVE_NAME,
                "dummy",
                "§6💰 Kontoübersicht"
        );
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        double money = economy.getMoney(uuid);
        double minute = stats.getMinute(uuid);
        double hour = stats.getHour(uuid);

        String notify = settings.getNotify(uuid).name().toLowerCase();

        int line = 10;
        obj.getScore("§7────────────").setScore(line--);
        obj.getScore("§eCoins").setScore(line--);
        obj.getScore("§f" + String.format("%.2f", money)).setScore(line--);
        obj.getScore(" ").setScore(line--);
        obj.getScore("§eVerkauf / Min").setScore(line--);
        obj.getScore("§f+" + String.format("%.2f", minute)).setScore(line--);
        obj.getScore("§eVerkauf / Std").setScore(line--);
        obj.getScore("§f+" + String.format("%.2f", hour)).setScore(line--);
        obj.getScore("§7──────────── ").setScore(line--);
        obj.getScore("§eNotify").setScore(line--);
        obj.getScore("§f" + notify).setScore(line--);

        player.setScoreboard(board);
    }
}
