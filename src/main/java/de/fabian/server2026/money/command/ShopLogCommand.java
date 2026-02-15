package de.fabian.server2026.money.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class ShopLogCommand implements CommandExecutor {

    private final File logFile;

    public ShopLogCommand(File logFile) {
        this.logFile = logFile;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Keine Berechtigung.");
            return true;
        }

        if (!logFile.exists()) {
            player.sendMessage(ChatColor.YELLOW + "Noch keine Shop-Käufe geloggt.");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Shop-Log Datei:");
        player.sendMessage(ChatColor.GRAY + logFile.getAbsolutePath());
        player.sendMessage(ChatColor.GRAY + "(Server-Datei, nicht im Chat lesbar)");

        return true;
    }
}
