package me.staartvin.plugins.advancedplayerwarping.warps;

/**
 * Type of warps that exist.
 */
public enum WarpType {

    /**
     * A warp that can only be used by the owner of the warp. Default warp type.
     */
    PRIVATE,

    /**
     * A warp that is accessible to everyone, made by a player.
     */
    PUBLIC,

    /**
     * A warp that is created by 'the server' and can be used by everyone.
     */
    SERVER;

}
