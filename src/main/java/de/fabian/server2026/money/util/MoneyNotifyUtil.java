package de.fabian.server2026.money.util;

import de.fabian.server2026.money.settings.NotifyType;
import de.fabian.server2026.money.settings.PlayerSettingsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class MoneyNotifyUtil {

    public static void notify(
            Player player,
            PlayerSettingsManager settings,
            String message
    ) {
        NotifyType type = settings.getNotify(player.getUniqueId());

        switch (type) {
            case CHAT -> player.sendMessage(message);
            case HOTBAR -> player.sendActionBar(Component.text(message));
            case OFF -> {
                // nichts
            }
        }
    }
}
