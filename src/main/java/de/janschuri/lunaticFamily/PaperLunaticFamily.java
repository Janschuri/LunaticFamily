package de.janschuri.lunaticFamily;

import de.janschuri.lunaticFamily.commands.paper.*;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.listener.paper.JoinListener;
import de.janschuri.lunaticFamily.listener.paper.QuitListener;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.utils.Mode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class PaperLunaticFamily extends JavaPlugin {

    private static PaperLunaticFamily instance;

    @Override
    public void onEnable() {
        instance = this;
        LunaticFamily.registerRequests();
        LunaticFamily.setDataDirectory(getDataFolder().toPath());

        if (Utils.classExists("eu.endercentral.crazy_advancements.CrazyAdvancementsAPI")) {
            LunaticFamily.installedCrazyAdvancementsAPI = true;
        }

        if (!LunaticFamily.loadConfig()) {
            disable();
            return;
        }

        if (LunaticFamily.getMode() == Mode.STANDALONE) {

            getServer().getPluginManager().registerEvents(new JoinListener(), this);
            getServer().getPluginManager().registerEvents(new QuitListener(), this);

            if (!Database.loadDatabase()) {
                disable();
            }
        } else {
            Logger.infoLog("Backend mode enabled.");
        }
        LunaticFamily.onEnable();
    }
    public static PaperLunaticFamily getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        LunaticFamily.onDisable();
    }

    private static void disable() {
        Logger.errorLog("Disabling LunaticFamily...");
        Bukkit.getServer().getPluginManager().disablePlugin(getInstance());
    }

    public static boolean spawnParticleCloud(UUID uuid, double[] position, String particleString) {
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

    static void registerCommands() {

        for (String command : LunaticFamily.commands) {

            switch (command) {
                case "family":
                    getInstance().getCommand(command).setExecutor(new FamilyCommand());
                    getInstance().getCommand(command).setTabCompleter(new FamilyCommand());
                    break;
                case "adopt":
                    getInstance().getCommand(command).setExecutor(new AdoptCommand());
                    getInstance().getCommand(command).setTabCompleter(new AdoptCommand());
                    break;
                case "gender":
                    getInstance().getCommand(command).setExecutor(new GenderCommand());
                    getInstance().getCommand(command).setTabCompleter(new GenderCommand());
                    break;
                case "marry":
                    getInstance().getCommand(command).setExecutor(new MarryCommand());
                    getInstance().getCommand(command).setTabCompleter(new MarryCommand());
                    break;
                case "sibling":
                    getInstance().getCommand(command).setExecutor(new SiblingCommand());
                    getInstance().getCommand(command).setTabCompleter(new SiblingCommand());
                    break;
            }

            getInstance().getCommand(command).setAliases(Language.getLanguage().getAliases(command));
            getInstance().getCommand(command).setPermission("lunaticfamily." + command);
        }
    }

}
