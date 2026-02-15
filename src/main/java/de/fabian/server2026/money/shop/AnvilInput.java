package de.fabian.server2026.money.shop;

import de.fabian.server2026.money.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

public class AnvilInput {

    // feste Auswahlmöglichkeiten
    private static final int[] AMOUNTS = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096};

    /**
     * Öffnet ein Auswahl-Menü für die Anzahl.
     * WICHTIG: der eigentliche Kauf wird vom ShopListener im InventoryClickEvent verarbeitet.
     */
    public static void open(Player player, ShopItem item, EconomyManager economy, BiConsumer<Player, Integer> callback) {
        int size = 9;
        String title = "Wähle Menge für " + item.getDisplayName();
        Inventory inv = Bukkit.createInventory(null, size, ChatColor.GREEN + title);

        for (int i = 0; i < AMOUNTS.length && i < size; i++) {
            int amount = AMOUNTS[i];

            ItemStack stack = new ItemStack(item.getMaterial());
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW.toString() + amount + "x " + item.getDisplayName());
            meta.setLore(List.of(
                    ChatColor.AQUA + "Preis: " + (item.getPrice() * amount) + " Coins",
                    ChatColor.GRAY + "Klicke um diese Menge zu kaufen"
            ));
            stack.setItemMeta(meta);

            inv.setItem(i, stack);
        }

        player.openInventory(inv);

        // Kauf-Logik wird über den Callback im Listener ausgeführt
    }

}
