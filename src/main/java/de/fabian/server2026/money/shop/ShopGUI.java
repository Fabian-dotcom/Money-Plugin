package de.fabian.server2026.money.shop;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class ShopGUI {
    public ShopManager getManager() {
        return manager;
    }

    public static final Material BACK = Material.ARROW;
    public static final Material HOME = Material.NETHER_STAR;


    private final ShopManager manager;
    private final EconomyManager economy;
    private final PlayerSettingsManager settings;

    public ShopGUI(ShopManager manager, EconomyManager economy, PlayerSettingsManager settings) {
        this.manager = manager;
        this.economy = economy;
        this.settings = settings;
    }

    // Kategorien-Menü
    public Inventory createCategoryMenu(Player player) {
        Map<String, List<ShopItem>> cats = manager.getCategories();
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Shop Kategorien");

        int slot = 0;
        for (String cat : cats.keySet()) {
            if (slot >= inv.getSize()) break;

            Material icon = Material.PAPER; // Standard Icon
            switch (cat.toLowerCase()) {
                case "rohstoffe" -> icon = Material.IRON_INGOT;
                case "high-end" -> icon = Material.NETHERITE_INGOT;
                case "mineralien" -> icon = Material.DIAMOND;
                case "baumaterial" -> icon = Material.STONE;
                case "nahrung" -> icon = Material.WHEAT;
                case "farming" -> icon = Material.CACTUS;
                case "mob-drops" -> icon = Material.ROTTEN_FLESH;
                case "nether" -> icon = Material.NETHERRACK;
                case "end" -> icon = Material.END_STONE;
                case "rare/special" -> icon = Material.AMETHYST_SHARD;
            }

            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + cat);
            meta.setLore(List.of(ChatColor.YELLOW + "Klicke, um diese Kategorie zu öffnen"));
            item.setItemMeta(meta);

            inv.setItem(slot++, item);
        }

        return inv;
    }

    // Item-Menü für Kategorie
    public Inventory createItemMenu(Player player, String category) {
        List<ShopItem> items = manager.getItems(category);
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.AQUA + category);

        int slot = 0;
        for (ShopItem shopItem : items) {
            ItemStack item = new ItemStack(shopItem.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(shopItem.getDisplayName());
            meta.setLore(List.of(
                    ChatColor.YELLOW + "Preis: " + shopItem.getPrice() + " Coins",
                    ChatColor.GRAY + "Klicke zum Kaufen",
                    ChatColor.GRAY + "> Shift-Klick für Menge"
            ));
            item.setItemMeta(meta);

            inv.setItem(slot++, item);
        }

        // Zurück
        ItemStack back = new ItemStack(BACK);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "Zurück");
        back.setItemMeta(backMeta);
        inv.setItem(45, back);

        // Home
        ItemStack home = new ItemStack(HOME);
        ItemMeta homeMeta = home.getItemMeta();
        homeMeta.setDisplayName(ChatColor.GOLD + "Kategorien");
        home.setItemMeta(homeMeta);
        inv.setItem(49, home);


        return inv;
    }
}
