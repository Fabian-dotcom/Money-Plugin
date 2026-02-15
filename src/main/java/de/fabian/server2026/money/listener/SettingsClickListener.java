package de.fabian.server2026.money.listener;

import de.fabian.server2026.money.gui.SettingsGUI;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import de.fabian.server2026.money.settings.NotifyType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SettingsClickListener implements Listener {

    private final PlayerSettingsManager settings;

    public SettingsClickListener(PlayerSettingsManager settings) {
        this.settings = settings;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§8⚙ Einstellungen")) return;

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == 11) {
            NotifyType current = settings.getNotify(p.getUniqueId());
            NotifyType next = switch (current) {
                case CHAT -> NotifyType.HOTBAR;
                case HOTBAR -> NotifyType.OFF;
                case OFF -> NotifyType.CHAT;
            };
            settings.setNotify(p.getUniqueId(), next);
            p.openInventory(SettingsGUI.create(p));
        }

        if (e.getSlot() == 15) {
            boolean enabled = settings.isScoreboardEnabled(p.getUniqueId());
            settings.setScoreboardEnabled(p.getUniqueId(), !enabled);
            p.openInventory(SettingsGUI.create(p));
        }
    }
}
