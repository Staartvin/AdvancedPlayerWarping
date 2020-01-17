package me.staartvin.plugins.advancedplayerwarping.gui.filter;

import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpFilter {

    private Map<WarpFilterKeys, Object> filterMap = new HashMap<>();

    private boolean shouldMatchAll = true;

    public WarpFilter(WarpFilterKeys key, Object value) {
        this.addFilter(key, value);
    }

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
        for (Map.Entry<WarpFilterKeys, Object> entry : filterMap.entrySet()) {

            // The warp should match ALL criteria, so as soon as we find one that does not match we return false.
            if (this.shouldMatchAll()) {
                switch (entry.getKey()) {
                    case TYPE:
                        if (!warp.getType().equals(WarpType.valueOf(entry.getValue().toString()))) return false;
                        break;
                    case OWNER:
                        if (!warp.isOwner(UUID.fromString(entry.getValue().toString()))) return false;
                        break;
                    case EQUALS_NAME:
                    case CONTAINS_IN_NAME:
                        if (!warp.getDisplayName(false).toLowerCase().contains(entry.getValue().toString().toLowerCase()))
                            return false;
                        break;
                    case CONTAINS_IN_DESCRIPTION:
                        if (warp.getDescription().parallelStream().noneMatch(string -> string.toLowerCase().contains(entry.getValue().toString().toLowerCase())))
                            return false;
                        break;
                }
            } else {
                // In this case, a warp should meet at least one criterion to match the filter.
                switch (entry.getKey()) {
                    case TYPE:
                        if (warp.getType().equals(WarpType.valueOf(entry.getValue().toString()))) return true;
                        break;
                    case OWNER:
                        if (warp.isOwner(UUID.fromString(entry.getValue().toString()))) return true;
                        break;
                    case EQUALS_NAME:
                    case CONTAINS_IN_NAME:
                        if (warp.getDisplayName(false).toLowerCase().contains(entry.getValue().toString().toLowerCase()))
                            return true;
                        break;
                    case CONTAINS_IN_DESCRIPTION:
                        if (warp.getDescription().parallelStream().anyMatch(string -> string.toLowerCase().contains(entry.getValue().toString().toLowerCase())))
                            return true;
                        break;
                }
            }
        }

        // Return whether it should match all. If it should, and we came here then we know we pass all criterion.
        // If we shouldn't match all and we came here it means that we haven't found a criterion that worked, so we
        // don't pass the filter.
        return this.shouldMatchAll();
    }

    /**
     * If there are multiple filter criteria given, we can either look for warps that match all criteria or match any
     * of the criteria. When this method returns true, a warp must contain all the criteria to match. If this returns
     * false, a warp should only contain at least one of the criteria.
     *
     * @return true if we should match all criteria, false if we only have to meet at least one criterion.
     */
    public boolean shouldMatchAll() {
        return shouldMatchAll;
    }

    public void setShouldMatchAll(boolean shouldMatchAll) {
        this.shouldMatchAll = shouldMatchAll;
    }
}
