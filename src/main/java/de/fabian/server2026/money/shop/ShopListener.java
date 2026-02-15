package de.fabian.server2026.money.shop;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.economy.MoneyScoreboard;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.stats.SalesStatsManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopListener implements Listener {

    private final ShopGUI shopGUI;
    private final EconomyManager economy;
    private final PlayerSettingsManager settings;
    private final SalesStatsManager stats; // bleibt für Scoreboard
    private final ShopLogManager shopLog;

    private final Map<UUID, Long> purchaseCooldown = new HashMap<>();
    private static final long PURCHASE_COOLDOWN_MS = 500L;

    public ShopListener(
            ShopGUI shopGUI,
            EconomyManager economy,
            PlayerSettingsManager settings,
            SalesStatsManager stats,
            ShopLogManager shopLog
    ) {
        this.shopGUI = shopGUI;
        this.economy = economy;
        this.settings = settings;
        this.stats = stats;
        this.shopLog = shopLog;
    }

    private boolean checkCooldown(Player player) {
        long now = System.currentTimeMillis();
        long last = purchaseCooldown.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < PURCHASE_COOLDOWN_MS) {
            player.sendMessage(ChatColor.RED + "Zu viele Anfragen, warte einen Moment!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return false;
        }
        purchaseCooldown.put(player.getUniqueId(), now);
        return true;
    }

    private void giveItemOrDrop(Player player, ItemStack item) {
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(stack ->
                    player.getWorld().dropItemNaturally(player.getLocation(), stack)
            );
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        String title = ChatColor.stripColor(e.getView().getTitle());

        // -------------------------
        // Mengen-Auswahl
        // -------------------------
        if (title.startsWith("Wähle Menge für ")) {
            e.setCancelled(true);

            if (!checkCooldown(player)) return;

            if (clicked.getAmount() <= 0) {
                player.sendMessage(ChatColor.RED + "Ungültiger Kauf!");
                return;
            }

            String display = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            int amount;
            try {
                amount = Integer.parseInt(display.split("x")[0].trim());
            } catch (Exception ex) {
                return;
            }

            String wantedName = title.replace("Wähle Menge für ", "").trim();

            ShopItem shopItem = shopGUI.getManager().getCategories().values().stream()
                    .flatMap(Collection::stream)
                    .filter(it -> it.getDisplayName().equalsIgnoreCase(wantedName))
                    .findFirst().orElse(null);

            if (shopItem == null) return;

            double total = shopItem.getPrice() * amount;

            if (economy.getMoney(player) < total) {
                player.sendMessage(ChatColor.RED + "Du hast nicht genug Coins!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            economy.addMoney(player.getUniqueId(), -total);
            giveItemOrDrop(player, new ItemStack(shopItem.getMaterial(), amount));

            shopLog.logPurchase(player.getName(), shopItem.getDisplayName(), amount, total);

            MoneyScoreboard.update(player, economy, settings, stats, false);

            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.sendMessage(ChatColor.GREEN + "Du hast " + amount + "x "
                    + shopItem.getDisplayName() + " für " + total + " Coins gekauft!");

            player.closeInventory();
            return;
        }

        // -------------------------
        // Kategorien
        // -------------------------
        if (title.equals("Shop Kategorien")) {
            e.setCancelled(true);
            String cat = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            player.openInventory(shopGUI.createItemMenu(player, cat));
            return;
        }

        // -------------------------
        // Item-Menü
        // -------------------------
        if (e.getView().getTitle().startsWith(ChatColor.AQUA.toString())) {
            e.setCancelled(true);

            if (clicked.getType() == ShopGUI.BACK || clicked.getType() == ShopGUI.HOME) {
                player.openInventory(shopGUI.createCategoryMenu(player));
                return;
            }

            if (clicked.getAmount() <= 0) {
                player.sendMessage(ChatColor.RED + "Ungültiger Kauf!");
                return;
            }

            String category = ChatColor.stripColor(e.getView().getTitle());
            ShopItem shopItem = shopGUI.getManager().getItems(category).stream()
                    .filter(it -> it.getMaterial() == clicked.getType())
                    .findFirst().orElse(null);

            if (shopItem == null) return;

            if (e.isShiftClick()) {
                AnvilInput.open(player, shopItem, economy, (p, a) -> {});
                return;
            }

            if (!checkCooldown(player)) return;

            double price = shopItem.getPrice();
            if (economy.getMoney(player) < price) {
                player.sendMessage(ChatColor.RED + "Du hast nicht genug Coins!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }

            economy.addMoney(player.getUniqueId(), -price);
            giveItemOrDrop(player, new ItemStack(shopItem.getMaterial(), 1));

            shopLog.logPurchase(player.getName(), shopItem.getDisplayName(), 1, price);

            MoneyScoreboard.update(player, economy, settings, stats, false);

            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.sendMessage(ChatColor.GREEN + "Du hast 1x "
                    + shopItem.getDisplayName() + " für " + price + " Coins gekauft!");
        }
    }
}
