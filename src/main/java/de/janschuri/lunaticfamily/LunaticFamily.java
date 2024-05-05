package de.janschuri.lunaticfamily;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticfamily.config.DatabaseConfig;
import de.janschuri.lunaticfamily.config.Language;
import de.janschuri.lunaticfamily.config.PluginConfig;
import de.janschuri.lunaticfamily.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticfamily.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticfamily.handler.FamilyTree;
import de.janschuri.lunaticfamily.utils.Logger;
import de.janschuri.lunaticlib.LunaticLib;
import de.janschuri.lunaticlib.futurerequests.FutureRequest;
import de.janschuri.lunaticlib.futurerequests.FutureRequestsHandler;
import de.janschuri.lunaticlib.utils.Mode;

import java.nio.file.Path;
import java.util.UUID;

public final class LunaticFamily {
    public static BiMap<UUID, UUID> marryRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> marryPriestRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> marryPriest = HashBiMap.create();
    public static BiMap<UUID, UUID> adoptRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> siblingRequests = HashBiMap.create();
    public static boolean isDebug;
    public static boolean enabledProxy = false;
    public static boolean installedCrazyAdvancementsAPI = false;

    static final String[] commands = {
        "family",
        "marry",
        "gender",
        "adopt",
        "sibling",
    };

    private static final FutureRequest[] futureRequests = {
        new UpdateFamilyTreeRequest(),
        new SpawnParticlesCloudRequest(),
    };

    private static Path dataDirectory;
    static Mode mode = Mode.STANDALONE;

    public static Mode getMode() {
        return mode;
    }

    public static void setDataDirectory(Path dataDirectory) {
        LunaticFamily.dataDirectory = dataDirectory;
    }

    public static Path getDataDirectory() {
        return dataDirectory;
    }

    public static boolean loadConfig() {

        new PluginConfig(dataDirectory);

        if (PluginConfig.isUseProxy() && mode != Mode.PROXY) {
            enabledProxy = true;
            mode = Mode.BACKEND;
        }

        if (mode != Mode.BACKEND) {
            new Language(dataDirectory, commands);
            new DatabaseConfig(dataDirectory);
        }


        if (mode != Mode.PROXY) {
            if (PluginConfig.isUseCrazyAdvancementAPI() || LunaticFamily.getMode() == Mode.BACKEND) {
                Logger.infoLog("Loading family tree...");
                loadCrazyAdvancementsAPI();
            }
        }

        if (mode != Mode.BACKEND) {
            registerCommands();
        }
        return true;
    }

    public static void onEnable() {
        Logger.infoLog("LunaticFamily enabled.");
    }

    static void onDisable() {
        LunaticFamily.unregisterRequests();
        Logger.infoLog("LunaticFamily disabled.");
    }

    public static void registerRequests() {
        for (FutureRequest request : futureRequests) {
            FutureRequestsHandler.registerRequest(request);
        }
    }

    public static void unregisterRequests() {
        for (FutureRequest request : futureRequests) {
            FutureRequestsHandler.unregisterRequest(request.getRequestName());
        }
    }

    public static void loadCrazyAdvancementsAPI() {
        if (!installedCrazyAdvancementsAPI) {
            Logger.errorLog("CrazyAdvancementsAPI is not installed! Please install CrazyAdvancementsAPI or disable it in plugin config.yml.");
            return;
        }
        FamilyTree.loadAdvancementMap();
        Logger.infoLog("Loaded family tree.");
    }

    private static void registerCommands() {
        switch (LunaticLib.getPlatform()) {
            case VELOCITY:
                VelocityLunaticFamily.registerCommands();
                break;
            case PAPER:
                PaperLunaticFamily.registerCommands();
                break;
            case BUNGEE:
                BungeeLunaticFamily.registerCommands();
                break;
            default:
                Logger.errorLog("Platform not supported");
        }
    }
}
