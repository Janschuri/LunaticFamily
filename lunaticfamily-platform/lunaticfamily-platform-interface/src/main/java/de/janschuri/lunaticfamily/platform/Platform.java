package de.janschuri.lunaticfamily.platform;

import java.util.UUID;

public interface Platform<P> {

    boolean spawnParticlesCloud(UUID uuid, double[] position, String particleString);
    FamilyTree getFamilyTree();
    void registerListener();
    void disable();
    P getInstanceOfPlatform();
}
