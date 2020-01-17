package me.staartvin.plugins.advancedplayerwarping.teleporting;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import me.staartvin.plugins.advancedplayerwarping.permissions.PermissionManager;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportHandler {

    private AdvancedPlayerWarping plugin;

    private Map<UUID, Long> lastTimeTeleported = new HashMap<>();

    public TeleportHandler(AdvancedPlayerWarping instance) {
        this.plugin = instance;
    }

    public boolean canTeleport(UUID uuid) {
        return getTimeLeft(uuid) <= 0;
    }

    public long getTimeLeft(UUID uuid) {
        if (lastTimeTeleported.containsKey(uuid)) {

            long lastTimeTeleported = this.lastTimeTeleported.get(uuid);

            long timeLeft =
                    plugin.getConfigurationManager().getWarpTimeout() - ((System.currentTimeMillis() - lastTimeTeleported) / 1000);

            if (timeLeft < 0) {
                return 0;
            } else {
                return timeLeft;
            }
        }

        return 0;
    }

    public void teleportPlayer(Player player, Warp warp) {

        // Check if the time-out period has subsided (if we have to).
        if (PermissionManager.shouldWaitForTimeout(player) && !canTeleport(player.getUniqueId())) {
            player.sendMessage(Message.WARPS_WAIT_FOR_TIMEOUT.getTranslatedMessage(getTimeLeft(player.getUniqueId())));
            return;
        }

        // Check for sufficient funds (if the player should pay for the warp).
        if (PermissionManager.shouldPayForUsingWarp(player, warp)
                && plugin.getEconomyManager().isEconomySupported()
                && !plugin.getEconomyManager().withdrawFunds(player.getUniqueId(), warp.getCost())) {
            player.sendMessage(Message.WARPS_INSUFFICIENT_FUNDS.getTranslatedMessage(plugin.getEconomyManager().getMissingFunds(player.getUniqueId(), warp.getCost())));
            return;
        }

        // Teleport the player
        player.teleport(warp.getDestination());

        // Update last update time.
        this.updateLastUpdateTime(player.getUniqueId());
    }

    public void updateLastUpdateTime(UUID uuid) {
        this.lastTimeTeleported.put(uuid, System.currentTimeMillis());
    }
}
