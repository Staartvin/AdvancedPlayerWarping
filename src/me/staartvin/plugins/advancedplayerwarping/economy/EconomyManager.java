package me.staartvin.plugins.advancedplayerwarping.economy;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class EconomyManager {

    private AdvancedPlayerWarping plugin;
    private static Economy economy;

    public EconomyManager(AdvancedPlayerWarping instance) {
        this.plugin = instance;

        if (this.checkForDependencies()) {
            plugin.getLogger().info("Using Vault for economy support.");
            this.registerEconomyHook();
        } else {
            plugin.getLogger().severe("Vault was not found, cannot provide economy support.");
        }
    }

    public boolean checkForDependencies() {

        Plugin dependency = plugin.getServer().getPluginManager().getPlugin("Vault");

        return dependency != null && dependency.isEnabled();
    }

    public boolean isEconomySupported() {
        return economy != null;
    }

    public void registerEconomyHook() {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }

        economy = rsp.getProvider();
    }

    public boolean hasSufficientFunds(UUID uuid, double neededFunds) {
        if (!isEconomySupported()) return true;

        return economy.has(Bukkit.getOfflinePlayer(uuid), neededFunds);
    }

    public boolean withdrawFunds(UUID uuid, double fundsToWithdraw) {
        if (!isEconomySupported()) return false;

        return hasSufficientFunds(uuid, fundsToWithdraw) && economy.withdrawPlayer(Bukkit.getOfflinePlayer(uuid),
                fundsToWithdraw).type.equals(EconomyResponse.ResponseType.SUCCESS);
    }

    public double getMissingFunds(UUID uuid, double neededFunds) {
        if (!isEconomySupported()) return 0.0f;

        if (hasSufficientFunds(uuid, neededFunds)) {
            return 0.0f;
        }

        return neededFunds - economy.getBalance(Bukkit.getOfflinePlayer(uuid));
    }
}
