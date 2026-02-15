package de.fabian.server2026.money.command;

import de.fabian.server2026.money.Money;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GiveBankCommand implements CommandExecutor {

    private final Money plugin;

    public GiveBankCommand(Money plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        if (!player.hasPermission("money.givebank")) {
            player.sendMessage(ChatColor.RED + "Keine Berechtigung.");
            return true;
        }

        ItemStack bankBlock = new ItemStack(Material.BARREL);
        ItemMeta meta = bankBlock.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "🏦 Bankchest");
        meta.setLore(List.of(
                ChatColor.GRAY + "Platziere diese Kiste",
                ChatColor.GRAY + "um Items automatisch zu verkaufen."
        ));

        bankBlock.setItemMeta(meta);

        player.getInventory().addItem(bankBlock);
        player.sendMessage(ChatColor.GREEN + "Du hast eine Bankchest erhalten!");

        return true;
    }
}
