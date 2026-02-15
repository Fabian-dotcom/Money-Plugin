package de.fabian.server2026.money.shop;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.economy.MoneyScoreboard;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.stats.SalesStatsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {

    private final ShopGUI shopGUI;
    private final EconomyManager economy;
    private final PlayerSettingsManager settings;
    private final SalesStatsManager stats;

    public ShopListener(ShopGUI shopGUI, EconomyManager economy, PlayerSettingsManager settings, SalesStatsManager stats) {
        this.shopGUI = shopGUI;
        this.economy = economy;
        this.settings = settings;
        this.stats = stats;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        String invTitle = e.getView().getTitle();

        // Kategorie-Menü
        if (invTitle.equals(ChatColor.GOLD + "Shop Kategorien")) {
            e.setCancelled(true);
            String cat = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            player.openInventory(shopGUI.createItemMenu(player, cat));
        }

        // Item-Menü
        else if (invTitle.startsWith(ChatColor.AQUA.toString())) {
            e.setCancelled(true);

            String category = ChatColor.stripColor(invTitle);
            ShopItem shopItem = shopGUI.getManager().getItems(category).stream()
                    .filter(item -> item.getMaterial() == clicked.getType())
                    .findFirst().orElse(null);


            if (shopItem == null) return;
            if (clicked.getType() == ShopGUI.BACK) {
                e.setCancelled(true);
                player.openInventory(shopGUI.createCategoryMenu(player));
                return;
            }

            if (clicked.getType() == ShopGUI.HOME) {
                e.setCancelled(true);
                player.openInventory(shopGUI.createCategoryMenu(player));
                return;
            }

            if (e.isShiftClick()) {
                e.setCancelled(true);
                AnvilInput.open(player, shopItem, economy, (p, amount) -> {
                    double total = shopItem.getPrice() * amount;

                    if (economy.getMoney(p) < total) {
                        p.sendMessage(ChatColor.RED + "Du hast nicht genug Coins!");
                        return;
                    }

                    economy.addMoney(p.getUniqueId(), -total);

                    p.getInventory().addItem(
                            new ItemStack(shopItem.getMaterial(), amount)
                    );

                    MoneyScoreboard.update(p, economy, settings, stats);
                    p.sendMessage(ChatColor.GREEN + "Du hast " + amount + "x "
                            + shopItem.getDisplayName() + " für " + total + " Coins gekauft!");
                });
            } else {
                // Normaler Klick → 1 kaufen
                double price = shopItem.getPrice();
                if (economy.getMoney(player) >= price) {
                    economy.addMoney(player.getUniqueId(), -price);
                    player.getInventory().addItem(
                            new ItemStack(shopItem.getMaterial(), 1)
                    );
                    MoneyScoreboard.update(player, economy, settings, stats);
                    player.sendMessage(ChatColor.GREEN + "Du hast 1x " + shopItem.getDisplayName() + " für " + price + " Coins gekauft!");
                } else {
                    player.sendMessage(ChatColor.RED + "Du hast nicht genug Coins!");
                }
            }
        }
    }
}
