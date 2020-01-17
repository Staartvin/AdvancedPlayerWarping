package me.staartvin.plugins.advancedplayerwarping.language;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Every enumeration value has its path and default value. To get the path, do
 * {@link #getPath()}. To get the default value, do {@link #getDefault()}.
 * <p>
 * For the defined value in the lang.yml config, use
 * {@link #getTranslatedMessage(Object...)}. String objects are expected as
 * input.
 *
 * @author Staartvin and gomeow
 */

public enum Message {
    /**
     * Only players can open the warp menu. Sorry!
     */
    COMMANDS_ONLY_PLAYERS_CAN_OPEN_MENUS("commands.console not allowed to open menu", "&cOnly players can open the " +
            "warp menu. Sorry!"),

    /**
     * Only players can make warps
     */
    COMMANDS_ONLY_PLAYERS_CAN_MAKE_WARPS("commands.console not allowed to make warps", "&cOnly players can make warps"),

    /**
     * You are not allowed to create any more {0} warps.
     */
    WARPS_NOT_ALLOWED_ANY_MORE_WARPS("warps.warp limit reached", "&cYou are not allowed to create any more {0} warps."),

    /**
     * You are not allowed to create {0} warps.
     */
    WARPS_NOT_ALLOWED_WARPS("warps.create warp forbidden", "&cYou are not allowed to create {0} warps."),

    /**
     * You have to wait {0} more seconds before you can use a warp again.
     */
    WARPS_WAIT_FOR_TIMEOUT("warps.wait for timeout", "&cYou have to wait &d{0}&c more seconds before you can use a " +
            "warp again."),

    /**
     * You have not specified a warp name.
     */
    COMMANDS_NO_WARP_NAME_PROVIDED("commands.no warp name provided", "&cYou have not specified a warp name."),

    /**
     * There is already a warp with that name.
     */
    COMMANDS_CONFLICTING_WARP_NAME("commands.conflicting warp name", "&cThere is already a warp with that name."),

    /**
     * Your warp '{0}' has been created.
     */
    COMMANDS_WARP_CREATED("commands.warp created", "&aYour warp '&6{0}&a' has been created."),

    /**
     * Your warp '{0}' could not be created.
     */
    COMMANDS_WARP_NOT_CREATED("commands.warp not created", "&cYour warp '&6{0}&c' could not be created."),

    /**
     * There is no warp with that name.
     */
    COMMANDS_NO_WARP_FOUND("commands.no warp found", "&cThere is no warp with that name."),

    /**
     * You are not allowed to remove this warp.
     */
    PERMISSION_NOT_ALLOWED_DELETE_WARP("permission.not allowed.delete warp", "&cYou are not allowed to remove this " +
            "warp."),

    /**
     * The warp '{0}' has been removed.
     */
    COMMANDS_WARP_REMOVED("commands.warp removed", "&aThe warp '&6{0}&a' has been removed."),

    /**
     * The warp '{0}' could not be removed.
     */
    COMMANDS_WARP_NOT_REMOVED("commands.warp not removed", "&cThe warp '&6{0}&c' could not be removed."),

    /**
     * That's not a number!
     */
    COST_INPUT_NOT_A_NUMBER("warps.editing.cost.not a number", "&cThat's not a number!"),

    /**
     * Set the cost for using your warp (e.g. 20.5)
     */
    COST_PROMPT_QUESTION("warps.editing.cost.editing cost", "&6Set the cost for using your warp (e.g. 20.5)"),

    /**
     * You cannot set the cost lower than {0}.
     */
    COST_MINIMUM_COST("warps.editing.cost.minimum cost", "&cYou cannot set the cost lower than &d{0}&c."),

    /**
     * You cannot set the cost lower than {0}.
     */
    COST_MAXIMUM_COST("warps.editing.cost.maximum cost", "&cYou cannot set the cost higher than &d{0}&c."),

    /**
     * You didn't provide a description.
     */
    DESCRIPTION_NO_DESCRIPTION_PROVIDED("warps.editing.description.no description provided", "&cYou didn't provide a " +
            "description."),

    /**
     * Provide a description for your warp. Use comma's (,) to separate lines.
     */
    DESCRIPTION_PROMPT_QUESTION("warps.editing.description.editing description", "&6Provide a description for your " +
            "warp. Use comma's (,) to separate lines."),

    /**
     * You are not allowed to use color codes in the description.
     */
    DESCRIPTION_NO_COLOR_CODES_ALLOWED("warps.editing.description.color codes not allowed", "&cYou are not allowed to" +
            " use color codes in the description."),

    /**
     * Your description contains words that are not allowed.
     */
    DESCRIPTION_USING_FORBIDDEN_WORDS("warps.editing.description.using forbidden words", "&cYour description contains" +
            " words that are not allowed."),

    /**
     * Provide a new name for your warp.
     */
    NAME_PROMPT_QUESTION("warps.editing.name.editing name", "&6Provide a new name for your warp."),

    /**
     * You are not allowed to use color codes in the name.
     */
    NAME_NO_COLOR_CODES_ALLOWED("warps.editing.name.color codes not allowed", "&cYou are not allowed to" +
            " use color codes in the name."),

    /**
     * Your name contains words that are not allowed.
     */
    NAME_USING_FORBIDDEN_WORDS("warps.editing.name.using forbidden words", "&cYour name contains" +
            " words that are not allowed."),

    /**
     * There exists no material with that name!
     */
    ICON_INVALID_MATERIAL("warps.editing.icon.invalid material", "&cThere exists no material with that name!"),

    /**
     * You are not allowed to use that material as an icon.
     */
    ICON_USING_FORBIDDEN_MATERIAL("warps.editing.icon.using forbidden material", "&cYou are not allowed to use that " +
            "material as an icon."),

    /**
     * Provide a material type for the icon.
     */
    ICON_PROMPT_QUESTION("warps.editing.icon.editing icon", "&6Provide a material type for the icon."),

    /**
     * Type 'add' or 'remove' followed by a player to edit the whitelist.
     */
    WHITELIST_PROMPT_QUESTION("warps.editing.whitelist.editing whitelist", "&6Type 'add' or 'remove' followed by a " +
            "player to edit the whitelist."),

    /**
     * You did not provide the action, don't forget 'add' or 'remove'.
     */
    WHITELIST_NO_ACTION_PROVIDED("warps.editing.whitelist.no action provided", "&cYou did not provide the action, " +
            "don't forget 'add' or 'remove'."),

    /**
     * You did not provide a player.
     */
    WHITELIST_NO_PLAYER_PROVIDED("warps.editing.whitelist.no player provided", "&cYou did not provide a player."),

    /**
     * This player has never played before.
     */
    WHITELIST_UNKNOWN_PLAYER_PROVIDED("warps.editing.whitelist.unknown player provided", "&cThis player has never " +
            "played before."),

    /**
     * {0} is already whitelisted.
     */
    WHITELIST_PLAYER_ALREADY_WHITELISTED("warps.editing.whitelist.player already whitelisted", "&c{0} is already " +
            "whitelisted."),

    /**
     * {0} has been whitelisted.
     */
    WHITELIST_PLAYER_ADDED_TO_WHITELIST("warps.editing.whitelist.player added to whitelist", "&a{0} has been " +
            "whitelisted."),

    /**
     * {0} has been removed from the whitelist
     */
    WHITELIST_PLAYER_REMOVED_FROM_WHITELIST("warps.editing.whitelist.player removed from whitelist", "&a{0} has been " +
            "removed from the whitelist."),

    /**
     * {0} is not whitelisted.
     */
    WHITELIST_PLAYER_NOT_WHITELISTED("warps.editing.whitelist.player not whitelisted", "&c{0} is not whitelisted."),

    /**
     * You need at least {0} more funds to do this.
     */
    WARPS_INSUFFICIENT_FUNDS("warps.insufficient funds", "&cYou need at least {0} more funds to do this."),


    ;

    private static FileConfiguration LANG;
    private String path, def;

    /**
     * Lang enum constructor.
     *
     * @param path  The string path.
     * @param start The default string.
     */
    Message(final String path, final String start) {
        this.path = path;
        this.def = start;
    }

    Message(String string) {
        this.def = string;
    }

    /**
     * Set the {@code FileConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(final FileConfiguration config) {
        LANG = config;
    }

    /**
     * Get the value in the config with certain arguments.
     *
     * @param args arguments that need to be given. (Can be null)
     * @return value in config or otherwise default value
     */
    public String getTranslatedMessage(final Object... args) {
        String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(getPath(), getDefault()));

        if (args == null || args.length == 0)
            return value;
        else {

            if (args[0] == null) return value;

            for (int i = 0; i < args.length; i++) {
                value = value.replace("{" + i + "}", args[i].toString());
            }
        }

        return value;
    }

    /**
     * Get the default value of the path.
     *
     * @return The default value of the path.
     */
    public String getDefault() {
        return this.def;
    }

    /**
     * Get the path to the string.
     *
     * @return The path to the string.
     */
    public String getPath() {
        if (path == null) {
            return this.toString().replace("_", "-");
        }

        return this.path;
    }
}
