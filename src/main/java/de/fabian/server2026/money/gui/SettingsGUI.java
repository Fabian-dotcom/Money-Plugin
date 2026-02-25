package de.fabian.server2026.money.gui;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.settings.NotifyType;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;

public class SettingsGUI {

    public static final String TITLE = "§8Einstellungen";

    private static PlayerSettingsManager settings;
    private static EconomyManager economy;
    private static JavaPlugin plugin;

    public static void init(JavaPlugin pluginInstance, PlayerSettingsManager settingsManager, EconomyManager economyManager) {
        plugin = pluginInstance;
        settings = settingsManager;
        economy = economyManager;
    }

    public static Inventory create(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        inv.setItem(11, notifyItem(player));
        inv.setItem(13, profileItem(player));
        inv.setItem(15, scoreboardItem(player));
        inv.setItem(16, bankHologramItem(player));

        return inv;
    }

    private static ItemStack notifyItem(Player p) {
        ItemStack item = new ItemStack(Material.BELL);
        ItemMeta meta = item.getItemMeta();

        NotifyType type = settings.getNotify(p.getUniqueId());

        meta.setDisplayName("§eGeld-Benachrichtigung");
        meta.setLore(List.of(
                "§7Modus:",
                "§6" + type.name(),
                "",
                "§8Klicken zum Wechseln"
        ));

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack profileItem(Player p) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(p);

        String version = plugin.getDescription().getVersion();

        meta.setDisplayName("§6Money Plugin §7v" + version);
        meta.setLore(List.of(
                "§7von §eFabian",
                "",
                "§7Spieler: §f" + p.getName(),
                "§7Kontostand:",
                "§a" + String.format(Locale.US, "%.2f", economy.getMoney(p)) + " Coins"
        ));

        head.setItemMeta(meta);
        return head;
    }

    private static ItemStack scoreboardItem(Player p) {
        ItemStack item = new ItemStack(Material.PAINTING);
        ItemMeta meta = item.getItemMeta();

        boolean enabled = settings.isScoreboardEnabled(p.getUniqueId());

        meta.setDisplayName("§eScoreboard");
        meta.setLore(List.of(
                "§7Status:",
                enabled ? "§aAktiv" : "§cDeaktiviert",
                "",
                "§8Klicken zum Umschalten"
        ));

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack bankHologramItem(Player p) {
        ItemStack item = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = item.getItemMeta();

        boolean enabled = settings.isBankHologramEnabled(p.getUniqueId());

        meta.setDisplayName("§eBank-Hologramm");
        meta.setLore(List.of(
                "§7Verkaufsanzeige ueber Bankchests",
                enabled ? "§aAktiv" : "§cDeaktiviert",
                "",
                "§8Klicken zum Umschalten"
        ));

        item.setItemMeta(meta);
        return item;
    }
}
