package me.staartvin.plugins.advancedplayerwarping.commands;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.gui.inventory.AWPMenuType;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import me.staartvin.plugins.advancedplayerwarping.permissions.PermissionManager;
import me.staartvin.plugins.advancedplayerwarping.permissions.results.CreateWarpPermissionResult;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpIdentifier;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandsManager implements CommandExecutor, TabExecutor {

    private AdvancedPlayerWarping plugin;

    public CommandsManager(AdvancedPlayerWarping instance) {
        this.plugin = instance;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length < 1) {
            // TODO: Implement opening inventory

            if (commandSender instanceof Player) {
                plugin.getInventoryManager().openInventory(AWPMenuType.MAIN_MENU, (Player) commandSender);
            } else {
                commandSender.sendMessage(Message.COMMANDS_ONLY_PLAYERS_CAN_OPEN_MENUS.getTranslatedMessage());
            }

            return true;
        }

        boolean createWarp;

        String action = strings[0].trim();

        // Check the action.
        if (action.equalsIgnoreCase("create")) {
            createWarp = true;
        } else if (action.equalsIgnoreCase("delete")) {
            createWarp = false;
        } else if (action.equalsIgnoreCase("reload")) {

            if (commandSender instanceof Player && !PermissionManager.canReloadConfigs((Player) commandSender)) {
                commandSender.sendMessage(ChatColor.RED + "You are not allowed to reload the configs.");
                return true;
            }

            // Reload the configs
            plugin.getConfigurationManager().reloadConfig();
            plugin.getLanguageHandler().reloadConfig();

            commandSender.sendMessage(ChatColor.GREEN + "Config files have been reloaded.");

            return true;
        } else {
            commandSender.sendMessage(ChatColor.GOLD + "/warp create (server/public/private) <warp name>");
            commandSender.sendMessage(ChatColor.GOLD + "/warp delete");
            return true;
        }

        if (createWarp) {
            WarpType warpType = WarpType.PRIVATE;
            boolean providedType = false; // Whether the user provided a type

            String warpName = null;

            if (strings.length > 1) {
                String type = strings[1].trim();

                // Find the type of the warp
                if (type.equalsIgnoreCase("server")) {
                    warpType = WarpType.SERVER;
                    providedType = true;
                } else if (type.equalsIgnoreCase("public")) {
                    warpType = WarpType.PUBLIC;
                    providedType = true;
                } else if (type.equalsIgnoreCase("private")) {
                    warpType = WarpType.PRIVATE;
                    providedType = true;
                } else {
                    // No type was provided so assume it's the name of the warp.
                    warpName = type;
                }
            }

            if (strings.length > 2) {
                if (providedType) {
                    // User provided a type, so skip the type.
                    warpName = StringUtils.join(strings, " ", 2, strings.length);
                } else {
                    // User didn't provide a type, so use the first string after the 'create'.
                    warpName = StringUtils.join(strings, " ", 1, strings.length);
                }
            }

            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Message.COMMANDS_ONLY_PLAYERS_CAN_MAKE_WARPS.getTranslatedMessage());
                return true;
            }

            CreateWarpPermissionResult permissionResult =
                    plugin.getPermissionManager().canCreateWarp((Player) commandSender, warpType);

            if (permissionResult == CreateWarpPermissionResult.REACHED_WARP_LIMIT) {
                commandSender.sendMessage(Message.WARPS_NOT_ALLOWED_ANY_MORE_WARPS.getTranslatedMessage(warpType.toString().toLowerCase()));
                return true;
            } else if (permissionResult == CreateWarpPermissionResult.NO_PERMISSION) {
                commandSender.sendMessage(Message.WARPS_NOT_ALLOWED_WARPS.getTranslatedMessage(warpType.toString().toLowerCase()));
                return true;
            }

            if (warpName == null || warpName.trim().equalsIgnoreCase("")) {
                commandSender.sendMessage(Message.COMMANDS_NO_WARP_NAME_PROVIDED.getTranslatedMessage());
                return true;
            }

            UUID uuid = ((Player) commandSender).getUniqueId();

            // Check if the warp already exists.
            if (plugin.getWarpManager().warpExists(new WarpIdentifier(uuid, warpName))) {
                commandSender.sendMessage(Message.COMMANDS_CONFLICTING_WARP_NAME.getTranslatedMessage());
                return true;
            }

            Warp warp = new Warp(warpName, uuid, ((Player) commandSender).getLocation());

            warp.setType(warpType);

            // Create the warp.
            if (plugin.getWarpManager().getWarpStorageProvider().saveWarp(warp)) {
                commandSender.sendMessage(Message.COMMANDS_WARP_CREATED.getTranslatedMessage(warp.getDisplayName()));
            } else {
                commandSender.sendMessage(Message.COMMANDS_WARP_NOT_CREATED.getTranslatedMessage(warp.getDisplayName()));
            }

            return true;
        } else {
            String warpName = null;

            warpName = StringUtils.join(strings, " ", 1, strings.length);

            if (warpName == null || warpName.trim().equalsIgnoreCase("")) {
                commandSender.sendMessage(Message.COMMANDS_NO_WARP_NAME_PROVIDED.getTranslatedMessage());
                return true;
            }

            UUID uuid = ((Player) commandSender).getUniqueId();

            WarpIdentifier identifier = new WarpIdentifier(uuid, warpName);

            // Check if the warp exists.
            if (!plugin.getWarpManager().warpExists(identifier)) {
                commandSender.sendMessage(Message.COMMANDS_NO_WARP_FOUND.getTranslatedMessage());
                return true;
            }

            Warp warpToDelete = plugin.getWarpManager().getWarpByIdentifier(identifier).orElse(null);

            if (!PermissionManager.canEditWarp((Player) commandSender, warpToDelete)) {
                commandSender.sendMessage(Message.PERMISSION_NOT_ALLOWED_DELETE_WARP.getTranslatedMessage());
                return true;
            }

            if (plugin.getWarpManager().getWarpStorageProvider().deleteWarp(identifier)) {
                commandSender.sendMessage(Message.COMMANDS_WARP_REMOVED.getTranslatedMessage(warpName));
            } else {
                commandSender.sendMessage(Message.COMMANDS_WARP_NOT_REMOVED.getTranslatedMessage(warpName));
            }

            return true;
        }
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        // Command format: /warp create (public/private/server) <name>
        // OR: /warp delete <name>


        if (strings.length == 1) {
            return Stream.of("create", "delete", "reload").filter(suggestion -> suggestion.startsWith(strings[0].toLowerCase())).collect(Collectors.toList());
        }

        String type = strings[0];

        if (type.equalsIgnoreCase("create")) {
            if (strings.length == 2) {
                return Stream.of("server", "public", "private").filter(suggestion -> suggestion.startsWith(strings[1])).collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        } else if (type.equalsIgnoreCase("delete")) {

            String typedString = StringUtils.join(strings, "", 1, strings.length);

            return plugin.getWarpManager().getWarpStorageProvider().getAllWarps().stream()
                    .filter(warp -> warp.getDisplayName(false).startsWith(typedString))
                    .map(warp -> warp.getDisplayName(false)).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
