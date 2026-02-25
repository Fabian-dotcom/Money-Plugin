package de.fabian.server2026.money.settings;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerSettingsManager {

    private final File file;
    private final YamlConfiguration config;

    public PlayerSettingsManager(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "settings.yml");
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

    public NotifyType getNotify(UUID uuid) {
        String s = config.getString("players." + uuid + ".notify", NotifyType.HOTBAR.name());
        try {
            return NotifyType.valueOf(s);
        } catch (IllegalArgumentException e) {
            return NotifyType.HOTBAR;
        }
    }

    public void setNotify(UUID uuid, NotifyType type) {
        config.set("players." + uuid + ".notify", type.name());
        save();
    }

    public boolean isScoreboardEnabled(UUID uuid) {
        return config.getBoolean("players." + uuid + ".scoreboard", true);
    }

    public void setScoreboardEnabled(UUID uuid, boolean enabled) {
        config.set("players." + uuid + ".scoreboard", enabled);
        save();
    }

    public boolean isBankHologramEnabled(UUID uuid) {
        return config.getBoolean("players." + uuid + ".bank_hologram", true);
    }

    public void setBankHologramEnabled(UUID uuid, boolean enabled) {
        config.set("players." + uuid + ".bank_hologram", enabled);
        save();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
