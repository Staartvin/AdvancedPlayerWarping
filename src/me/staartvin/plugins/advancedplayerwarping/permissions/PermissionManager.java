package me.staartvin.plugins.advancedplayerwarping.permissions;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.permissions.results.CreateWarpPermissionResult;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Objects;

public class PermissionManager {

    private AdvancedPlayerWarping plugin;

    public PermissionManager(AdvancedPlayerWarping instance) {
        this.plugin = instance;
    }

    public static boolean canEditWarp(Player player, Warp warp) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(warp);

        if (warp.isOwner(player.getUniqueId())) return true;

        WarpPermissions permission;

        if (warp.getType() == WarpType.SERVER) {
            permission = WarpPermissions.EDIT_WARP_SERVER;
        } else if (warp.getType() == WarpType.PUBLIC) {
            permission = WarpPermissions.EDIT_WARP_PUBLIC_OTHERS;
        } else if (warp.getType() == WarpType.PRIVATE) {
            permission = WarpPermissions.EDIT_WARP_PRIVATE_OTHERS;
        } else {
            return false;
        }

        return permission.hasPermission(player);
    }

    public static boolean canEditWarpName(Player player, Warp warp) {
        if (!canEditWarp(player, warp)) return false;

        return WarpPermissions.EDIT_PROPERTIES_WARP_NAME.hasPermission(player);
    }

    public static boolean canEditWarpNameWithColors(Player player, Warp warp) {
        if (!canEditWarpName(player, warp)) return false;

        return WarpPermissions.EDIT_PROPERTIES_WARP_NAME_COLORS.hasPermission(player);
    }

    public static boolean canEditWarpDescription(Player player, Warp warp) {
        if (!canEditWarp(player, warp)) return false;

        return WarpPermissions.EDIT_PROPERTIES_WARP_DESCRIPTION.hasPermission(player);
    }

    public static boolean canEditWarpDescriptionWithColors(Player player, Warp warp) {
        if (!canEditWarpDescription(player, warp)) return false;

        return WarpPermissions.EDIT_PROPERTIES_WARP_DESCRIPTION_COLORS.hasPermission(player);
    }

    public static boolean canEditWarpCost(Player player, Warp warp) {
        if (!canEditWarp(player, warp)) return false;

        return WarpPermissions.EDIT_PROPERTIES_WARP_COST.hasPermission(player);
    }

    public static boolean canEditWarpDestination(Player player, Warp warp) {
        if (!canEditWarp(player, warp)) return false;

        return WarpPermissions.EDIT_PROPERTIES_WARP_DESTINATION.hasPermission(player);
    }

    public static boolean canEditWarpIcon(Player player, Warp warp) {
        if (!canEditWarp(player, warp)) return false;

        return WarpPermissions.EDIT_PROPERTIES_WARP_ICON.hasPermission(player);
    }

    public static boolean canEditWarpEnchantedLook(Player player, Warp warp) {
        if (!canEditWarp(player, warp)) return false;

        return WarpPermissions.EDIT_PROPERTIES_WARP_ICON_ENCHANTED.hasPermission(player);
    }

    public static boolean canEditWarpWhitelist(Player player, Warp warp) {
        if (!canEditWarp(player, warp)) return false;

        return WarpPermissions.EDIT_PROPERTIES_WARP_WHITELIST.hasPermission(player);
    }

    public static boolean canUseWarp(Player player, WarpType type) {
        if (type == WarpType.SERVER) return WarpPermissions.USE_WARP_SERVER.hasPermission(player);
        if (type == WarpType.PUBLIC) return WarpPermissions.USE_WARP_PUBLIC.hasPermission(player);
        if (type == WarpType.PRIVATE) return WarpPermissions.USE_WARP_PRIVATE.hasPermission(player);

        return false;
    }

    public static boolean canSearchWarpsByPlayer(Player player) {
        return WarpPermissions.SEARCH_WARPS_BY_PLAYER.hasPermission(player);
    }

    public static boolean canSearchWarpsByString(Player player) {
        return WarpPermissions.SEARCH_WARPS_BY_STRING.hasPermission(player);
    }

    public CreateWarpPermissionResult canCreateWarp(Player player, WarpType type) {
        if (type == WarpType.SERVER && !WarpPermissions.CREATE_WARP_SERVER.hasPermission(player)) return CreateWarpPermissionResult.NO_PERMISSION;
        if (type == WarpType.PUBLIC && !WarpPermissions.CREATE_WARP_PUBLIC.hasPermission(player)) return CreateWarpPermissionResult.NO_PERMISSION;
        if (type == WarpType.PRIVATE && !WarpPermissions.CREATE_WARP_PRIVATE.hasPermission(player)) return CreateWarpPermissionResult.NO_PERMISSION;

        if (type == WarpType.SERVER) return CreateWarpPermissionResult.ALLOWED;

        // If we are checking a public or private warp, we should do some more checking.
        int createdWarps = 0;
        int limit = 0;

        if (type == WarpType.PRIVATE) {
            createdWarps =
                    plugin.getWarpManager().getWarpStorageProvider().getPrivateWarps(player.getUniqueId()).size();
            limit = this.getAllowedPrivateWarps(player);
        } else if (type == WarpType.PUBLIC) {
            createdWarps = plugin.getWarpManager().getWarpStorageProvider().getPublicWarps(player.getUniqueId()).size();
            limit = this.getAllowedPublicWarps(player);
        }

        System.out.println("Player " + player.getName() + " can create " + limit + " " + type + " warps and currently" +
                        " has " + createdWarps);

        // The player can only create a new warp if the limit has not been reached.

        if (createdWarps < limit) {
            return CreateWarpPermissionResult.ALLOWED;
        } else {
            return CreateWarpPermissionResult.REACHED_WARP_LIMIT;
        }
    }

    public int getAllowedPrivateWarps(Player player) {

        if (WarpPermissions.BYPASS_LIMIT_PRIVATE_WARPS.hasPermission(player)) return Integer.MAX_VALUE;

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {

            String permission = attachmentInfo.getPermission();

            if (permission.startsWith("apw.limit.private.")) {
                return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
            }
        }

        return plugin.getConfigurationManager().getDefaultPrivateWarpLimit();
    }

    public int getAllowedPublicWarps(Player player) {

        if (WarpPermissions.BYPASS_LIMIT_PUBLIC_WARPS.hasPermission(player)) return Integer.MAX_VALUE;

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {

            String permission = attachmentInfo.getPermission();

            if (permission.startsWith("apw.limit.public.")) {
                return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
            }
        }

        return plugin.getConfigurationManager().getDefaultPublicWarpLimit();
    }

    public static boolean canReloadConfigs(Player player) {
        return WarpPermissions.RELOAD_CONFIGS.hasPermission(player);
    }

    public static boolean shouldPayForUsingWarp(Player player, Warp warp) {
        return !warp.isOwner(player.getUniqueId()) || WarpPermissions.BYPASS_ECONOMY_USE_WARPS.hasPermission(player);
    }

    public static boolean shouldWaitForTimeout(Player player) {
        return !WarpPermissions.BYPASS_TIMEOUT.hasPermission(player);
    }

}
