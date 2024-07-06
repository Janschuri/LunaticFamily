package de.janschuri.lunaticfamily.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticlib.common.utils.Mode;
import de.janschuri.lunaticlib.platform.velocity.external.Metrics;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Callable;

@Plugin(
        id = "lunaticfamily",
        name = "LunaticFamily",
        version = "1.2.4",
        authors = "janschuri",
        dependencies = {
        @Dependency(id = "lunaticlib")

        }
)
public class VelocityLunaticFamily {

    private static ProxyServer proxy;
    private static Path dataDirectory;
    private static VelocityLunaticFamily instance;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("lunaticfamily:proxy");
    private final Metrics.Factory metricsFactory;


    @Inject
    public VelocityLunaticFamily(ProxyServer proxy, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        VelocityLunaticFamily.proxy = proxy;
        VelocityLunaticFamily.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    public static ProxyServer getProxy() {
        return proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        instance = this;

        proxy.getChannelRegistrar().register(IDENTIFIER);

        int pluginId = 21914;
        Metrics metrics = metricsFactory.make(this, pluginId);


        Mode mode = Mode.PROXY;
        Platform platform = new PlatformImpl();
        Path dataDirectory =  VelocityLunaticFamily.dataDirectory;

        LunaticFamily.onEnable(dataDirectory, mode, platform);

        metrics.addCustomChart(new Metrics.AdvancedPie("marriages", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                return LunaticFamily.getMarriagesStats();
            }
        }));

        metrics.addCustomChart(new Metrics.AdvancedPie("adoptions", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                return LunaticFamily.getAdoptionsStats();
            }
        }));

        metrics.addCustomChart(new Metrics.AdvancedPie("siblings", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                return LunaticFamily.getSiblinghoodsStats();
            }
        }));

        metrics.addCustomChart(new Metrics.SingleLineChart("married_players", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return LunaticFamily.getMarriedPlayersCount();
            }
        }));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        LunaticFamily.onDisable();
    }

    public static VelocityLunaticFamily getInstance() {
        return instance;
    }
}

