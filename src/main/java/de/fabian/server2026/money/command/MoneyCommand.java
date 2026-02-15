package de.fabian.server2026.money.command;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.gui.SettingsGUI;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    private final EconomyManager economy;
    private final PlayerSettingsManager settings;

    public MoneyCommand(EconomyManager economy, PlayerSettingsManager settings) {
        this.economy = economy;
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Command nutzen.");
            return true;
        }

        // /money settings
        if (args.length == 1 && args[0].equalsIgnoreCase("settings")) {
            player.openInventory(SettingsGUI.create(player));
            return true;
        }

        // /money
        double money = economy.getMoney(player);
        player.sendMessage("§6💰 Dein Kontostand: §e" + money + " Coins");

        return true;
    }
}
