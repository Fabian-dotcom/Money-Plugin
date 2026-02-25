package de.fabian.server2026.money.achievement;

public enum AchievementType {
    FIRST_PURCHASE("first_purchase", "Erster Einkauf", "Kaufe zum ersten Mal im Shop", 1),
    SALES_100K("sales_100k", "100.000 Coins Umsatz", "Erreiche 100000 Coins Umsatz ueber Bankchests", 100000),
    SHOP_MAGNATE("shop_magnate", "Shop-Magnat", "Fuehre 250 Shop-Einkaeufe aus", 250);

    private final String key;
    private final String title;
    private final String description;
    private final double target;

    AchievementType(String key, String title, String description, double target) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.target = target;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getTarget() {
        return target;
    }
}
