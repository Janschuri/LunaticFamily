package de.janschuri.lunaticfamily.platform.bungee;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticlib.common.utils.Mode;
import de.janschuri.lunaticlib.platform.bungee.external.Metrics;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Path;

public class BungeeLunaticFamily extends Plugin {

    private static BungeeLunaticFamily instance;

    @Override
    public void onEnable() {
        instance = this;

        int pluginId = 21918;
        Metrics metrics = new Metrics(this, pluginId);

        Mode mode = Mode.PROXY;
        Platform platform = new PlatformImpl();
        Path dataDirectory = getDataFolder().toPath();

        LunaticFamily.onEnable(dataDirectory, mode, platform);
    }

    @Override
    public void onDisable() {
        LunaticFamily.onDisable();
    }

    public static BungeeLunaticFamily getInstance() {
        return instance;
    }
}
