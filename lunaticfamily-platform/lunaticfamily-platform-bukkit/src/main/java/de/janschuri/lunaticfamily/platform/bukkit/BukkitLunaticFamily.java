package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticfamily.platform.bukkit.external.PlaceholderAPI;
import de.janschuri.lunaticlib.common.utils.Mode;
import de.janschuri.lunaticlib.platform.bukkit.external.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class BukkitLunaticFamily extends JavaPlugin {

    private static BukkitLunaticFamily instance;
    private static boolean installedCrazyAdvancementsAPI = false;

    @Override
    public void onEnable() {
        instance = this;

        if (Utils.classExists("eu.endercentral.crazy_advancements.CrazyAdvancementsAPI")) {
             BukkitLunaticFamily.installedCrazyAdvancementsAPI = true;
        }

        Mode mode = Mode.STANDALONE;

        int pluginId = 21912;
        Metrics metrics = new Metrics(this, pluginId);

        Path dataDirectory = getDataFolder().toPath();
        Platform platform = new PlatformImpl();

        LunaticFamily.onEnable(dataDirectory, mode, platform);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            Logger.infoLog("PlaceholderAPI found. Registering placeholders...");
            new PlaceholderAPI().register(); //
        }
    }
    public static BukkitLunaticFamily getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        LunaticFamily.onDisable();
    }

    public static boolean isInstalledCrazyAdvancementsAPI() {
        return installedCrazyAdvancementsAPI;
    }
}
