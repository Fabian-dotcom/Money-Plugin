package de.fabian.server2026.money.achievement;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AchievementManager {

    private final File file;
    private final YamlConfiguration config;

    public AchievementManager(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "achievements/players.yml");

        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void recordPurchase(UUID uuid, int amount, Player playerIfOnline) {
        int purchases = getPurchases(uuid) + Math.max(amount, 0);
        config.set(path(uuid, "metrics.purchases"), purchases);

        checkUnlock(uuid, AchievementType.FIRST_PURCHASE, purchases, playerIfOnline);
        checkUnlock(uuid, AchievementType.SHOP_MAGNATE, purchases, playerIfOnline);
        save();
    }

    public void recordSales(UUID uuid, double amount, Player playerIfOnline) {
        double totalSales = getTotalSales(uuid) + Math.max(amount, 0.0);
        config.set(path(uuid, "metrics.sales_total"), totalSales);

        checkUnlock(uuid, AchievementType.SALES_100K, totalSales, playerIfOnline);
        save();
    }

    public boolean isUnlocked(UUID uuid, AchievementType type) {
        return config.getBoolean(path(uuid, "unlocked." + type.getKey()), false);
    }

    public double getProgress(UUID uuid, AchievementType type) {
        return switch (type) {
            case FIRST_PURCHASE, SHOP_MAGNATE -> getPurchases(uuid);
            case SALES_100K -> getTotalSales(uuid);
        };
    }

    private int getPurchases(UUID uuid) {
        return config.getInt(path(uuid, "metrics.purchases"), 0);
    }

    private double getTotalSales(UUID uuid) {
        return config.getDouble(path(uuid, "metrics.sales_total"), 0.0);
    }

    private void checkUnlock(UUID uuid, AchievementType type, double progress, Player playerIfOnline) {
        if (isUnlocked(uuid, type)) {
            return;
        }
        if (progress < type.getTarget()) {
            return;
        }

        config.set(path(uuid, "unlocked." + type.getKey()), true);
        if (playerIfOnline != null) {
            playerIfOnline.sendMessage(ChatColor.GOLD + "Achievement freigeschaltet: " + ChatColor.YELLOW + type.getTitle());
            playerIfOnline.sendMessage(ChatColor.GRAY + type.getDescription());
        }
    }

    private String path(UUID uuid, String suffix) {
        return "players." + uuid + "." + suffix;
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
