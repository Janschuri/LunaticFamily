package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticfamily.platform.bukkit.listener.JoinListener;
import de.janschuri.lunaticfamily.platform.bukkit.listener.QuitListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class PlatformImpl implements Platform<JavaPlugin> {
    @Override
    public boolean spawnParticlesCloud(UUID uuid, double[] position, String particleString) {
        if (Bukkit.getPlayer(uuid) == null) {
            return false;
        } else {
            Particle particle = Particle.valueOf(particleString.toUpperCase(Locale.ROOT));
            World world = Bukkit.getPlayer(uuid).getWorld();
            Location location = new Location(world, position[0], position[1], position[2]);

            Random random = new Random();

            double range = 2.0;

            for (int i = 0; i < 10; i++) {
                double offsetX = (random.nextDouble() - 0.5) * range * 2;
                double offsetY = (random.nextDouble() - 0.5) * range * 2;
                double offsetZ = (random.nextDouble() - 0.5) * range * 2;

                Location particleLocation = location.clone().add(offsetX, offsetY, offsetZ);

                world.spawnParticle(particle, particleLocation, 1);
            }
            return true;
        }
    }

    public FamilyTreeManagerImpl getFamilyTree() {
        if (!BukkitLunaticFamily.isInstalledCrazyAdvancementsAPI()) {
            Logger.errorLog("FamilyTree is null. Please check if CrazyAdvancementsAPI is installed or disable it!");
            return null;
        }

        return new FamilyTreeManagerImpl();
    }

    @Override
    public void registerListener() {
        Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(), BukkitLunaticFamily.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new QuitListener(), BukkitLunaticFamily.getInstance());
    }

    @Override
    public void disable() {
        Bukkit.getServer().getPluginManager().disablePlugin(BukkitLunaticFamily.getInstance());
    }

    @Override
    public JavaPlugin getInstanceOfPlatform() {
        return BukkitLunaticFamily.getInstance();
    }
}
