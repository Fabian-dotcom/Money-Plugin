package de.fabian.server2026.money.command;

import de.fabian.server2026.money.bank.Bank;
import de.fabian.server2026.money.bank.BankManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BankCommand implements CommandExecutor {

    private final BankManager bankManager;

    public BankCommand(BankManager bankManager) {
        this.bankManager = bankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler koennen diesen Command nutzen.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            List<Bank> banks = bankManager.getBanksByOwner(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "Deine Bankchests: " + ChatColor.YELLOW + banks.size());

            if (banks.isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "Keine Bankchests registriert.");
                return true;
            }

            for (Bank bank : banks) {
                Location loc = bank.getLocation();
                if (loc.getWorld() == null) {
                    player.sendMessage(ChatColor.DARK_GRAY + "- [unbekannte Welt] "
                            + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
                    continue;
                }

                player.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.AQUA + loc.getWorld().getName()
                        + ChatColor.GRAY + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + "Verwendung: /" + label + " list");
        return true;
    }
}
