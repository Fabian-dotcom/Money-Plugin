package de.fabian.server2026.money.listener;

import de.fabian.server2026.money.bank.Bank;
import de.fabian.server2026.money.bank.BankManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BankPlaceListener implements Listener {

    private final BankManager bankManager;

    public BankPlaceListener(BankManager bankManager) {
        this.bankManager = bankManager;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (item.getType() != Material.BARREL) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return;

        if (!ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("🏦 Bankchest")) return;

        Bank bank = new Bank(event.getPlayer().getUniqueId(), event.getBlockPlaced().getLocation());
        bankManager.addBank(bank);

        event.getPlayer().sendMessage(ChatColor.GOLD + "🏦 Bankchest registriert!");
    }
}
