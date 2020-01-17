package me.staartvin.plugins.advancedplayerwarping.storage;

import com.sun.istack.internal.Nullable;
import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpIdentifier;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WarpManager {

    private WarpStorage warpStorage;
    private AdvancedPlayerWarping plugin;

    public WarpManager(AdvancedPlayerWarping instance) {
        this.plugin = instance;

        // First load storage provider and then load warps into cache.
        if (this.loadWarpStorageProvider()) {
            plugin.getLogger().info("Loaded " + this.loadWarps() + " warps from storage.");
        } else {
            plugin.getLogger().severe("Could not load warp storage provider, so didn't load any warps.");
        }
    }

    private boolean loadWarpStorageProvider() {
        this.warpStorage = new FlatfileStorage(plugin);

        return this.warpStorage.loadStorage();
    }

    /**
     * Load all warps into the cache.
     *
     * @return how many warps have been loaded.
     */
    private int loadWarps() {
        return this.getWarpStorageProvider().getAllWarps().size();
    }

    /**
     * Get the storage provider that stores the warps. Note that it may be null if no storage provider could be loaded!
     *
     * @return {@link WarpStorage} object.
     */
    @Nullable
    public WarpStorage getWarpStorageProvider() {
        return this.warpStorage;
    }

    /**
     * Check if a warp already exists with the given identifier.
     *
     * @param identifier Identifier used to verify whether a warp already exists
     * @return true if it exists, false if doesn't exist.
     */
    public boolean warpExists(WarpIdentifier identifier) {
        Objects.requireNonNull(identifier);

        List<Warp> warpsToSearch;

        if (!identifier.hasOwner()) {
            // We know it must be a server warp.
            warpsToSearch = this.getWarpStorageProvider().getServerWarps();
        } else {
            warpsToSearch = this.getWarpStorageProvider().getUserWarps(identifier.getOwner());
        }

        return warpsToSearch.stream().anyMatch(warp -> warp.getDisplayName(false).equalsIgnoreCase(identifier.getWarpName()));
    }

    /**
     * Find the warp that matches the given identifier.
     * @param identifier Identifier to use.
     * @return a warp if one was found.
     */
    public Optional<Warp> getWarpByIdentifier(WarpIdentifier identifier) {
        Objects.requireNonNull(identifier);

        return this.getWarpStorageProvider().getWarpByName(identifier);
    }


}
