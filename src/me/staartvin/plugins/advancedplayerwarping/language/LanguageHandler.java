package me.staartvin.plugins.advancedplayerwarping.language;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * This class
 *
 * @author Staartvin Return the language being used.
 */
public class LanguageHandler {

    private FileConfiguration languageConfig;
    private File languageConfigFile;
    private AdvancedPlayerWarping plugin;

    public LanguageHandler(final AdvancedPlayerWarping instance) {
        this.plugin = instance;

        this.createNewFile();
    }

    /**
     * Create a new language file.
     */
    public void createNewFile() {
        reloadConfig();
        saveConfig();

        loadConfig();

        plugin.getLogger().info("Loaded " + Message.values().length + " translatable strings.");
    }

    public FileConfiguration getConfig() {
        if (languageConfig == null) {
            this.reloadConfig();
        }
        return languageConfig;
    }

    public void loadConfig() {

        languageConfig.options().header("Language file. You can edit these messages if you'd like.");

        for (Message value : Message.values()) {
            languageConfig.addDefault(value.getPath(), value.getDefault());
        }

        languageConfig.options().copyDefaults(true);
        saveConfig();
    }

    public void reloadConfig() {
        if (languageConfigFile == null) {
            languageConfigFile = new File(plugin.getDataFolder() + "/messages", "messages.yml");
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageConfigFile);

        Message.setFile(languageConfig);
    }

    public void saveConfig() {
        if (languageConfig == null || languageConfigFile == null) {
            return;
        }
        try {
            getConfig().save(languageConfigFile);
        } catch (final IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + languageConfigFile, ex);
        }
    }
}
