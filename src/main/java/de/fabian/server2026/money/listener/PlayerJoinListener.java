package de.fabian.server2026.money.listener;

import de.fabian.server2026.money.economy.EconomyManager;
import de.fabian.server2026.money.economy.MoneyScoreboard;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.stats.SalesStatsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final EconomyManager economy;
    private final PlayerSettingsManager settings;
    private final SalesStatsManager stats;

    public PlayerJoinListener(EconomyManager economy, PlayerSettingsManager settings, SalesStatsManager stats) {
        this.economy = economy;
        this.settings = settings;
        this.stats = stats;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        MoneyScoreboard.update(event.getPlayer(), economy, settings, stats);
    }
}
