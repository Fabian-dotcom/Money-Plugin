package de.fabian.server2026.money.command;

import de.fabian.server2026.money.achievement.AchievementManager;
import de.fabian.server2026.money.achievement.AchievementType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class AchievementsCommand implements CommandExecutor {

    private final AchievementManager achievements;

    public AchievementsCommand(AchievementManager achievements) {
        this.achievements = achievements;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler koennen diesen Command nutzen.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "Deine Achievements");

        for (AchievementType type : AchievementType.values()) {
            boolean unlocked = achievements.isUnlocked(player.getUniqueId(), type);
            double progress = achievements.getProgress(player.getUniqueId(), type);
            double target = type.getTarget();

            String status = unlocked ? ChatColor.GREEN + "Freigeschaltet" : ChatColor.YELLOW + String.format(Locale.US, "%.0f/%.0f", progress, target);
            player.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.AQUA + type.getTitle() + ChatColor.GRAY + " | " + status);
            player.sendMessage(ChatColor.GRAY + "  " + type.getDescription());
        }

        return true;
    }
}
