package de.fabian.server2026.money.stats;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SalesStatsManager {

    private final Map<UUID, Double> minute = new HashMap<>();
    private final Map<UUID, Double> hour = new HashMap<>();

    public SalesStatsManager(JavaPlugin plugin) {

        // jede Minute
        Bukkit.getScheduler().runTaskTimer(
                plugin,
                minute::clear,
                1200L,
                1200L
        );

        // jede Stunde
        Bukkit.getScheduler().runTaskTimer(
                plugin,
                hour::clear,
                72000L,
                72000L
        );
    }

    public void add(UUID uuid, double amount) {
        minute.put(uuid, minute.getOrDefault(uuid, 0.0) + amount);
        hour.put(uuid, hour.getOrDefault(uuid, 0.0) + amount);
    }

    public double getMinute(UUID uuid) {
        return minute.getOrDefault(uuid, 0.0);
    }

    public double getHour(UUID uuid) {
        return hour.getOrDefault(uuid, 0.0);
    }
}
