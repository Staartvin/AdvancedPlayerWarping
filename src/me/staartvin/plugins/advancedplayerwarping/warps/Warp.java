package me.staartvin.plugins.advancedplayerwarping.warps;

import com.sun.istack.internal.NotNull;
import me.staartvin.plugins.advancedplayerwarping.gui.filter.WarpFilter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Warp {

    @NotNull
    private Location destination;
    @NotNull
    private String displayName = "A new warp";
    private List<String> description = Collections.singletonList("No description set.");
    @NotNull
    private UUID owner;
    private double cost = 0.0f;
    private WarpType type = WarpType.PRIVATE;
    private WarpIcon icon = new WarpIcon();
    private List<UUID> whitelist = new ArrayList<>();
    private LocalDateTime timeCreated = LocalDateTime.now();

    /**
     * Create a new warp for a player. By default, the warp is private.
     *
     * @param name        Name of the warp
     * @param owner       Owner of the warp
     * @param destination Destination of the warp.
     */
    public Warp(@NotNull String name, @NotNull UUID owner, @NotNull Location destination) {
        Objects.requireNonNull(name, "The name of a warp cannot be null.");
        Objects.requireNonNull(owner, "The owner of a warp cannot be null.");
        Objects.requireNonNull(destination, "The destination of a warp cannot be null.");

        this.setDisplayName(name);
        this.setOwner(owner);
        this.setDestination(destination);
    }

    /**
     * Get the age of this warp in days.
     *
     * @return days since this warp was created
     */
    public long getAge() {
        return this.getAge(ChronoUnit.DAYS);
    }

    /**
     * Get the age of this warp in the given unit.
     *
     * @param unit Unit to return the age in.
     * @return age of the warp (in given unit).
     */
    public long getAge(ChronoUnit unit) {
        Objects.requireNonNull(unit);
        return unit.between(timeCreated, LocalDateTime.now());
    }

    /**
     * Share this warp with a player.
     *
     * @param sharedPlayer Player to share the warp with.
     * @return true if the warp was shared, false otherwise.
     */
    public boolean shareWarp(UUID sharedPlayer) {
        Objects.requireNonNull(sharedPlayer);

        List<UUID> sharedUUIDs = this.getWhitelist();

        // The player was already on the whitelist
        if (sharedUUIDs.contains(sharedPlayer)) return false;

        sharedUUIDs.add(sharedPlayer);

        this.setWhitelist(sharedUUIDs);

        return true;
    }

    /**
     * Get the itemstack that should be used to show this warp in a menu.
     *
     * @return ItemStack.
     */
    public ItemStack getItemStack() {

        ItemStack itemStack = new ItemStack(getIcon().getIcon(), 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();

        itemMeta.setDisplayName(this.getDisplayName());

        if (getIcon().isEnchanted()) {
            itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (!this.getDescription().isEmpty()) {
            lore.addAll(this.getDescription());
        }

        // Add empty line
        lore.add("");

        if (getType() != WarpType.SERVER) {
            lore.add(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + Bukkit.getOfflinePlayer(this.getOwner()).getName());
        }

        if (this.getCost() > 0) {
            lore.add(ChatColor.GOLD + "Cost: " + ChatColor.GREEN + this.getCost());
        }

        lore.add("");

        lore.add(ChatColor.GOLD + "Destination: " + ChatColor.LIGHT_PURPLE +
                this.getDestination().getBlockX() + ", " +
                this.getDestination().getBlockY() + ", " +
                this.getDestination().getBlockZ() + " on " +
                this.getDestination().getWorld().getName());

        lore.add("");

        lore.add(ChatColor.GOLD + "Type: " + ChatColor.YELLOW + this.getType().toString().toLowerCase());

        if (type == WarpType.PRIVATE && !this.whitelist.isEmpty()) {
            lore.add("");

            lore.add(ChatColor.GOLD + "Whitelist: ");
            lore.addAll(this.whitelist.stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).collect(Collectors.toList()));
        }

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * Check whether this warp matches the given filters. See {@link WarpFilter} for more info about filters.
     * @param filters Filters to check.
     * @return true if this warp matches all filters, false otherwise.
     */
    public boolean matchesFilters(WarpFilter... filters) {

        if (filters == null) return true;

        for (WarpFilter filter : filters) {
            if (!filter.matches(this)) return false;
        }

        return true;
    }

    /**
     * Check whether a player is the owner of this warp.
     * @param uuid UUID of the player to check.
     * @return true if the given player is the owner of this warp, false otherwise.
     */
    public boolean isOwner(UUID uuid) {
        Objects.requireNonNull(uuid);

        return this.getOwner().equals(uuid);
    }


    // --- GETTERS & SETTERS --- //

    /**
     * Get the destination of the warp.
     *
     * @return {@link Location} object representing destination
     */
    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    /**
     * Get the display name of this warp with color coding. See {@link #getDisplayName(boolean)} for more info.
     */
    public String getDisplayName() {
        return this.getDisplayName(true);
    }

    public void setDisplayName(String displayName) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
    }

    /**
     * Get the display name of this warp.
     *
     * @param withColorCoding Whether color coding should be included in the string.
     * @return name of the warp
     */
    public String getDisplayName(boolean withColorCoding) {
        return (withColorCoding ?
                ChatColor.translateAlternateColorCodes('&', displayName) :
                ChatColor.stripColor(this.displayName));
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public WarpType getType() {
        return type;
    }

    public void setType(WarpType type) {
        this.type = type;
    }

    public WarpIcon getIcon() {
        return icon;
    }

    public void setIcon(WarpIcon icon) {
        this.icon = icon;
    }

    public List<UUID> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<UUID> whitelist) {
        this.whitelist = whitelist;
    }

    public LocalDateTime getTimeCreated() {
        return this.timeCreated;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }
}
