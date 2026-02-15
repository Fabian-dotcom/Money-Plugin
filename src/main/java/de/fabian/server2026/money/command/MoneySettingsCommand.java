package de.fabian.server2026.money.command;

import de.fabian.server2026.money.gui.SettingsGUI;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneySettingsCommand implements CommandExecutor {

    private final PlayerSettingsManager settings;

    public MoneySettingsCommand(PlayerSettingsManager settings) {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        player.openInventory(SettingsGUI.create(player));
        return true;
    }
}
