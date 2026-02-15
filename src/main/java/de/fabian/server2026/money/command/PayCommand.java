package de.fabian.server2026.money.command;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.test.TestAccount;
import de.fabian.server2026.money.util.MoneyNotifyUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PayCommand implements CommandExecutor {

    private final EconomyManager economy;
    private final PlayerSettingsManager settings;

    public PayCommand(EconomyManager economy, PlayerSettingsManager settings) {
        this.economy = economy;
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player senderPlayer)) return true;

        if (args.length != 2) {
            senderPlayer.sendMessage("§c/pay <Spieler|testSpieler> <Betrag>");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            senderPlayer.sendMessage("§cUngültiger Betrag.");
            return true;
        }

        if (amount <= 0) {
            senderPlayer.sendMessage("§cBetrag muss größer 0 sein.");
            return true;
        }

        if (economy.getMoney(senderPlayer) < amount) {
            senderPlayer.sendMessage("§cNicht genug Geld.");
            return true;
        }

        // 🎯 Ziel bestimmen
        UUID targetUUID;
        String targetName;

        if (args[0].equalsIgnoreCase(TestAccount.NAME)) {
            targetUUID = TestAccount.UUID;
            targetName = TestAccount.NAME;
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                senderPlayer.sendMessage("§cSpieler nicht online.");
                return true;
            }
            targetUUID = target.getUniqueId();
            targetName = target.getName();
        }

        // 💸 Transfer
        economy.addMoney(senderPlayer, -amount);
        economy.addMoney(targetUUID, amount);

        MoneyNotifyUtil.notify(
                senderPlayer,
                settings,
                "§6💸 Gesendet: §e-" + amount + " Coins an §f" + targetName
        );

        // 🤖 testSpieler antwortet automatisch
        if (targetUUID.equals(TestAccount.UUID)) {
            Bukkit.getScheduler().runTaskLater(
                    Bukkit.getPluginManager().getPlugin("Money"),
                    () -> economy.addMoney(senderPlayer, amount),
                    40L
            );
        }

        return true;
    }
}
