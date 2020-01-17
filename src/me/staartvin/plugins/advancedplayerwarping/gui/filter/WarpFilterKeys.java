package me.staartvin.plugins.advancedplayerwarping.gui.filter;

import me.staartvin.plugins.advancedplayerwarping.warps.WarpType;

import java.util.UUID;

public enum WarpFilterKeys {

    TYPE(WarpType.class), EQUALS_NAME(String.class), OWNER(UUID.class), CONTAINS_IN_NAME(String.class),
    CONTAINS_IN_DESCRIPTION(String.class);

    private Class<?> classOfValue;

    WarpFilterKeys(Class<?> classOfValue) {
        this.classOfValue = classOfValue;
    }

    public Class<?> getClassOfValue() {
        return classOfValue;
    }

}
