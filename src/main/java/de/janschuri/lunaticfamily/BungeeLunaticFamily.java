package de.janschuri.lunaticfamily;

import de.janschuri.lunaticfamily.commands.bungee.*;
import de.janschuri.lunaticfamily.database.Database;
import de.janschuri.lunaticfamily.listener.bungee.JoinListener;
import de.janschuri.lunaticfamily.listener.bungee.QuitListener;
import de.janschuri.lunaticlib.utils.Mode;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Path;

public class BungeeLunaticFamily extends Plugin {

    private static BungeeLunaticFamily instance;

    @Override
    public void onEnable() {
        LunaticFamily.mode = Mode.PROXY;
        instance = this;


        LunaticFamily.registerRequests();

        Path dataDirectory = getDataFolder().toPath();

        LunaticFamily.setDataDirectory(dataDirectory);
        LunaticFamily.loadConfig();
        Database.loadDatabase();

        getProxy().getPluginManager().registerListener(this, new QuitListener());
        getProxy().getPluginManager().registerListener(this, new JoinListener());

        LunaticFamily.onEnable();
    }

    @Override
    public void onDisable() {
        LunaticFamily.onDisable();
    }

    public static BungeeLunaticFamily getInstance() {
        return instance;
    }

    static void registerCommands() {
        for (String command : LunaticFamily.commands) {
            switch (command) {
                case "family":
                    getInstance().getProxy().getPluginManager().registerCommand(getInstance(), new FamilyCommand());
                    break;
                case "adopt":
                    getInstance().getProxy().getPluginManager().registerCommand(getInstance(), new AdoptCommand());
                    break;
                case "gender":
                    getInstance().getProxy().getPluginManager().registerCommand(getInstance(), new GenderCommand());
                    break;
                case "marry":
                    getInstance().getProxy().getPluginManager().registerCommand(getInstance(), new MarryCommand());
                    break;
                case "sibling":
                    getInstance().getProxy().getPluginManager().registerCommand(getInstance(), new SiblingCommand());
                    break;
            }

        }
    }
}
