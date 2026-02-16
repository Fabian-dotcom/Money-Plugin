package de.fabian.server2026.money.listener;

import de.fabian.server2026.money.bank.Bank;
import de.fabian.server2026.money.bank.BankManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BankBreakListener implements Listener {

    private final BankManager bankManager;

    public BankBreakListener(BankManager bankManager) {
        this.bankManager = bankManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (!bankManager.isBank(location)) {
            return;
        }

        Bank bank = bankManager.getBank(location);
        if (bank == null) {
            return;
        }

        Player player = event.getPlayer();
        boolean isOwner = bank.getOwner().equals(player.getUniqueId());
        boolean isOp = player.isOp();

        if (!isOwner && !isOp) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Du darfst diese Bankchest nicht abbauen.");
            return;
        }

        bankManager.removeBank(location);
        player.sendMessage(ChatColor.YELLOW + "Bankchest wurde aus dem System entfernt.");
    }
}
