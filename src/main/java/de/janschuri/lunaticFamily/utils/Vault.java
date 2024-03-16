package de.janschuri.lunaticFamily.utils;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {
    private static Economy econ = null;
    public static Economy getEconomy() {
        return econ;
    }
}
