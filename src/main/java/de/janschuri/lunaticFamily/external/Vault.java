package de.janschuri.lunaticFamily.external;

import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.utils.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {

    private static Economy econ = null;
    public Vault() {
        if (!setupEconomy() ) {
            Logger.warnLog("Could not setup Economy.");
            Config.enabledVault = false;
        }
    }

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
