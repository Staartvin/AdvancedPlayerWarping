package me.staartvin.plugins.advancedplayerwarping.storage;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpIcon;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpIdentifier;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FlatfileStorage implements WarpStorage {

    public static DateTimeFormatter timeCreatedFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private AdvancedPlayerWarping plugin;
    private File warpsFile;
    private FileConfiguration warpsData;

    public FlatfileStorage(AdvancedPlayerWarping instance) {
        super();

        this.plugin = instance;
    }

    public boolean loadStorage() {
        warpsFile = new File(plugin.getDataFolder() + File.separator + "warps" + File.separator + "warps.yml");

        if (!warpsFile.exists()) {
            try {
                boolean created = warpsFile.getParentFile().mkdirs() && warpsFile.createNewFile();

                if (!created) {
                    plugin.getLogger().severe("Could not create warps.yml file!");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        warpsData = YamlConfiguration.loadConfiguration(warpsFile);

        return true;
    }

    @Override
    public List<Warp> getPrivateWarps(UUID uuid) {
        return getUserWarps(uuid).stream().filter(warp -> warp.getType() == WarpType.PRIVATE).collect(Collectors.toList());
    }

    @Override
    public List<Warp> getPublicWarps(UUID uuid) {
        return getUserWarps(uuid).stream().filter(warp -> warp.getType() == WarpType.PUBLIC).collect(Collectors.toList());
    }

    @Override
    public List<Warp> getUserWarps(UUID uuid) {
        Objects.requireNonNull(uuid);

        ConfigurationSection uuidSection = getUUIDSection(uuid);

        List<Warp> warps = new ArrayList<>();

        // Read all warps under the section of a player
        for (String warpName : uuidSection.getKeys(false)) {

            ConfigurationSection warpSection = uuidSection.getConfigurationSection(warpName);

            // Read the warp from the section and add it.
            warps.add(this.readWarpFromSection(warpSection));
        }

        // Return the warps that we've read.
        return warps;
    }

    @Override
    public List<Warp> getPublicWarps() {
        return this.getAllWarps().parallelStream().filter(warp -> warp.getType() == WarpType.PUBLIC).collect(Collectors.toList());
    }

    @Override
    public List<Warp> getAccessiblePrivateWarps(UUID uuid) {
        return this.getAllWarps().parallelStream().filter(warp -> warp.getType() == WarpType.PRIVATE
                && !warp.isOwner(uuid) // We do not want the private warps of the requested player
                && warp.getWhitelist().contains(uuid))
                .collect(Collectors.toList());
    }

    @Override
    public List<Warp> getServerWarps() {
        return this.getAllWarps().parallelStream().filter(warp -> warp.getType() == WarpType.SERVER).collect(Collectors.toList());
    }

    @Override
    public Optional<Warp> getWarpByName(WarpIdentifier identifier) {

        ConfigurationSection warpSection = this.getWarpSectionByIdentifier(identifier).orElse(null);

        if (warpSection == null) return Optional.empty();

        return Optional.of(this.readWarpFromSection(warpSection));
    }

    @Override
    public boolean createWarp(UUID owner, String displayName, Location destination) {
        Warp warp = new Warp(displayName, owner, destination);

        return this.saveWarp(warp);
    }

    @Override
    public boolean deleteWarp(WarpIdentifier identifier) {
        Optional<ConfigurationSection> sectionOptional = getWarpSectionByIdentifier(identifier);

        // No selection to delete.
        if (!sectionOptional.isPresent()) return false;

        ConfigurationSection section = sectionOptional.get();

        section.getParent().set(section.getName(), null);

        this.saveFile();

        return true;
    }

    @Override
    public List<Warp> getAllWarps() {

        List<Warp> warps = new ArrayList<>();

        for (String storedUUID : warpsData.getKeys(false)) {
            UUID uuid = UUID.fromString(storedUUID);

            warps.addAll(this.getUserWarps(uuid));
        }

        return warps;
    }

    @Override
    public boolean saveWarp(Warp warp) {
        Objects.requireNonNull(warp);

        List<Warp> matchingWarps = this.findSameWarps(warp);

        // Delete warps that match the warp we are about to save.
        if (!matchingWarps.isEmpty()) {
            matchingWarps.forEach(matchingWarp -> {
                deleteWarp(new WarpIdentifier(matchingWarp.getOwner(), matchingWarp.getDisplayName(false)));
            });
        }

        ConfigurationSection section = getUUIDSection(warp.getOwner());

        this.saveWarpToSection(section, warp);

        return true;
    }

    private List<Warp> findSameWarps(Warp warp) {
        return this.getUserWarps(warp.getOwner()).stream().filter(userWarp -> areSameWarps(warp, userWarp)).collect(Collectors.toList());
    }

    private boolean areSameWarps(Warp one, Warp two) {
        if (!one.isOwner(two.getOwner())) {
            return false;
        }

        if (!one.getTimeCreated().isEqual(two.getTimeCreated())) {
            return false;
        }

        return true;
    }

    private ConfigurationSection getUUIDSection(UUID uuid) {
        Objects.requireNonNull(uuid);

        ConfigurationSection section = warpsData.getConfigurationSection(uuid.toString());

        if (section == null) {
            section = warpsData.createSection(uuid.toString());
        }

        return section;
    }

    private ConfigurationSection getWarpSection(UUID uuid, String displayName) {
        return warpsData.getConfigurationSection(uuid.toString() + "." + displayName.toLowerCase());
    }

    private Warp readWarpFromSection(ConfigurationSection section) {
        Objects.requireNonNull(section);

        String destinationString = section.getString("destination");

        String[] splittedDestString = destinationString.split(",");

        World world = plugin.getServer().getWorld(splittedDestString[0]);

        Location destination = new Location(world, Double.parseDouble(splittedDestString[1]),
                Double.parseDouble(splittedDestString[2]),
                Double.parseDouble(splittedDestString[3]), Float.parseFloat(splittedDestString[4]),
                Float.parseFloat(splittedDestString[5]));

        String displayName = section.getString("display name");

        List<String> description = section.getStringList("description");

        UUID owner = UUID.fromString(section.getString("owner"));

        double cost = section.getDouble("cost");

        WarpType type = WarpType.valueOf(section.getString("type"));

        Material iconMaterial = Material.valueOf(section.getString("icon.material"));
        boolean enchanted = section.getBoolean("icon.enchanted");

        WarpIcon icon = new WarpIcon(iconMaterial, enchanted);

        List<UUID> whitelist =
                section.getStringList("whitelist").stream().map(UUID::fromString).collect(Collectors.toList());

        LocalDateTime timeCreated = LocalDateTime.parse(section.getString("time created"), timeCreatedFormat);

        Warp warp = new Warp(displayName, owner, destination);

        warp.setWhitelist(whitelist);
        warp.setCost(cost);
        warp.setDescription(description);
        warp.setIcon(icon);
        warp.setTimeCreated(timeCreated);
        warp.setType(type);

        return warp;
    }

    private void saveWarpToSection(ConfigurationSection section, Warp warp) {
        Objects.requireNonNull(section);
        Objects.requireNonNull(warp);

        Location dest = warp.getDestination();

        String displayName = warp.getDisplayName(false).toLowerCase();

        // Store the destination.
        section.set(displayName + ".destination",
                dest.getWorld().getName() + "," + dest.getX() + "," + dest.getY() + "," + dest.getZ() + ", " + dest.getYaw() +
                        "," + dest.getPitch());

        // Store the display name
        section.set(displayName + ".display name", warp.getDisplayName());

        // Store description
        section.set(displayName + ".description", warp.getDescription());

        // Store the owner
        section.set(displayName + ".owner", warp.getOwner().toString());

        // Store the cost
        section.set(displayName + ".cost", warp.getCost());

        // Store warp type
        section.set(displayName + ".type", warp.getType().toString());

        // Store icon
        section.set(displayName + ".icon.material", warp.getIcon().getIcon().toString());
        section.set(displayName + ".icon.enchanted", warp.getIcon().isEnchanted());

        // Store whitelist
        section.set(displayName + ".whitelist", warp.getWhitelist().stream().map(UUID::toString).collect(Collectors.toList()));

        // Store creation of the warp
        section.set(displayName + ".time created", warp.getTimeCreated().format(timeCreatedFormat));

        this.saveFile();
    }

    private Optional<ConfigurationSection> getWarpSectionByIdentifier(WarpIdentifier identifier) {
        // User did not provide an owner, so look through server warps.
        if (!identifier.hasOwner()) {
            // We are looking for the owner first.
            Warp warp =
                    this.getServerWarps().stream().filter(searchWarp -> searchWarp.getDisplayName(false)
                            .equalsIgnoreCase(identifier.getWarpName())).findFirst().orElse(null);

            // There is no server warp with the given name
            if (warp == null) {
                return Optional.empty();
            } else {
                // We found a match, so return it.
                return Optional.ofNullable(this.getWarpSection(warp.getOwner(), warp.getDisplayName(false)));
            }
        }

        return Optional.ofNullable(this.getWarpSection(identifier.getOwner(), identifier.getWarpName()));
    }

    private void saveFile() {
        try {
            this.warpsData.save(warpsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
