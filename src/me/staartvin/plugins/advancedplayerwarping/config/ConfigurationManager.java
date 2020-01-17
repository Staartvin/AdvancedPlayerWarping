package me.staartvin.plugins.advancedplayerwarping.config;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.gui.inventory.AWPMenuType;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationManager {

    private AdvancedPlayerWarping plugin;

    public ConfigurationManager(AdvancedPlayerWarping instance) {
        this.plugin = instance;

        // Load configuration file so we can read from it.
        this.loadConfig();
    }

    private void loadConfig() {
        // Load default config from jar file.
        plugin.saveDefaultConfig();
    }

    public List<Material> getBlacklistedIcons() {
        List<String> stringMaterials = plugin.getConfig().getStringList("warps.editing.blacklisted-warp-icons");

        List<Material> materials = new ArrayList<>();

        stringMaterials.forEach(stringMaterial -> {

            try {
                materials.add(Material.valueOf(stringMaterial.toUpperCase().replace(" ", "_")));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Blacklisted icon '" + stringMaterial + "' is not a valid item!");
            }

        });

        return materials;
    }

    public List<String> getBlacklistedWarpNames() {
        return plugin.getConfig().getStringList("warps.editing.blacklisted-warp-names");
    }

    public List<String> getBlacklistedDescriptions() {
        return plugin.getConfig().getStringList("warps.editing.blacklisted-warp-descriptions");
    }

    public double getEditWarpNameCost() {
        return plugin.getConfig().getDouble("warps.editing.warp-name-cost", 0.0);
    }

    public double getEditDescriptionCost() {
        return plugin.getConfig().getDouble("warps.editing.warp-description-cost", 0.0);
    }

    public double getEditCostCost() {
        return plugin.getConfig().getDouble("warps.editing.warp-cost-cost", 0.0);
    }

    public double getEditTypeCost() {
        return plugin.getConfig().getDouble("warps.editing.warp-type-cost", 0.0);
    }

    public double getEditIconCost() {
        return plugin.getConfig().getDouble("warps.editing.warp-icon-cost", 0.0);
    }

    public double getEditEnchantedLookCost() {
        return plugin.getConfig().getDouble("warps.editing.warp-enchanted-icon-cost", 0.0);
    }

    public double getEditRelocateCost() {
        return plugin.getConfig().getDouble("warps.editing.warp-relocate-cost", 0.0);
    }

    public double getEditWhitelistCost() {
        return plugin.getConfig().getDouble("warps.editing.warp-whitelist-cost", 0.0);
    }

    public double getMinimumUsageCost() {
        return plugin.getConfig().getDouble("warps.min-cost", 0.0);
    }

    public double getMaximumUsageCost() {
        return plugin.getConfig().getDouble("warps.maximum-cost", 1000.0);
    }

    public int getWarpTimeout() {
        return plugin.getConfig().getInt("warps.time-out", 5);
    }

    public int getDefaultPrivateWarpLimit() {
        return plugin.getConfig().getInt("warps.limits.default-private-limit", 5);
    }

    public int getDefaultPublicWarpLimit() {
        return plugin.getConfig().getInt("warps.limits.default-public-limit", 2);
    }

    public boolean isIconEnabled(MenuIcon icon) {

        String path = getIconPath(icon) + ".enabled";

        return plugin.getConfig().getBoolean(path);
    }

    public Material getIconMaterial(MenuIcon icon) {
        String path = getIconPath(icon) + ".icon";

        try {
            return Material.valueOf(plugin.getConfig().getString(path));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe("Material of icon " + icon.toString() + " is not valid!");
            return Material.PAPER;
        }
    }

    public String getIconTitle(MenuIcon icon) {
        String path = getIconPath(icon) + ".title";

        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path));
    }

    public List<String> getIconDescription(MenuIcon icon) {
        String path = getIconPath(icon) + ".description";

        return plugin.getConfig().getStringList(path).stream().map(string ->
                ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());
    }

    public String getMenuTitle(AWPMenuType menuType) {
        String path = getMenuPath(menuType) + ".title";

        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path, ""));
    }

    private String getIconPath(MenuIcon icon) {
        switch (icon) {
            case MAIN_MENU_SERVER_WARPS:
                return "menus.main.server-warps";
            case MAIN_MENU_PUBLIC_WARPS:
                return "menus.main.public-warps";
            case MAIN_MENU_ACCESSIBLE_PRIVATE_WARPS:
                return "menus.main.accessible-private-warps";
            case MAIN_MENU_OWNED_WARPS:
                return "menus.main.owned-warps";
            case MAIN_MENU_SEARCH_WARPS_BY_PLAYER:
                return "menus.main.search-warps-by-player";
            case MAIN_MENU_SEARCH_WARPS_BY_STRING:
                return "menus.main.search-warps-by-string";
            case EDIT_MENU_EDIT_NAME:
                return "menus.edit-warp.edit-name";
            case EDIT_MENU_EDIT_DESCRIPTION:
                return "menus.edit-warp.edit-description";
            case EDIT_MENU_EDIT_TYPE:
                return "menus.edit-warp.edit-type";
            case EDIT_MENU_EDIT_COST:
                return "menus.edit-warp.edit-cost";
            case EDIT_MENU_EDIT_ICON:
                return "menus.edit-warp.edit-icon";
            case EDIT_MENU_EDIT_ENCHANTED_ICON:
                return "menus.edit-warp.edit-enchanted-icon";
            case EDIT_MENU_EDIT_DESTINATION:
                return "menus.edit-warp.edit-destination";
            case EDIT_MENU_EDIT_DELETE:
                return "menus.edit-warp.edit-delete";
            case EDIT_MENU_EDIT_WHITELIST:
                return "menus.edit-warp.edit-whitelist";
        }

        return "";
    }

    private String getMenuPath(AWPMenuType menuType) {
        switch (menuType) {
            case MAIN_MENU:
                return "menus.main";
            case EDIT_WARP_MENU:
                return "menus.edit-warp";
            case OWNED_WARPS_MENU:
                return "menus.owned-warps";
            case PUBLIC_WARPS_MENU:
                return "menus.public-warps";
            case SERVER_WARPS_MENU:
                return "menus.server-warps";
            case PRIVATE_WARPS_MENU:
                return "menus.accessible-private-warps";
            case SEARCH_BY_PLAYER_MENU:
                return "menus.search-warps-by-player";
            case SEARCH_BY_STRING_MENU:
                return "menus.search-warps-by-string";
        }

        return "";
    }

    public void reloadConfig() {
        plugin.reloadConfig();
    }
}
