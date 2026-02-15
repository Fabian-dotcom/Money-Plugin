package de.fabian.server2026.money.shop;

import org.bukkit.Material;

public class ShopItem {

    private final Material material;
    private final String displayName;
    private final double price;

    public ShopItem(Material material, String displayName, double price) {
        this.material = material;
        this.displayName = displayName;
        this.price = price;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getPrice() {
        return price;
    }
}
