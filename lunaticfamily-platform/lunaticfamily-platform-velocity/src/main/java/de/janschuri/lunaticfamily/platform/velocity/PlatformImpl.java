package de.janschuri.lunaticfamily.platform.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import de.janschuri.lunaticfamily.common.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticfamily.platform.velocity.listener.JoinListener;
import de.janschuri.lunaticfamily.platform.velocity.listener.QuitListener;

import java.util.UUID;

public class PlatformImpl implements Platform<PluginContainer> {
    @Override
    public boolean spawnParticlesCloud(UUID uuid, double[] position, String particleString) {
        return new SpawnParticlesCloudRequest().get(uuid, position, particleString)
                .thenApply(s -> s)
                .join();
    }

    @Override
    public FamilyTreeManager getFamilyTreeManager() {
        return new FamilyTreeManagerImpl();
    }

    @Override
    public void registerListener() {
        VelocityLunaticFamily.getProxy().getEventManager().register(VelocityLunaticFamily.getInstance(), new QuitListener());
        VelocityLunaticFamily.getProxy().getEventManager().register(VelocityLunaticFamily.getInstance(), new JoinListener());
    }

    @Override
    public void disable() {

    }

    @Override
    public PluginContainer getInstanceOfPlatform() {
        return VelocityLunaticFamily.getInstance();
    }
}
