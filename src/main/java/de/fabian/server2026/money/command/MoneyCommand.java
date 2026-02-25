package de.fabian.server2026.money.command;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.gui.SettingsGUI;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MoneyCommand implements CommandExecutor {

    private final EconomyManager economy;
    private final PlayerSettingsManager settings;

    public MoneyCommand(EconomyManager economy, PlayerSettingsManager settings) {
        this.economy = economy;
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("top")) {
            List<Map.Entry<UUID, Double>> top = economy.getTopBalances(5);
            sender.sendMessage(ChatColor.GOLD + "Top 5 Kontostaende");

            if (top.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "Noch keine Kontostaende vorhanden.");
                return true;
            }

            int rank = 1;
            for (Map.Entry<UUID, Double> entry : top) {
                OfflinePlayer offline = Bukkit.getOfflinePlayer(entry.getKey());
                String name = offline.getName() != null ? offline.getName() : entry.getKey().toString().substring(0, 8);
                sender.sendMessage(ChatColor.GRAY + "#" + rank + " " + ChatColor.YELLOW + name + ChatColor.DARK_GRAY + " - "
                        + ChatColor.GREEN + String.format(Locale.US, "%.2f", entry.getValue()) + " Coins");
                rank++;
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler koennen diesen Command nutzen.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("settings")) {
            player.openInventory(SettingsGUI.create(player));
            return true;
        }

        double money = economy.getMoney(player);
        player.sendMessage("§6Dein Kontostand: §e" + String.format(Locale.US, "%.2f", money) + " Coins");

        return true;
    }
}
