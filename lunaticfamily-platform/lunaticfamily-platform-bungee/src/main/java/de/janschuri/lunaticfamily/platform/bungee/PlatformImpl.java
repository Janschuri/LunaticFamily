package de.janschuri.lunaticfamily.platform.bungee;

import de.janschuri.lunaticfamily.common.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticfamily.platform.FamilyTree;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticfamily.platform.bungee.listener.JoinListener;
import de.janschuri.lunaticfamily.platform.bungee.listener.QuitListener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class PlatformImpl implements Platform<Plugin> {

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
        BungeeLunaticFamily.getInstance().getProxy().getPluginManager().registerListener(BungeeLunaticFamily.getInstance(), new JoinListener());
        BungeeLunaticFamily.getInstance().getProxy().getPluginManager().registerListener(BungeeLunaticFamily.getInstance(), new QuitListener());
    }

    @Override
    public void disable() {

    }

    @Override
    public Plugin getInstanceOfPlatform() {
        return BungeeLunaticFamily.getInstance();
    }


}
