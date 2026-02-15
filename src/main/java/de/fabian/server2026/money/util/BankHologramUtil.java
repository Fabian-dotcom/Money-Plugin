package de.fabian.server2026.money.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

public class BankHologramUtil {

    public static void spawnSellHologram(JavaPlugin plugin, Location baseLoc, double amount) {
        Location loc = baseLoc.clone().add(0.5, 1.5, 0.5);

        ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class, as -> {
            as.setInvisible(true);
            as.setMarker(true);
            as.setGravity(false);
            as.setCustomNameVisible(true);
            as.setCustomName("§6💰 +" + String.format("%.2f", amount) + " Coins");
        });

        // nach 2 Sekunden entfernen
        Bukkit.getScheduler().runTaskLater(plugin, stand::remove, 40L);
    }
}
