package me.staartvin.plugins.advancedplayerwarping.permissions;

import org.bukkit.entity.Player;

public enum WarpPermissions {

    /** Creating warps **/
    CREATE_WARP_SERVER("apw.create.server"),
    CREATE_WARP_PUBLIC("apw.create.public"),
    CREATE_WARP_PRIVATE("apw.create.private"),

    /** Editing warps of others **/
    EDIT_WARP_SERVER("apw.edit.server"),
    EDIT_WARP_PUBLIC_OTHERS("apw.edit.public.others"),
    EDIT_WARP_PRIVATE_OTHERS("apw.edit.private.others"),

    /** Editing properties of warps **/
    EDIT_PROPERTIES_WARP_NAME("apw.edit.warpname"),
    EDIT_PROPERTIES_WARP_NAME_COLORS("apw.edit.warpname.colors"),
    EDIT_PROPERTIES_WARP_COST("apw.edit.warpcost"),
    EDIT_PROPERTIES_WARP_DESCRIPTION("apw.edit.warpdescription"),
    EDIT_PROPERTIES_WARP_DESCRIPTION_COLORS("apw.edit.warpdescription.colors"),
    EDIT_PROPERTIES_WARP_ICON("apw.edit.warpicon"),
    EDIT_PROPERTIES_WARP_ICON_ENCHANTED("apw.edit.warpcost.enchanted"),
    EDIT_PROPERTIES_WARP_DESTINATION("apw.edit.destination"),
    EDIT_PROPERTIES_WARP_WHITELIST("apw.edit.whitelist"),

    /** Using warps **/
    USE_WARP_SERVER("apw.use.warps.server"),
    USE_WARP_PUBLIC("apw.use.warps.public"),
    USE_WARP_PRIVATE("apw.use.warps.private"),

    /** Use search functionality **/
    SEARCH_WARPS_BY_PLAYER("apw.search.warps.player"),
    SEARCH_WARPS_BY_STRING("apw.search.warps.string"),

    /** Bypass permissions **/
    BYPASS_LIMIT_PRIVATE_WARPS("apw.bypass.limit.private"),
    BYPASS_LIMIT_PUBLIC_WARPS("apw.bypass.limit.public"),
    BYPASS_ECONOMY_USE_WARPS("apw.bypass.economy.warps.use"),
    BYPASS_TIMEOUT("apw.bypass.time-out"),

    /** RELOAD COMMANDS **/
    RELOAD_CONFIGS("apw.reload"),
    
    ;

    private String permission;

    WarpPermissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(this.getPermission());
    }
}
