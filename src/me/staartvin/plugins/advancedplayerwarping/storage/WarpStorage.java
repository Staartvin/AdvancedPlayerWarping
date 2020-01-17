package me.staartvin.plugins.advancedplayerwarping.storage;

import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpIdentifier;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarpStorage {

    /**
     * Get the private warps of a player
     * @param uuid UUID of the player
     * @return a list of private warps of the given player
     */
    List<Warp> getPrivateWarps(UUID uuid);

    /**
     * Get the public warps of a player
     * @param uuid UUID of the player
     * @return a list of the public warps of the given player
     */
    List<Warp> getPublicWarps(UUID uuid);

    /**
     * Get all warps owned by the given player
     * @param uuid UUID of the player
     * @return a list of warps that are owned by the given player
     */
    List<Warp> getUserWarps(UUID uuid);

    /**
     * Get all public warps.
     * @return a list of public warps.
     */
    List<Warp> getPublicWarps();

    /**
     * Get the private warps that the given player has access to. Note that this will not include warps made by the
     * player.
     * @param uuid UUID of the player
     * @return a list of private warps.
     */
    List<Warp> getAccessiblePrivateWarps(UUID uuid);

    /**
     * Get all server warps.
     * @return a list of server warps.
     */
    List<Warp> getServerWarps();

    /**
     * Try to get the warp that matches the given display name and owner. If the given owner is null, it tries to get
     * a server warp with the given display name.
     *
     * @param identifier Identifier used to find the warp
     * @return the warp that matches the given parameters.
     */
    Optional<Warp> getWarpByName(WarpIdentifier identifier);

    /**
     * Create a warp with the given parameters.
     * @param owner Owner of the warp.
     * @param displayName Name of the warp.
     * @param destination Destination of the warp.
     * @return true if the warp was created, false if it already existed or couldn't be created for
     * some other reason.
     */
    boolean createWarp(UUID owner, String displayName, Location destination);

    /**
     * Delete a warp with the given name and owner. If no owner is given, it will try to delete a server warp with
     * the given name.
     *
     * @param identifier@return true if the warp was deleted. False if it didn't exist or couldn't be deleted for some other reason.
     */
    boolean deleteWarp(WarpIdentifier identifier);

    /**
     * Get all warps that are recorded.
     * @return a list of all warps that are stored.
     */
    List<Warp> getAllWarps();

    /**
     * Save a warp to the storage. If it already exists, the existing warp will be overridden.
     * @param warp Warp to save.
     * @return true if the warp could be saved, false otherwise.
     */
    boolean saveWarp(Warp warp);

    boolean loadStorage();
}
