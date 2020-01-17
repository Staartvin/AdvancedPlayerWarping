package me.staartvin.plugins.advancedplayerwarping.gui.filter;

import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpFilter {

    private Map<WarpFilterKeys, Object> filterMap = new HashMap<>();

    public WarpFilter addFilter(WarpFilterKeys key, Object value) {

        if (!value.getClass().equals(key.getClassOfValue())) {
            throw new IllegalArgumentException("Class of key " + key + " requires " + key.getClassOfValue() + ", but " +
                    "it got " + value.getClass() + " instead!");
        }

        filterMap.put(key, value);

        return this;
    }

    /**
     * Check whether a given warp matches this filter.
     *
     * @param warp Warp to test against this filter.
     * @return true if the given warp matches the filter, false otherwise.
     */
    public boolean matches(Warp warp) {
        for (Map.Entry<WarpFilterKeys, Object> entry: filterMap.entrySet()) {
            switch (entry.getKey()) {
                case TYPE:
                    if (!warp.getType().equals(WarpType.valueOf(entry.getValue().toString()))) return false;
                    break;
                case OWNER:
                    if (!warp.getOwner().equals(UUID.fromString(entry.getValue().toString()))) return false;
                    break;
                case NAME:
                    if (!warp.getDisplayName(false).toLowerCase().contains(entry.getValue().toString().toLowerCase())) return false;
                    break;
            }
        }

        return true;
    }
}
