package me.staartvin.plugins.advancedplayerwarping;

import me.staartvin.plugins.advancedplayerwarping.commands.CommandsManager;
import me.staartvin.plugins.advancedplayerwarping.config.ConfigurationManager;
import me.staartvin.plugins.advancedplayerwarping.economy.EconomyManager;
import me.staartvin.plugins.advancedplayerwarping.gui.InventoryManager;
import me.staartvin.plugins.advancedplayerwarping.language.LanguageHandler;
import me.staartvin.plugins.advancedplayerwarping.permissions.PermissionManager;
import me.staartvin.plugins.advancedplayerwarping.storage.WarpManager;
import me.staartvin.plugins.advancedplayerwarping.teleporting.TeleportHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedPlayerWarping extends JavaPlugin {

    private WarpManager warpManager;
    private CommandsManager commandsManager;
    private InventoryManager inventoryManager;
    private PermissionManager permissionManager;

    private ConfigurationManager configurationManager;
    private TeleportHandler teleportHandler;

    private LanguageHandler languageHandler;
    private EconomyManager economyManager;

    public void onEnable() {

        // Set up language module
        this.setLanguageHandler(new LanguageHandler(this));

        // Create permissions manager to be able to tell whether a player can do something or not.
        this.setPermissionManager(new PermissionManager(this));

        // Make sure to be able to access the config.
        this.setConfigurationManager(new ConfigurationManager(this));

        // Allow for teleporting
        this.setTeleportHandler(new TeleportHandler(this));

        // Load warp manager.
        this.setWarpManager(new WarpManager(this));

        // Load manager to handle commands.
        this.setCommandsManager(new CommandsManager(this));

        // Load inventory manager.
        this.setInventoryManager(new InventoryManager(this));

        // Load the economy manager.
        this.setEconomyManager(new EconomyManager(this));

        // Load /warp command.
        this.getCommand("warp").setExecutor(this.getCommandsManager());
        this.getCommand("warp").setTabCompleter(this.getCommandsManager());

        this.getLogger().info(this.getDescription().getFullName() + " has been enabled.");
    }

    public void onDisable() {

        this.getLogger().info(this.getDescription().getFullName() + " has been disabled.");
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public void setWarpManager(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public void setCommandsManager(CommandsManager commandsManager) {
        this.commandsManager = commandsManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public void setInventoryManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public void setConfigurationManager(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    public TeleportHandler getTeleportHandler() {
        return teleportHandler;
    }

    public void setTeleportHandler(TeleportHandler teleportHandler) {
        this.teleportHandler = teleportHandler;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public void setLanguageHandler(LanguageHandler languageHandler) {
        this.languageHandler = languageHandler;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public void setEconomyManager(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }
}
