package de.janschuri.lunaticFamily.external;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.utils.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {

    private static Economy econ = null;
    public Vault() {

        if (!LunaticFamily.installedVault) {
            Logger.warnLog("Vault is not installed! Please install Vault or disable it in plugin config.yml.");
            PluginConfig.useVault = false;
        }

        if (!setupEconomy() ) {
            Logger.warnLog("Could not setup Economy.");
            PluginConfig.useVault = false;
        }
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
