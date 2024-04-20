package de.janschuri.lunaticFamily.utils.logger;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import org.bukkit.Bukkit;

public class BukkitLogger extends Logger {
    private static final LunaticFamily plugin = LunaticFamily.getInstance();

    public void debug(String msg) {
        if(PluginConfig.isDebug){
            Bukkit.getLogger().info("[" +plugin.getName() + "] [DEBUG]: " + msg);
        }
    }
    public void info(String msg) {
        Bukkit.getLogger().info("[" + plugin.getName() + "] [INFO]: " + msg);
    }

    public void warn(String msg) {
        Bukkit.getLogger().warning("[" + plugin.getName() + "] [WARN]: " + msg);
    }

    public void error(String msg) {
        Bukkit.getLogger().severe("[" + plugin.getName() + "] [ERROR]: " + msg);
    }
}
