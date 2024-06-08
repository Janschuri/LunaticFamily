package de.janschuri.lunaticfamily.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticfamily.common.commands.family.*;
import de.janschuri.lunaticfamily.common.config.ConfigImpl;
import de.janschuri.lunaticfamily.common.config.FamilyTreeJSON;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.database.Database;
import de.janschuri.lunaticfamily.common.futurerequests.IsFamilyTreeMapLoadedRequest;
import de.janschuri.lunaticfamily.common.futurerequests.LoadFamilyTreeMapRequest;
import de.janschuri.lunaticfamily.common.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticfamily.common.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequest;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequestsHandler;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.nio.file.Path;
import java.util.UUID;

public final class LunaticFamily {
    public static BiMap<UUID, UUID> marryRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> marryPriestRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> marryPriest = HashBiMap.create();
    public static BiMap<UUID, UUID> adoptRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> siblingRequests = HashBiMap.create();
    public static boolean isDebug;

    private static LanguageConfigImpl languageConfig;
    private static de.janschuri.lunaticfamily.Config config;

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
        new LoadFamilyTreeMapRequest(),
        new IsFamilyTreeMapLoadedRequest(),
    };

    private static Path dataDirectory;
    private static Mode mode = Mode.STANDALONE;
    private static Platform platform;

    public static Mode getMode() {
        return mode;
    }

    public static Path getDataDirectory() {
        return dataDirectory;
    }

    public static boolean loadConfig() {

        config = new ConfigImpl(dataDirectory);
        config.load();

        if (config.isUseProxy()) {
            mode = Mode.BACKEND;
            Logger.infoLog("Backend mode enabled.");
            return true;
        }

        if (mode != Mode.BACKEND) {
            String languageKey = config.getLanguageKey();
            languageConfig = new LanguageConfigImpl(dataDirectory, languageKey);
            languageConfig.load();
        }
        return true;
    }

    public static void onEnable(Path dataDirectory, Mode mode, Platform platform) {
        LunaticFamily.dataDirectory = dataDirectory;
        LunaticFamily.mode = mode;
        LunaticFamily.platform = platform;


        loadConfig();

        Logger.debugLog("Mode: " + LunaticFamily.mode);

        registerRequests();

        if (LunaticFamily.mode != Mode.BACKEND) {
            Database.loadDatabase();

            FamilyTreeJSON.loadFamilyTreeJSON();

            if (config.isUseCrazyAdvancementAPI()) {
                platform.getFamilyTree().loadFamilyTreeMap(FamilyTreeJSON.getContent());
            }
            registerCommands();
            platform.registerListener();
        }

        Logger.infoLog("LunaticFamily enabled.");
    }

    public static Platform getPlatform() {
        return platform;
    }

    public static void onDisable() {
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

    public static void registerCommands() {
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new FamilySubcommand());
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new AdoptSubcommand());
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new SiblingSubcommand());
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new MarrySubcommand());
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new GenderSubcommand());
    }

    public static LanguageConfigImpl getLanguageConfig() {
        return languageConfig;
    }

    public static de.janschuri.lunaticfamily.Config getConfig() {
        return config;
    }
}
