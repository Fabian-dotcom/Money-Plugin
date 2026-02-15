package de.fabian.server2026.money;

import de.fabian.server2026.money.bank.BankManager;
import de.fabian.server2026.money.command.GiveBankCommand;
import de.fabian.server2026.money.command.MoneyCommand;
import de.fabian.server2026.money.command.MoneySettingsCommand;
import de.fabian.server2026.money.command.PayCommand;
import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.economy.MoneyScoreboard;
import de.fabian.server2026.money.gui.SettingsGUI;
import de.fabian.server2026.money.listener.BankChestListener;
import de.fabian.server2026.money.listener.BankPlaceListener;
import de.fabian.server2026.money.listener.PlayerJoinListener;
import de.fabian.server2026.money.listener.SettingsClickListener;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.shop.AnvilInput;
import de.fabian.server2026.money.shop.ShopLogManager;
import de.fabian.server2026.money.stats.SalesStatsManager;
import de.fabian.server2026.money.command.ShopCommand;
import de.fabian.server2026.money.shop.ShopListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;

import java.io.File;

public final class Money extends JavaPlugin {

    private BankManager bankManager;
    private EconomyManager economy;
    private PlayerSettingsManager settingsManager;
    private SalesStatsManager stats;

    private ShopLogManager shopLog;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("prices.yml", false);

        // Manager
        bankManager = new BankManager(this);
        economy = new EconomyManager(this);
        settingsManager = new PlayerSettingsManager(this);
        stats = new SalesStatsManager(this);

        shopLog = new ShopLogManager(this);

        // GUI initialisieren
        SettingsGUI.init(this, settingsManager, economy);

        // Preise laden
        File pricesFile = new File(getDataFolder(), "prices.yml");
        FileConfiguration prices = YamlConfiguration.loadConfiguration(pricesFile);

        // Shop Command
        ShopCommand shopCommand = new ShopCommand(economy, settingsManager, prices);
        getCommand("shop").setExecutor(shopCommand);

        ShopLogManager shopLog = new ShopLogManager(this);

        getServer().getPluginManager().registerEvents(
                new ShopListener(
                        shopCommand.getShopGUI(),
                        economy,
                        settingsManager,
                        stats,
                        shopLog
                ),
                this
        );




        // Scoreboard Auto-Refresh (alle 60 Sekunden)
        Bukkit.getScheduler().runTaskTimer(
                this,
                () -> Bukkit.getOnlinePlayers().forEach(player ->
                        MoneyScoreboard.update(
                                player,
                                economy,
                                settingsManager,
                                stats
                        )
                ),
                20L * 60, // Start nach 60 Sekunden
                20L * 30  // Wiederholung alle 30 Sekunden
        );

        Bukkit.getScheduler().runTaskTimer(
                this,
                shopLog::flushToDisk,
                20L * 60,
                20L * 60
        );

        // Commands
        getCommand("money").setExecutor(
                new MoneyCommand(economy, settingsManager)
        );
        getCommand("givebank").setExecutor(
                new GiveBankCommand(this)
        );
        getCommand("moneysettings").setExecutor(
                new MoneySettingsCommand(settingsManager)
        );

        getCommand("pay").setExecutor(
                new PayCommand(economy, settingsManager)
        );

        // Listener
        getServer().getPluginManager().registerEvents(
                new BankPlaceListener(bankManager), this
        );

        getServer().getPluginManager().registerEvents(
                new SettingsClickListener(settingsManager), this
        );

        getServer().getPluginManager().registerEvents(
                new BankChestListener(
                        this,
                        bankManager,
                        economy,
                        prices,
                        settingsManager,
                        stats
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new PlayerJoinListener(economy, settingsManager, stats),
                this
        );



        getLogger().info("Money Plugin aktiviert!");
    }

    public EconomyManager getEconomy() {
        return economy;
    }
}
