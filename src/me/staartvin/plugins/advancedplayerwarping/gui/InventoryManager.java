package me.staartvin.plugins.advancedplayerwarping.gui;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.config.ConfigurationManager;
import me.staartvin.plugins.advancedplayerwarping.config.MenuIcon;
import me.staartvin.plugins.advancedplayerwarping.conversations.ConversationCallback;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.conversations.editwarp.*;
import me.staartvin.plugins.advancedplayerwarping.conversations.filterwarps.RequestPlayerNamePrompt;
import me.staartvin.plugins.advancedplayerwarping.conversations.filterwarps.RequestStringPrompt;
import me.staartvin.plugins.advancedplayerwarping.gui.filter.WarpFilter;
import me.staartvin.plugins.advancedplayerwarping.gui.filter.WarpFilterKeys;
import me.staartvin.plugins.advancedplayerwarping.gui.inventory.*;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import me.staartvin.plugins.advancedplayerwarping.permissions.PermissionManager;
import me.staartvin.plugins.advancedplayerwarping.permissions.results.CreateWarpPermissionResult;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpIcon;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InventoryManager {

    private AdvancedPlayerWarping plugin;
    private ConfigurationManager config;

    public InventoryManager(AdvancedPlayerWarping instance) {
        this.plugin = instance;
        this.config = instance.getConfigurationManager();

        PaginatedGUI.prepare(instance);
    }

    public void openInventory(AWPMenuType type, Player player) {
        PaginatedGUI gui = null;

        if (type == AWPMenuType.MAIN_MENU) {
            gui = getInventoryMainMenu(player);
        } else if (type == AWPMenuType.SERVER_WARPS_MENU) {
            gui = getInventoryServerWarpsMenu();
        } else if (type == AWPMenuType.PUBLIC_WARPS_MENU) {
            gui = getInventoryPublicWarpsMenu();
        } else if (type == AWPMenuType.PRIVATE_WARPS_MENU) {
            gui = getInventoryAccessiblePrivateWarpsMenu(player.getUniqueId());
        } else if (type == AWPMenuType.OWNED_WARPS_MENU) {
            gui = getInventoryOwnedWarps(player.getUniqueId());
        }

        if (gui == null) return;

        player.openInventory(gui.getInventory());
    }

    private PaginatedGUI getInventoryMainMenu(Player player) {

        PaginatedGUI ui = new PaginatedGUI(config.getMenuTitle(AWPMenuType.MAIN_MENU));

        if (config.isIconEnabled(MenuIcon.MAIN_MENU_SERVER_WARPS)
                && PermissionManager.canUseWarp(player, WarpType.SERVER)) {
            ui.setButton(10, new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.MAIN_MENU_SERVER_WARPS))
                    .amount(1).name(config.getIconTitle(MenuIcon.MAIN_MENU_SERVER_WARPS))
                    .lore(config.getIconDescription(MenuIcon.MAIN_MENU_SERVER_WARPS)).build(), event -> {
                this.openInventory(AWPMenuType.SERVER_WARPS_MENU, (Player) event.getWhoClicked());
            }));
        }

        if (config.isIconEnabled(MenuIcon.MAIN_MENU_PUBLIC_WARPS) &&
                PermissionManager.canUseWarp(player, WarpType.PUBLIC)) {
            ui.setButton(11, new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.MAIN_MENU_PUBLIC_WARPS))
                    .amount(1).name(config.getIconTitle(MenuIcon.MAIN_MENU_PUBLIC_WARPS))
                    .lore(config.getIconDescription(MenuIcon.MAIN_MENU_PUBLIC_WARPS)).build(), event -> {
                this.openInventory(AWPMenuType.PUBLIC_WARPS_MENU, (Player) event.getWhoClicked());
            }));
        }

        if (config.isIconEnabled(MenuIcon.MAIN_MENU_ACCESSIBLE_PRIVATE_WARPS)) {
            ui.setButton(12,
                    new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.MAIN_MENU_ACCESSIBLE_PRIVATE_WARPS))
                            .amount(1).name(config.getIconTitle(MenuIcon.MAIN_MENU_ACCESSIBLE_PRIVATE_WARPS))
                            .lore(config.getIconDescription(MenuIcon.MAIN_MENU_ACCESSIBLE_PRIVATE_WARPS)).build(),
                            event -> {
                                this.openInventory(AWPMenuType.PRIVATE_WARPS_MENU, (Player) event.getWhoClicked());
                            }));
        }

        if (config.isIconEnabled(MenuIcon.MAIN_MENU_OWNED_WARPS)) {
            ui.setButton(13, new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.MAIN_MENU_OWNED_WARPS))
                    .amount(1).name(config.getIconTitle(MenuIcon.MAIN_MENU_OWNED_WARPS))
                    .lore(config.getIconDescription(MenuIcon.MAIN_MENU_OWNED_WARPS)).build(), event -> {
                this.openInventory(AWPMenuType.OWNED_WARPS_MENU, (Player) event.getWhoClicked());
            }));
        }

        if (config.isIconEnabled(MenuIcon.MAIN_MENU_SEARCH_WARPS_BY_PLAYER)
                && PermissionManager.canSearchWarpsByPlayer(player)) {
            ui.setButton(14,
                    new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.MAIN_MENU_SEARCH_WARPS_BY_PLAYER))
                            .amount(1).name(config.getIconTitle(MenuIcon.MAIN_MENU_SEARCH_WARPS_BY_PLAYER))
                            .lore(config.getIconDescription(MenuIcon.MAIN_MENU_SEARCH_WARPS_BY_PLAYER)).build(),
                            event -> {
                                event.setCancelled(true);
                                event.getWhoClicked().closeInventory();

                                // Request a player name to filter on.
                                PluginConversation conversation =
                                        PluginConversation.fromFirstPrompt(new RequestPlayerNamePrompt());

                                conversation.afterConversationEnded(callback -> {

                                    // We got a name from the player, so we use that to filter the warps on.
                                    if (callback.wasSuccessful()) {

                                        UUID targetUUID =
                                                (UUID) callback.getStorageObject(PluginConversation.FILTER_WARP_BY_USER_IDENTIFIER);

                                        // Filter based on the owner.
                                        WarpFilter ownerFilter = new WarpFilter(WarpFilterKeys.OWNER, targetUUID);

                                        getInventoryFilteredWarps("Warps of " + Bukkit.getOfflinePlayer(targetUUID).getName(), ownerFilter).showPlayer(player);
                                    } else {
                                        getInventoryMainMenu(player).showPlayer(player);
                                    }

                                });

                                conversation.setEscapeSequence("stop");

                                conversation.startConversation(player);
                            }));
        }

        if (config.isIconEnabled(MenuIcon.MAIN_MENU_SEARCH_WARPS_BY_STRING)
                && PermissionManager.canSearchWarpsByString(player)) {
            ui.setButton(15,
                    new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.MAIN_MENU_SEARCH_WARPS_BY_STRING))
                            .amount(1).name(config.getIconTitle(MenuIcon.MAIN_MENU_SEARCH_WARPS_BY_STRING))
                            .lore(config.getIconDescription(MenuIcon.MAIN_MENU_SEARCH_WARPS_BY_STRING)).build(),
                            event -> {
                                event.setCancelled(true);
                                event.getWhoClicked().closeInventory();

                                // Request a player name to filter on.
                                PluginConversation conversation =
                                        PluginConversation.fromFirstPrompt(new RequestStringPrompt());

                                conversation.afterConversationEnded(callback -> {

                                    // We got a name from the player, so we use that to filter the warps on.
                                    if (callback.wasSuccessful()) {

                                        String term =
                                                callback.getStorageString(PluginConversation.FILTER_WARP_BY_STRING_IDENTIFIER);

                                        // Filter based on the name and description
                                        WarpFilter filter = new WarpFilter(WarpFilterKeys.EQUALS_NAME, term)
                                                .addFilter(WarpFilterKeys.CONTAINS_IN_DESCRIPTION, term);
                                        filter.setShouldMatchAll(false);


                                        getInventoryFilteredWarps("Warps containing '" + term + "'", filter).showPlayer(player);
                                    } else {
                                        getInventoryMainMenu(player).showPlayer(player);
                                    }

                                });

                                conversation.setEscapeSequence("stop");

                                conversation.startConversation(player);
                            }));
        }

        this.addCloseButton(ui);

        return ui;
    }

    private PaginatedGUI getInventoryServerWarpsMenu() {
        PaginatedGUI ui = new PaginatedGUI(config.getMenuTitle(AWPMenuType.SERVER_WARPS_MENU));

        List<GUIButton> buttons =
                plugin.getWarpManager().getWarpStorageProvider().getServerWarps().stream()
                        .sorted(Comparator.comparing(o -> o.getDisplayName(false)))
                        .map(warp -> new GUIButton(warp.getItemStack(), event -> {
                            event.setCancelled(true);

                            Player player = (Player) event.getWhoClicked();

                            if (event.isLeftClick()) {
                                plugin.getTeleportHandler().teleportPlayer(player, warp);
                            } else if (event.isRightClick()) {
                                if (!PermissionManager.canEditWarp(player, warp)) return;

                                player.openInventory(this.getEditMenu(player, warp).getInventory());
                            }
                        })).collect(Collectors.toList());

        this.fillInventoryWithWarps(ui, buttons);

        this.addBackButton(ui);
        this.addCloseButton(ui);

        return ui;
    }

    private PaginatedGUI getInventoryPublicWarpsMenu() {
        PaginatedGUI ui = new PaginatedGUI(config.getMenuTitle(AWPMenuType.PUBLIC_WARPS_MENU));

        List<GUIButton> buttons = plugin.getWarpManager().getWarpStorageProvider().getPublicWarps().stream()
                .sorted(Comparator.comparing(o -> o.getDisplayName(false)))
                .map(warp -> new GUIButton(warp.getItemStack(), event -> {

                    event.setCancelled(true);

                    Player player = (Player) event.getWhoClicked();

                    if (event.isLeftClick()) {
                        plugin.getTeleportHandler().teleportPlayer(player, warp);
                    } else if (event.isRightClick()) {
                        if (!PermissionManager.canEditWarp(player, warp)) return;

                        player.openInventory(this.getEditMenu(player, warp).getInventory());
                    }
                })).collect(Collectors.toList());

        this.fillInventoryWithWarps(ui, buttons);

        this.addBackButton(ui);
        this.addCloseButton(ui);

        return ui;
    }

    private PaginatedGUI getInventoryAccessiblePrivateWarpsMenu(UUID uuid) {
        PaginatedGUI ui = new PaginatedGUI(config.getMenuTitle(AWPMenuType.PRIVATE_WARPS_MENU));

        List<GUIButton> buttons =
                plugin.getWarpManager().getWarpStorageProvider().getAccessiblePrivateWarps(uuid).stream()
                        .sorted(Comparator.comparing(o -> o.getDisplayName(false)))
                        .map(warp -> new GUIButton(warp.getItemStack(), event -> {

                            event.setCancelled(true);

                            Player player = (Player) event.getWhoClicked();

                            if (event.isLeftClick()) {
                                plugin.getTeleportHandler().teleportPlayer(player, warp);
                            } else if (event.isRightClick()) {
                                if (!PermissionManager.canEditWarp(player, warp)) return;

                                player.openInventory(this.getEditMenu(player, warp).getInventory());
                            }
                        })).collect(Collectors.toList());

        this.fillInventoryWithWarps(ui, buttons);

        this.addBackButton(ui);
        this.addCloseButton(ui);

        return ui;
    }

    private PaginatedGUI getInventoryOwnedWarps(UUID uuid) {
        PaginatedGUI ui = new PaginatedGUI(config.getMenuTitle(AWPMenuType.OWNED_WARPS_MENU));

        List<GUIButton> buttons =
                plugin.getWarpManager().getWarpStorageProvider().getUserWarps(uuid).stream()
                        .sorted(Comparator.comparing(o -> o.getDisplayName(false)))
                        .map(warp -> new GUIButton(warp.getItemStack(), event -> {
                            event.setCancelled(true);

                            Player player = (Player) event.getWhoClicked();

                            // User wants to edit menu.
                            if (event.isRightClick()) {

                                if (!PermissionManager.canEditWarp(player, warp)) return;

                                player.openInventory(this.getEditMenu(player, warp).getInventory());
                            } else if (event.isLeftClick()) {
                                plugin.getTeleportHandler().teleportPlayer(player, warp);
                            }
                        })).collect(Collectors.toList());

        this.fillInventoryWithWarps(ui, buttons);

        this.addBackButton(ui);
        this.addCloseButton(ui);

        return ui;
    }

    private PaginatedGUI getInventoryFilteredWarps(String inventoryTitle, WarpFilter filter) {
        PaginatedGUI ui = new PaginatedGUI(inventoryTitle);

        List<Warp> warps = plugin.getWarpManager().getWarpStorageProvider().getAllWarps().stream()
                .filter(warp -> warp.matchesFilters(filter))
                .sorted(Comparator.comparing(o -> o.getDisplayName(false)))
                .collect(Collectors.toList());

        List<GUIButton> buttons =
                warps.stream().map(warp -> new GUIButton(warp.getItemStack(), event -> {
                    event.setCancelled(true);

                    Player player = (Player) event.getWhoClicked();

                    // User wants to edit menu.
                    if (event.isRightClick()) {

                        if (!PermissionManager.canEditWarp(player, warp)) return;

                        player.openInventory(this.getEditMenu(player, warp).getInventory());
                    } else if (event.isLeftClick()) {
                        plugin.getTeleportHandler().teleportPlayer(player, warp);
                    }
                })).collect(Collectors.toList());

        this.fillInventoryWithWarps(ui, buttons);

        this.addBackButton(ui);
        this.addCloseButton(ui);


        return ui;
    }

    private PaginatedGUI getEditMenu(Player viewer, Warp warp) {
        PaginatedGUI ui = new PaginatedGUI(config.getMenuTitle(AWPMenuType.EDIT_WARP_MENU)
                .replace("%warp-name%", warp.getDisplayName()));

        if (PermissionManager.canEditWarpName(viewer, warp)) {
            ui.setButton(10, new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.EDIT_MENU_EDIT_NAME))
                    .amount(1).name(config.getIconTitle(MenuIcon.EDIT_MENU_EDIT_NAME))
                    .lore(config.getIconDescription(MenuIcon.EDIT_MENU_EDIT_NAME))
                    .addLore("", ChatColor.GOLD + "Current name: ", warp.getDisplayName()).build(),
                    setupConversationInput(viewer, new EditNamePrompt(warp), getDefaultCallback(viewer, warp))));
        }

        if (PermissionManager.canEditWarpDescription(viewer, warp)) {
            ui.setButton(11,
                    new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.EDIT_MENU_EDIT_DESCRIPTION))
                            .amount(1).name(config.getIconTitle(MenuIcon.EDIT_MENU_EDIT_DESCRIPTION))
                            .lore(config.getIconDescription(MenuIcon.EDIT_MENU_EDIT_DESCRIPTION))
                            .addLore("", ChatColor.GOLD + "Current description: ")
                            .addLore(warp.getDescription().toArray(new String[0])).build(),
                            setupConversationInput(viewer, new EditDescriptionPrompt(warp), getDefaultCallback(viewer
                                    , warp))));
        }

        if (PermissionManager.canEditWarpCost(viewer, warp)) {
            ui.setButton(12, new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.EDIT_MENU_EDIT_COST))
                    .amount(1).name(config.getIconTitle(MenuIcon.EDIT_MENU_EDIT_COST))
                    .lore(config.getIconDescription(MenuIcon.EDIT_MENU_EDIT_COST))
                    .addLore("", ChatColor.GOLD + "Current cost: " + ChatColor.GREEN + "" + warp.getCost()).build(),
                    setupConversationInput(viewer, new EditCostPrompt(warp), getDefaultCallback(viewer, warp))));
        }

        ui.setButton(13, new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.EDIT_MENU_EDIT_TYPE))
                .amount(1).name(config.getIconTitle(MenuIcon.EDIT_MENU_EDIT_TYPE))
                .lore(config.getIconDescription(MenuIcon.EDIT_MENU_EDIT_TYPE))
                .addLore("", ChatColor.GOLD + "Currently: " + ChatColor.LIGHT_PURPLE
                        + warp.getType().toString().toLowerCase())
                .build(),
                event -> {
                    event.setCancelled(true);

                    WarpType newType = null;

                    if (plugin.getEconomyManager().isEconomySupported()) {

                        double cost = plugin.getConfigurationManager().getEditTypeCost();

                        if (!plugin.getEconomyManager().withdrawFunds(viewer.getUniqueId(), cost)) {
                            viewer.sendMessage(Message.WARPS_INSUFFICIENT_FUNDS.getTranslatedMessage(
                                    plugin.getEconomyManager().getMissingFunds(viewer.getUniqueId(), cost)));
                            return;
                        }
                    }

                    if (plugin.getPermissionManager().canCreateWarp(viewer, WarpType.SERVER) != CreateWarpPermissionResult.ALLOWED) {
                        // Player cannot create server warps, so we toggle between private and public.
                        newType = (warp.getType() == WarpType.PRIVATE ? WarpType.PUBLIC : WarpType.PRIVATE);
                    } else {
                        // Player can set type to any of the types, so we rotate between different types

                        // PRIVATE -> PUBLIC -> SERVER -> PRIVATE
                        if (warp.getType() == WarpType.PRIVATE) {
                            newType = WarpType.PUBLIC;
                        } else if (warp.getType() == WarpType.PUBLIC) {
                            newType = WarpType.SERVER;
                        } else {
                            newType = WarpType.PRIVATE;
                        }
                    }

                    // Only adjust the type of the warp if the player is allowed to create one of that type.
                    if (plugin.getPermissionManager().canCreateWarp(viewer, newType) != CreateWarpPermissionResult.ALLOWED) {
                        viewer.sendMessage(Message.WARPS_NOT_ALLOWED_ANY_MORE_WARPS.getTranslatedMessage(newType.toString().toLowerCase()));
                        return;
                    }

                    warp.setType(newType);

                    getEditMenu(viewer, warp).showPlayer(viewer);

                    plugin.getWarpManager().getWarpStorageProvider().saveWarp(warp);
                }));

        if (PermissionManager.canEditWarpIcon(viewer, warp)) {
            ui.setButton(14, new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.EDIT_MENU_EDIT_ICON))
                    .amount(1).name(config.getIconTitle(MenuIcon.EDIT_MENU_EDIT_ICON))
                    .lore(config.getIconDescription(MenuIcon.EDIT_MENU_EDIT_ICON))
                    .addLore(ChatColor.GOLD + "Currently: " + ChatColor.AQUA + warp.getIcon().getIcon().toString())
                    .build(), setupConversationInput(viewer, new EditIconMaterialPrompt(warp),
                    getDefaultCallback(viewer, warp))));
        }

        if (PermissionManager.canEditWarpEnchantedLook(viewer, warp)) {
            ui.setButton(15,
                    new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.EDIT_MENU_EDIT_ENCHANTED_ICON))
                            .amount(1).name(config.getIconTitle(MenuIcon.EDIT_MENU_EDIT_ENCHANTED_ICON))
                            .lore(config.getIconDescription(MenuIcon.EDIT_MENU_EDIT_ENCHANTED_ICON))
                            .addLore("", ChatColor.GOLD + "Currently: " + (warp.getIcon().isEnchanted() ?
                                    ChatColor.GREEN +
                                            "true" : ChatColor.RED + "false")).build(),
                            event -> {
                                event.setCancelled(true);

                                if (plugin.getEconomyManager().isEconomySupported()) {

                                    double cost = plugin.getConfigurationManager().getEditEnchantedLookCost();

                                    if (!plugin.getEconomyManager().withdrawFunds(viewer.getUniqueId(), cost)) {
                                        viewer.sendMessage(Message.WARPS_INSUFFICIENT_FUNDS.getTranslatedMessage(
                                                plugin.getEconomyManager().getMissingFunds(viewer.getUniqueId(),
                                                        cost)));
                                        return;
                                    }
                                }

                                warp.setIcon(new WarpIcon(warp.getIcon().getIcon(), !warp.getIcon().isEnchanted()));

                                getEditMenu(viewer, warp).showPlayer(viewer);

                                plugin.getWarpManager().getWarpStorageProvider().saveWarp(warp);
                            }));
        }

        if (PermissionManager.canEditWarpDestination(viewer, warp)) {
            ui.setButton(16,
                    new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.EDIT_MENU_EDIT_DESTINATION))
                            .amount(1).name(config.getIconTitle(MenuIcon.EDIT_MENU_EDIT_DESTINATION))
                            .lore(config.getIconDescription(MenuIcon.EDIT_MENU_EDIT_DESTINATION))
                            .build(),
                            setupConversationInput(viewer, new RelocateWarpPrompt(warp), getDefaultCallback(viewer,
                                    warp))));
        }

        if (warp.getType() == WarpType.PRIVATE && PermissionManager.canEditWarpWhitelist(viewer, warp)) {
            ui.setButton(17,
                    new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.EDIT_MENU_EDIT_WHITELIST))
                            .amount(1).name(config.getIconTitle(MenuIcon.EDIT_MENU_EDIT_WHITELIST))
                            .lore(config.getIconDescription(MenuIcon.EDIT_MENU_EDIT_WHITELIST))
                            .build(),
                            setupConversationInput(viewer, new EditWhitelistPrompt(warp), getDefaultCallback(viewer,
                                    warp))));
        }

        // Player may always delete their warp
        ui.setButton(31, new GUIButton(ItemBuilder.start(config.getIconMaterial(MenuIcon.EDIT_MENU_EDIT_DELETE))
                .amount(1).name(config.getIconTitle(MenuIcon.EDIT_MENU_EDIT_DELETE))
                .lore(config.getIconDescription(MenuIcon.EDIT_MENU_EDIT_DELETE)).build(),
                setupConversationInput(viewer, new DeleteWarpPrompt(warp), callback -> {

                    PaginatedGUI uiToShow;

                    if (callback.wasSuccessful()) {
                        // Warp was deleted so go back to 'your warps menu'
                        uiToShow = getInventoryOwnedWarps(viewer.getUniqueId());
                    } else {
                        uiToShow = getEditMenu(viewer, warp);
                    }

                    uiToShow.showPlayer(viewer);
                })));

        this.addBackButton(ui);
        this.addCloseButton(ui);

        return ui;
    }

    private ButtonListener setupConversationInput(Player viewer, Prompt firstPrompt, ConversationCallback callback) {
        return event -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();

            // Open edit conversation.
            PluginConversation conversation = PluginConversation.fromFirstPrompt(firstPrompt);

            conversation.afterConversationEnded(callback);

            conversation.setEscapeSequence("stop");

            conversation.startConversation(viewer);
        };
    }

    private ConversationCallback getDefaultCallback(Player viewer, Warp warp) {
        return callback -> {

            Warp warpToShow = warp;

            // If the user successfully edited the warp, we show the new warp
            // If not, we show the old warp.
            if (callback.wasSuccessful()) {
                warpToShow = (Warp) callback.getStorageObject(PluginConversation.EDITED_WARP_IDENTIFIER);
            }

            getEditMenu(viewer, warpToShow).showPlayer(viewer);
        };
    }

    private PaginatedGUI fillInventoryWithWarps(PaginatedGUI ui, List<GUIButton> items) {
        int row = 1;
        int column = 0;
        int page = 0;

        for (GUIButton button : items) {

            if (column >= 7) {
                column = 1;

                if (row == 4) {
                    row = 1;
                    page++;
                } else {
                    row++;
                }
            } else {
                column++;
            }

            int slot = page * 45 + row * 9 + column;

            ui.setButton(slot, button);
        }

        return ui;
    }

    private void addBackButton(PaginatedGUI ui) {
        ui.setToolbarItem(0, new GUIButton(ItemBuilder.start(Material.IRON_BARS).name("Back").amount(1).build(),
                event -> {
                    event.setCancelled(true);
                    this.openInventory(AWPMenuType.MAIN_MENU, (Player) event.getWhoClicked());
                }));
    }

    private void addBackButton(PaginatedGUI ui, AWPMenuType invToGoBackTo) {
        ui.setToolbarItem(0, new GUIButton(ItemBuilder.start(Material.IRON_BARS).name("Back").amount(1).build(),
                event -> {
                    event.setCancelled(true);
                    this.openInventory(invToGoBackTo, (Player) event.getWhoClicked());
                }));
    }

    private void addCloseButton(PaginatedGUI ui) {
        ui.setToolbarItem(8, new GUIButton(ItemBuilder.start(Material.BARRIER).amount(1).name("Close menu").build(),
                event -> event.getWhoClicked().closeInventory()));
    }


}
