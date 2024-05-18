package de.janschuri.lunaticfamily;

import de.janschuri.lunaticfamily.commands.paper.*;
import de.janschuri.lunaticfamily.config.Language;
import de.janschuri.lunaticfamily.database.Database;
import de.janschuri.lunaticfamily.listener.paper.JoinListener;
import de.janschuri.lunaticfamily.listener.paper.QuitListener;
import de.janschuri.lunaticfamily.utils.Logger;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.bstats.MetricsBukkit;
import de.janschuri.lunaticlib.utils.Mode;
import de.janschuri.lunaticlib.utils.Platform;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class PaperLunaticFamily extends JavaPlugin {

    private static PaperLunaticFamily instance;

    @Override
    public void onEnable() {
        LunaticFamily.platform = Platform.BUKKIT;
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

        int pluginId = 21912; // <-- Replace with the id of your plugin!
        MetricsBukkit metrics = new MetricsBukkit(this, pluginId);

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

            PluginCommand cmd = getInstance().getCommand(command);
            assert cmd != null;
            try {
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                bukkitCommandMap.setAccessible(true);
                CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
                List<String> list = Language.getLanguage().getAliases(command);
                list.forEach(alias -> {
                    commandMap.register(alias, instance.getName(), cmd);
                });
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            getInstance().getCommand(command).setPermission("lunaticfamily." + command);

            switch (command) {
                case "family":
                    getInstance().getCommand(command).setPermission("lunaticfamily.family");
                    getInstance().getCommand(command).setExecutor(new FamilyCommand());
                    getInstance().getCommand(command).setTabCompleter(new FamilyCommand());
                    break;
                case "adopt":
                    getInstance().getCommand(command).setPermission("lunaticfamily.adopt");
                    getInstance().getCommand(command).setExecutor(new AdoptCommand());
                    getInstance().getCommand(command).setTabCompleter(new AdoptCommand());
                    break;
                case "gender":
                    getInstance().getCommand(command).setPermission("lunaticfamily.gender");
                    getInstance().getCommand(command).setExecutor(new GenderCommand());
                    getInstance().getCommand(command).setTabCompleter(new GenderCommand());
                    break;
                case "marry":
                    getInstance().getCommand(command).setPermission("lunaticfamily.marry");
                    getInstance().getCommand(command).setExecutor(new MarryCommand());
                    getInstance().getCommand(command).setTabCompleter(new MarryCommand());
                    break;
                case "sibling":
                    getInstance().getCommand(command).setPermission("lunaticfamily.sibling");
                    getInstance().getCommand(command).setExecutor(new SiblingCommand());
                    getInstance().getCommand(command).setTabCompleter(new SiblingCommand());
                    break;
            }
        }
    }

}
