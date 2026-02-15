package de.fabian.server2026.money.shop;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ShopManager {

    private final FileConfiguration prices;
    private final Map<String, List<ShopItem>> categories = new HashMap<>();

    public ShopManager(FileConfiguration prices) {
        this.prices = prices;
        loadItems();
    }

    private void loadItems() {
        // hole die Section "prices"
        if (!prices.isConfigurationSection("prices")) return;
        var section = prices.getConfigurationSection("prices");

        for (String key : section.getKeys(false)) {
            double price = section.getDouble(key);
            Material mat;
            try {
                mat = Material.valueOf(key);
            } catch (IllegalArgumentException e) {
                continue; // ungültiges Material ignorieren
            }

            String category = determineCategory(key);
            ShopItem item = new ShopItem(mat, formatName(key), price);

            categories.computeIfAbsent(category, k -> new ArrayList<>()).add(item);
            System.out.println("Shop Item geladen: " + key + " für " + price + " Coins");
        }

        // alphabetisch sortieren
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
        return key.replace("_", " ").toLowerCase();
    }

    public Map<String, List<ShopItem>> getCategories() {
        return categories;
    }

    public List<ShopItem> getItems(String category) {
        return categories.getOrDefault(category, new ArrayList<>());
    }
}
