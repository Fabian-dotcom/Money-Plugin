package de.fabian.server2026.money.economy;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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


    /* --- UUID-based API --- */

    public double getMoney(UUID uuid) {
        return config.getDouble("players." + uuid.toString() + ".money", 0.0);
    }

    public void addMoney(UUID uuid, double amount) {
        double current = getMoney(uuid);
        config.set("players." + uuid.toString() + ".money", current + amount);
        save();
    }

    public void setMoney(UUID uuid, double amount) {
        config.set("players." + uuid.toString() + ".money", amount);
        save();
    }

    /* --- Convenience overloads --- */

    public double getMoney(Player player) {
        return getMoney(player.getUniqueId());
    }

    public void addMoney(Player player, double amount) {
        addMoney(player.getUniqueId(), amount);
    }

    public void setMoney(Player player, double amount) {
        setMoney(player.getUniqueId(), amount);
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
