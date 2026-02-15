package de.fabian.server2026.money.bank;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class Bank {

    private final UUID owner;
    private final String world;
    private final int x, y, z;

    public Bank(UUID owner, Location loc) {
        this.owner = owner;
        this.world = loc.getWorld().getName();
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        World w = Bukkit.getWorld(world);
        return new Location(w, x, y, z);
    }

    public String getKey() {
        return world + ";" + x + ";" + y + ";" + z;
    }
}
