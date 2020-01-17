package me.staartvin.plugins.advancedplayerwarping.warps;

import org.bukkit.Material;

public class WarpIcon {

    public WarpIcon() {}

    public WarpIcon(Material material, boolean enchanted) {
        this.enchantedLook = enchanted;
        this.icon = material;
    }

    private Material icon = Material.PURPLE_STAINED_GLASS_PANE;

    private boolean enchantedLook = false;

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public boolean isEnchanted() {
        return enchantedLook;
    }

    public void setEnchanted(boolean enchantedLook) {
        this.enchantedLook = enchantedLook;
    }
}
