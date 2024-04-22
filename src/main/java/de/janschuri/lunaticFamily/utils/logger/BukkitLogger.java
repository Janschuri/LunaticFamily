package de.janschuri.lunaticFamily.utils.logger;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import org.bukkit.Bukkit;

public class BukkitLogger extends Logger {
    private static final LunaticFamily plugin = LunaticFamily.getInstance();

    public static void debugLog(String msg) {
        if(PluginConfig.isDebug){
            Bukkit.getLogger().info("[" +plugin.getName() + "] [DEBUG]: " + msg);
        }
    }
    public static void infoLog(String msg) {
        Bukkit.getLogger().info("[" + plugin.getName() + "] [INFO]: " + msg);
    }

    public static void warnLog(String msg) {
        Bukkit.getLogger().warning("[" + plugin.getName() + "] [WARN]: " + msg);
    }

    public static void errorLog(String msg) {
        Bukkit.getLogger().severe("[" + plugin.getName() + "] [ERROR]: " + msg);
    }
}
