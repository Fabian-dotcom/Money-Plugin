package de.fabian.server2026.money.shop;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ShopLogManager {

    private final File logFile;
    private final Queue<String> buffer = new ConcurrentLinkedQueue<>();

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ShopLogManager(JavaPlugin plugin) {
        this.logFile = new File(plugin.getDataFolder(), "shop-log.txt");
    }

    public void logPurchase(String player, String item, int amount, double price) {
        String line = "[" + FORMAT.format(LocalDateTime.now()) + "] "
                + player + " kaufte " + amount + "x " + item + " für " + price + " Coins";
        buffer.add(line);
    }

    public void flushToDisk() {
        if (buffer.isEmpty()) return;

        try (FileWriter writer = new FileWriter(logFile, true)) {
            while (!buffer.isEmpty()) {
                writer.write(buffer.poll() + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
