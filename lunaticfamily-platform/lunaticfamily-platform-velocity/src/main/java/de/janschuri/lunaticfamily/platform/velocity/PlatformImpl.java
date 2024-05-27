package de.janschuri.lunaticfamily.platform.velocity;

import de.janschuri.lunaticfamily.platform.FamilyTree;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticfamily.platform.velocity.listener.JoinListener;
import de.janschuri.lunaticfamily.platform.velocity.listener.QuitListener;

import java.util.UUID;

public class PlatformImpl implements Platform {
    @Override
    public boolean spawnParticlesCloud(UUID uuid, double[] position, String particleString) {
        return false;
    }

    @Override
    public FamilyTree getFamilyTree() {
        return null;
    }

    @Override
    public void registerListener() {
        VelocityLunaticFamily.getProxy().getEventManager().register(this, new QuitListener());
        VelocityLunaticFamily.getProxy().getEventManager().register(this, new JoinListener());
    }

    @Override
    public void disable() {

    }

    @Override
    public Object getInstanceOfPlatform() {
        return null;
    }
}
