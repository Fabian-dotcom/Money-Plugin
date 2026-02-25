package de.fabian.server2026.money.command;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.shop.ShopGUI;
import de.fabian.server2026.money.shop.ShopItem;
import de.fabian.server2026.money.shop.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public class ShopCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final ShopManager shopManager;
    private final ShopGUI shopGUI;

    public ShopCommand(JavaPlugin plugin, EconomyManager economy, PlayerSettingsManager settings, FileConfiguration prices) {
        this.plugin = plugin;
        this.shopManager = new ShopManager(prices);
        this.shopGUI = new ShopGUI(shopManager, economy, settings);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "Keine Berechtigung.");
                return true;
            }

            File pricesFile = new File(plugin.getDataFolder(), "prices.yml");
            FileConfiguration reloaded = YamlConfiguration.loadConfiguration(pricesFile);
            shopManager.reload(reloaded);
            closeOpenShopInventories();
            sender.sendMessage(ChatColor.GREEN + "Shop-Preise wurden neu geladen.");
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("price")) {
            String itemArg = String.join("_", Arrays.copyOfRange(args, 1, args.length));
            Material material;
            try {
                material = Material.valueOf(itemArg.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Unbekanntes Item: " + itemArg);
                return true;
            }

            ShopItem item = shopManager.getByMaterial(material);
            if (item == null) {
                sender.sendMessage(ChatColor.RED + "Fuer dieses Item ist kein Shop-Preis gesetzt.");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + material.name() + ChatColor.GRAY + " kostet "
                    + ChatColor.YELLOW + String.format(Locale.US, "%.2f", item.getPrice()) + " Coins");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler koennen den Shop oeffnen.");
            return true;
        }

        player.openInventory(shopGUI.createCategoryMenu(player));
        return true;
    }

    private void closeOpenShopInventories() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            HumanEntity viewer = player;
            String stripped = ChatColor.stripColor(viewer.getOpenInventory().getTitle()).toLowerCase(Locale.ROOT);
            if (stripped.contains("shop kategorien") || viewer.getOpenInventory().getTitle().startsWith(ChatColor.AQUA.toString()) || stripped.contains("menge")) {
                viewer.closeInventory();
            }
        }
    }

    public ShopGUI getShopGUI() {
        return shopGUI;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }
}
