package de.fabian.server2026.money.economy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    private final File file;
    private final YamlConfiguration config;

    public EconomyManager(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "money.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean exists(UUID uuid) {
        return config.contains("players." + uuid + ".money");
    }

    public double getMoney(UUID uuid) {
        return config.getDouble("players." + uuid + ".money", 0.0);
    }

    public void addMoney(UUID uuid, double amount) {
        double current = getMoney(uuid);
        config.set("players." + uuid + ".money", current + amount);
        save();
    }

    public void setMoney(UUID uuid, double amount) {
        config.set("players." + uuid + ".money", amount);
        save();
    }

    public double getMoney(Player player) {
        return getMoney(player.getUniqueId());
    }

    public void addMoney(Player player, double amount) {
        addMoney(player.getUniqueId(), amount);
    }

    public void setMoney(Player player, double amount) {
        setMoney(player.getUniqueId(), amount);
    }

    public List<Map.Entry<UUID, Double>> getTopBalances(int limit) {
        if (limit <= 0) {
            return List.of();
        }

        ConfigurationSection players = config.getConfigurationSection("players");
        if (players == null) {
            return List.of();
        }

        List<Map.Entry<UUID, Double>> entries = new ArrayList<>();
        for (String key : players.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double money = players.getDouble(key + ".money", 0.0);
                entries.add(Map.entry(uuid, money));
            } catch (IllegalArgumentException ignored) {
                // ignore broken keys
            }
        }

        entries.sort(Comparator.comparingDouble(Map.Entry<UUID, Double>::getValue).reversed());
        if (entries.size() <= limit) {
            return entries;
        }
        return new ArrayList<>(entries.subList(0, limit));
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
