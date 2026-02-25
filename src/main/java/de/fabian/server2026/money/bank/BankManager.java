package de.fabian.server2026.money.bank;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BankManager {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;

    private final Map<String, Bank> banks = new HashMap<>();

    public BankManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "banks.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        loadBanks();
    }

    public void addBank(Bank bank) {
        banks.put(bank.getKey(), bank);

        String path = "banks." + bank.getKey();
        config.set(path + ".owner", bank.getOwner().toString());

        saveFile();
    }

    public void removeBank(Location loc) {
        String key = loc.getWorld().getName() + ";" +
                loc.getBlockX() + ";" +
                loc.getBlockY() + ";" +
                loc.getBlockZ();

        banks.remove(key);
        config.set("banks." + key, null);
        saveFile();
    }

    public boolean isBank(Location loc) {
        String key = loc.getWorld().getName() + ";" +
                loc.getBlockX() + ";" +
                loc.getBlockY() + ";" +
                loc.getBlockZ();

        return banks.containsKey(key);
    }

    public Bank getBank(Location loc) {
        String key = loc.getWorld().getName() + ";" +
                loc.getBlockX() + ";" +
                loc.getBlockY() + ";" +
                loc.getBlockZ();

        return banks.get(key);
    }

    public List<Bank> getBanksByOwner(UUID owner) {
        List<Bank> result = new ArrayList<>();
        for (Bank bank : banks.values()) {
            if (bank.getOwner().equals(owner)) {
                result.add(bank);
            }
        }
        return result;
    }

    private void loadBanks() {
        if (!config.contains("banks")) return;

        for (String key : config.getConfigurationSection("banks").getKeys(false)) {
            String ownerString = config.getString("banks." + key + ".owner");
            UUID owner = UUID.fromString(ownerString);

            String[] parts = key.split(";");
            String world = parts[0];
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);

            Location loc = new Location(Bukkit.getWorld(world), x, y, z);
            banks.put(key, new Bank(owner, loc));
        }
    }

    public Map<String, Bank> getBanks() {
        return banks;
    }

    public Iterable<Bank> getAllBanks() {
        return banks.values();
    }

    private void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
