package de.fabian.server2026.money.command;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.shop.ShopGUI;
import de.fabian.server2026.money.shop.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ShopCommand implements CommandExecutor {

    private final ShopManager shopManager;
    private final ShopGUI shopGUI;

    public ShopCommand(EconomyManager economy, PlayerSettingsManager settings, FileConfiguration prices) {
        this.shopManager = new ShopManager(prices);
        this.shopGUI = new ShopGUI(shopManager, economy, settings);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        player.openInventory(shopGUI.createCategoryMenu(player));
        return true;
    }

    public ShopGUI getShopGUI() {
        return shopGUI;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }
}
