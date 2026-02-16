package de.fabian.server2026.money.shop;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

public class ShopLogManager {

    private final JavaPlugin plugin;
    private final File logFile;
    private final Queue<String> buffer = new ConcurrentLinkedQueue<>();

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ShopLogManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logFile = new File(plugin.getDataFolder(), "shop-log.txt");
        ensureLogFileExists();
    }

    public void logPurchase(String player, String item, int amount, double price) {
        String line = "[" + FORMAT.format(LocalDateTime.now()) + "] "
                + player + " kaufte " + amount + "x " + item + " fuer " + price + " Coins";
        buffer.add(line);
    }

    public void flushToDisk() {
        if (buffer.isEmpty()) return;

        try (FileWriter writer = new FileWriter(logFile, true)) {
            while (!buffer.isEmpty()) {
                writer.write(buffer.poll() + System.lineSeparator());
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte shop-log.txt nicht schreiben: " + e.getMessage());
        }
    }

    public File getLogFile() {
        return logFile;
    }

    public boolean reloadLogFile() {
        flushToDisk();
        return ensureLogFileExists();
    }

    public List<String> readLastLines(int amount) {
        flushToDisk();
        if (amount <= 0 || !ensureLogFileExists()) return Collections.emptyList();

        ArrayDeque<String> ring = new ArrayDeque<>(amount);
        try (LineNumberReader reader = new LineNumberReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (ring.size() == amount) {
                    ring.removeFirst();
                }
                ring.addLast(line);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte shop-log.txt nicht lesen: " + e.getMessage());
            return Collections.emptyList();
        }

        return new ArrayList<>(ring);
    }

    public List<String> readFiltered(Predicate<String> predicate, int maxResults) {
        flushToDisk();
        if (maxResults <= 0 || !ensureLogFileExists()) return Collections.emptyList();

        ArrayDeque<String> ring = new ArrayDeque<>(maxResults);
        try (LineNumberReader reader = new LineNumberReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!predicate.test(line)) {
                    continue;
                }
                if (ring.size() == maxResults) {
                    ring.removeFirst();
                }
                ring.addLast(line);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte shop-log.txt nicht filtern: " + e.getMessage());
            return Collections.emptyList();
        }

        return new ArrayList<>(ring);
    }

    private boolean ensureLogFileExists() {
        if (logFile.exists()) {
            return true;
        }
        try {
            File parent = logFile.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            return logFile.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte shop-log.txt nicht anlegen: " + e.getMessage());
            return false;
        }
    }
}
