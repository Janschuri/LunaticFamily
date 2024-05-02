package de.janschuri.lunaticFamily;

import de.janschuri.lunaticFamily.commands.bungee.*;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.listener.bungee.JoinListener;
import de.janschuri.lunaticFamily.listener.bungee.QuitListener;
import de.janschuri.lunaticlib.utils.Mode;
import de.janschuri.lunaticFamily.utils.Logger;
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
        Database.loadDatabase(dataDirectory);

        getProxy().getPluginManager().registerListener(this, new QuitListener());
        getProxy().getPluginManager().registerListener(this, new JoinListener());

        getProxy().getPluginManager().registerCommand(this, new FamilyCommand());
        getProxy().getPluginManager().registerCommand(this, new AdoptCommand());
        getProxy().getPluginManager().registerCommand(this, new GenderCommand());
        getProxy().getPluginManager().registerCommand(this, new MarryCommand());
        getProxy().getPluginManager().registerCommand(this, new SiblingCommand());




        Logger.infoLog("LunaticFamily enabled.");
    }

    @Override
    public void onDisable() {
        LunaticFamily.unregisterRequests();
        getLogger().info("LunaticFamily disabled.");
    }

    public static BungeeLunaticFamily getInstance() {
        return instance;
    }
}
