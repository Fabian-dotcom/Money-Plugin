package de.fabian.server2026.money.shop;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShopManager {

    private final Map<String, List<ShopItem>> categories = new HashMap<>();
    private final Map<Material, ShopItem> byMaterial = new HashMap<>();

    public ShopManager(FileConfiguration prices) {
        reload(prices);
    }

    public void reload(FileConfiguration prices) {
        categories.clear();
        byMaterial.clear();
        loadItems(prices);
    }

    private void loadItems(FileConfiguration prices) {
        if (!prices.isConfigurationSection("prices")) return;

        var section = prices.getConfigurationSection("prices");
        for (String key : section.getKeys(false)) {
            double price = section.getDouble(key);
            Material material;
            try {
                material = Material.valueOf(key);
            } catch (IllegalArgumentException e) {
                continue;
            }

            String category = determineCategory(key);
            ShopItem item = new ShopItem(material, formatName(key), price);

            categories.computeIfAbsent(category, ignored -> new ArrayList<>()).add(item);
            byMaterial.put(material, item);
        }

        categories.values().forEach(list ->
                list.sort(Comparator.comparing(ShopItem::getDisplayName))
        );
    }

    private String determineCategory(String key) {
        if (key.startsWith("RAW_") || key.endsWith("_INGOT") || key.contains("DIAMOND") || key.contains("EMERALD")) {
            return "Rohstoffe";
        } else if (key.startsWith("NETHERITE") || key.equals("ELYTRA") || key.equals("SHULKER_SHELL")) {
            return "High-End";
        } else if (key.startsWith("COAL") || key.startsWith("REDSTONE") || key.startsWith("LAPIS")) {
            return "Mineralien";
        } else if (key.equals("COBBLESTONE") || key.equals("STONE") || key.endsWith("_BLOCK")) {
            return "Baumaterial";
        } else if (key.equals("WHEAT") || key.equals("CARROT") || key.equals("POTATO") || key.equals("BEETROOT") || key.equals("APPLE")) {
            return "Nahrung";
        } else if (key.equals("CACTUS") || key.equals("SUGARCANE") || key.equals("BAMBOO") || key.equals("KELP") || key.equals("NETHER_WART")) {
            return "Farming";
        } else if (key.equals("ROTTEN_FLESH") || key.equals("BONE") || key.equals("STRING") || key.equals("GUNPOWDER")) {
            return "Mob-Drops";
        } else if (key.equals("NETHERRACK") || key.equals("SOUL_SAND") || key.equals("BLACKSTONE") || key.equals("BASALT")) {
            return "Nether";
        } else if (key.equals("END_STONE") || key.equals("CHORUS_FRUIT") || key.equals("PURPUR_BLOCK")) {
            return "End";
        } else if (key.equals("AMETHYST_SHARD") || key.equals("ECHO_SHARD") || key.equals("ANCIENT_DEBRIS")) {
            return "Rare/Special";
        } else {
            return "Sonstiges";
        }
    }

    private String formatName(String key) {
        return key.replace("_", " ").toLowerCase(Locale.ROOT);
    }

    public Map<String, List<ShopItem>> getCategories() {
        return categories;
    }

    public List<ShopItem> getItems(String category) {
        return categories.getOrDefault(category, new ArrayList<>());
    }

    public ShopItem getByMaterial(Material material) {
        return byMaterial.get(material);
    }
}
