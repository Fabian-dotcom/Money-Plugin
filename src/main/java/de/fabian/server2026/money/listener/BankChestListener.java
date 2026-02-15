package de.fabian.server2026.money.listener;

import de.fabian.server2026.money.Money;
import de.fabian.server2026.money.bank.Bank;
import de.fabian.server2026.money.bank.BankManager;
import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.economy.MoneyScoreboard;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.stats.SalesStatsManager;
import de.fabian.server2026.money.util.BankHologramUtil;
import de.fabian.server2026.money.util.MoneyNotifyUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class BankChestListener implements Listener {

    private final JavaPlugin plugin;
    private final BankManager bankManager;
    private final EconomyManager economy;
    private final FileConfiguration prices;
    private final PlayerSettingsManager settingsManager;
    private final SalesStatsManager stats;

    public BankChestListener(
            Money plugin,
            BankManager bankManager,
            EconomyManager economy,
            FileConfiguration prices,
            PlayerSettingsManager settingsManager,
            SalesStatsManager stats
    ) {
        this.plugin = plugin;
        this.bankManager = bankManager;
        this.economy = economy;
        this.prices = prices;
        this.settingsManager = settingsManager;
        this.stats = stats;
    }

    // Hopper → Barrel
    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        Inventory dest = event.getDestination();
        if (!(dest.getHolder() instanceof BlockState state)) return;
        if (!(state instanceof Barrel)) return;

        Location loc = state.getLocation();
        if (!bankManager.isBank(loc)) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> processInventory(dest, loc), 1L);
    }

    // Spieler schließt Barrel
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof BlockState state)) return;
        if (!(state instanceof Barrel)) return;

        Location loc = state.getLocation();
        if (!bankManager.isBank(loc)) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> processInventory(inv, loc), 1L);
    }

    private void processInventory(Inventory inv, Location loc) {
        Bank bank = bankManager.getBank(loc);
        if (bank == null) return;

        UUID owner = bank.getOwner();
        double totalGiven = 0.0;
        int totalItems = 0;

        ItemStack[] contents = inv.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null) continue;

            Material mat = stack.getType();
            String pricePath = "prices." + mat.name();
            if (!prices.contains(pricePath)) continue;

            int amount = stack.getAmount();
            double price = prices.getDouble(pricePath);
            double total = price * amount;

            economy.addMoney(owner, total);
            stats.add(owner, total);

            totalGiven += total;
            totalItems += amount;

            inv.setItem(i, null);
        }

        if (totalItems > 0) {
            BankHologramUtil.spawnSellHologram(plugin, loc, totalGiven);

            Player ownerPlayer = Bukkit.getPlayer(owner);
            if (ownerPlayer != null && ownerPlayer.isOnline()) {
                String message =
                        "§6💰 Verkauf: §e+"
                                + String.format("%.2f", totalGiven)
                                + " Coins §7(x" + totalItems + ")";

                MoneyNotifyUtil.notify(
                        ownerPlayer,
                        settingsManager,
                        message
                );

                // Scoreboard aktualisieren
                MoneyScoreboard.update(ownerPlayer, economy, settingsManager, stats);
            }
        }
    }
}
