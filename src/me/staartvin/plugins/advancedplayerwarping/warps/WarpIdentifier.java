package me.staartvin.plugins.advancedplayerwarping.warps;

import org.bukkit.ChatColor;

import java.util.Objects;
import java.util.UUID;

/**
 * Class used to identify a warp. A warp can either be identified by a name (server warp) or by a name and an owner
 * (private or public warp). Hence, you do not have to provide an owner.
 */
public class WarpIdentifier {

    /**
     * Create an identifier by name and owner. See {@link WarpIdentifier} for more info.
     * @param owner Owner of the warp
     * @param warpName Name of the warp.
     */
    public WarpIdentifier(UUID owner, String warpName) {
        Objects.requireNonNull(warpName);

        this.warpName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', warpName));
        this.owner = owner;
    }

    /**
     * Create an identifier by name. Note that, since you do not provide an owner, this identifier can only be used
     * to look for server warps (as they do not require an owner). Private and public warps do require an owner. If
     * you wish to look for them, use {@link #WarpIdentifier(UUID, String)} instead.
     * @param warpName Name of the warp to look for.
     */
    public WarpIdentifier(String warpName) {
        this(null, warpName);
    }

    private String warpName;

    private UUID owner;


    public String getWarpName() {
        return warpName;
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean hasOwner() {
        return getOwner() != null;
    }
}
