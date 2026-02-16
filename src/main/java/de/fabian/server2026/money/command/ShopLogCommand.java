package de.fabian.server2026.money.command;

import de.fabian.server2026.money.shop.ShopLogManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ShopLogCommand implements CommandExecutor {

    private static final int DEFAULT_LINES = 10;
    private static final int DEFAULT_FILTERED_LINES = 50;
    private static final int MAX_LINES = 200;

    private final ShopLogManager shopLog;

    public ShopLogCommand(ShopLogManager shopLog) {
        this.shopLog = shopLog;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player && !player.isOp()) {
            sender.sendMessage(ChatColor.RED + "Keine Berechtigung.");
            return true;
        }

        if (args.length == 0) {
            sendEntries(sender, shopLog.readLastLines(DEFAULT_LINES), "Letzte " + DEFAULT_LINES + " Shop-Eintraege");
            return true;
        }

        if (args.length == 1) {
            String single = args[0].toLowerCase(Locale.ROOT);

            if (single.equals("reload")) {
                boolean ok = shopLog.reloadLogFile();
                if (ok) {
                    sender.sendMessage(ChatColor.GREEN + "Shop-Log wurde neu geladen.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Shop-Log konnte nicht neu geladen werden.");
                }
                return true;
            }

            Integer requestedLines = parseLines(args[0]);
            if (requestedLines == null) {
                sendUsage(sender, label);
                return true;
            }

            sendEntries(sender, shopLog.readLastLines(requestedLines), "Letzte " + requestedLines + " Shop-Eintraege");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        if (!sub.equals("player") && !sub.equals("item")) {
            sendUsage(sender, label);
            return true;
        }

        String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();
        if (query.isEmpty()) {
            sendUsage(sender, label);
            return true;
        }

        String lowerQuery = query.toLowerCase(Locale.ROOT);
        List<String> filtered;

        if (sub.equals("player")) {
            filtered = shopLog.readFiltered(line -> line.toLowerCase(Locale.ROOT).contains("] " + lowerQuery + " kaufte"), DEFAULT_FILTERED_LINES);
            sendEntries(sender, filtered, "Shop-Eintraege fuer Spieler: " + query + " (max " + DEFAULT_FILTERED_LINES + ")");
            return true;
        }

        filtered = shopLog.readFiltered(line -> line.toLowerCase(Locale.ROOT).contains("x " + lowerQuery + " fuer"), DEFAULT_FILTERED_LINES);
        sendEntries(sender, filtered, "Shop-Eintraege fuer Item: " + query + " (max " + DEFAULT_FILTERED_LINES + ")");
        return true;
    }

    private Integer parseLines(String input) {
        try {
            int parsed = Integer.parseInt(input);
            if (parsed <= 0) {
                return null;
            }
            return Math.min(parsed, MAX_LINES);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void sendEntries(CommandSender sender, List<String> entries, String title) {
        sender.sendMessage(ChatColor.GOLD + title);

        if (entries.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Keine passenden Eintraege gefunden.");
            return;
        }

        for (String line : entries) {
            sender.sendMessage(ChatColor.GRAY + line);
        }
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.RED + "Verwendung:");
        sender.sendMessage(ChatColor.GRAY + "/" + label + "");
        sender.sendMessage(ChatColor.GRAY + "/" + label + " <anzahl>");
        sender.sendMessage(ChatColor.GRAY + "/" + label + " player <name>");
        sender.sendMessage(ChatColor.GRAY + "/" + label + " item <item>");
        sender.sendMessage(ChatColor.GRAY + "/" + label + " reload");
    }
}
