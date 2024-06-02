package de.janschuri.lunaticfamily.platform.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import de.janschuri.lunaticfamily.common.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticfamily.platform.FamilyTree;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticfamily.platform.velocity.listener.JoinListener;
import de.janschuri.lunaticfamily.platform.velocity.listener.QuitListener;

import java.util.UUID;

public class PlatformImpl implements Platform<ProxyServer> {
    @Override
    public boolean spawnParticlesCloud(UUID uuid, double[] position, String particleString) {
        return new SpawnParticlesCloudRequest().get(uuid, position, particleString);
    }

    @Override
    public FamilyTree getFamilyTree() {
        return new FamilyTreeImpl();
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
    public ProxyServer getInstanceOfPlatform() {
        return VelocityLunaticFamily.getProxy();
    }
}
