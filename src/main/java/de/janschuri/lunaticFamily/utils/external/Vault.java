package de.janschuri.lunaticFamily.utils.external;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {
    private static Economy econ = null;

    public static Economy getEconomy() {
        return econ;
    }
}
