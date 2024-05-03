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
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;

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



        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        if (LunaticFamily.getMode() == Mode.STANDALONE) {

            for (String command : LunaticFamily.commands) {
                Command cmd = getCommand(command);
                assert cmd != null;
                try {
                    final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                    bukkitCommandMap.setAccessible(true);
                    CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                    List<String> list = Language.getInstance().getAliases(command);

                    list.forEach(alias -> {
                        commandMap.register(alias, instance.getName(), cmd);
                    });
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            getCommand("family").setExecutor(new FamilyCommand());
            getCommand("family").setTabCompleter(new FamilyCommand());
            getCommand("family").setPermission("lunaticfamily.family");

            getCommand("adopt").setExecutor(new AdoptCommand());
            getCommand("adopt").setTabCompleter(new AdoptCommand());
            getCommand("adopt").setPermission("lunaticfamily.adopt");

            getCommand("gender").setExecutor(new GenderCommand());
            getCommand("gender").setTabCompleter(new GenderCommand());
            getCommand("gender").setPermission("lunaticfamily.gender");

            getCommand("marry").setExecutor(new MarryCommand());
            getCommand("marry").setTabCompleter(new MarryCommand());
            getCommand("marry").setPermission("lunaticfamily.marry");

            getCommand("sibling").setExecutor(new SiblingCommand());
            getCommand("sibling").setTabCompleter(new SiblingCommand());
            getCommand("sibling").setPermission("lunaticfamily.sibling");

        } else {
            Logger.infoLog("Backend mode enabled.");
        }

        if (!Database.loadDatabase()) {
            disable();
        }
    }
    public static PaperLunaticFamily getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        LunaticFamily.unregisterRequests();
        Logger.infoLog("LunaticFamily disabled.");
    }

    private static void disable() {
        Logger.errorLog("Disabling LunaticFamily...");
        Bukkit.getServer().getPluginManager().disablePlugin(getInstance());
    }

}
