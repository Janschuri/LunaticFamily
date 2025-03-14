package de.janschuri.lunaticfamily.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticfamily.common.commands.adopt.Adopt;
import de.janschuri.lunaticfamily.common.commands.family.*;
import de.janschuri.lunaticfamily.common.commands.gender.Gender;
import de.janschuri.lunaticfamily.common.commands.marry.Marry;
import de.janschuri.lunaticfamily.common.commands.priest.Priest;
import de.janschuri.lunaticfamily.common.commands.sibling.Sibling;
import de.janschuri.lunaticfamily.common.config.ConfigImpl;
import de.janschuri.lunaticfamily.common.config.FamilyTreeJSON;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.futurerequests.*;
import de.janschuri.lunaticfamily.common.handler.Adoption;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.handler.Marriage;
import de.janschuri.lunaticfamily.common.handler.Siblinghood;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.Platform;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequest;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequestsHandler;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public final class LunaticFamily {
    public static BiMap<UUID, UUID> marryRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> marryPriestRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> marryPriests = HashBiMap.create();
    public static BiMap<UUID, UUID> adoptRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> adoptPriestRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> adoptPriests = HashBiMap.create();
    public static BiMap<UUID, UUID> siblingRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> siblingPriestRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> siblingPriests = HashBiMap.create();
    public static boolean isDebug;

    private static LanguageConfigImpl languageConfig;
    private static ConfigImpl config;

    private static final FutureRequest[] futureRequests = {
        new UpdateFamilyTreeRequest(),
        new SpawnParticlesCloudRequest(),
        new LoadFamilyTreeMapRequest(),
        new IsFamilyTreeMapLoadedRequest(),
        new GetPlaceholderRequest(),
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

                if (DatabaseRepository.init()) {
                    Logger.infoLog("Database loaded.");
                } else {
                    Logger.errorLog("Database could not be loaded.");
                }
        }
        return true;
    }

    public static void onEnable(Path dataDirectory, Mode mode, Platform platform) {
        LunaticFamily.dataDirectory = dataDirectory;
        LunaticFamily.mode = mode;
        LunaticFamily.platform = platform;


        loadConfig();

        if (LunaticFamily.mode != Mode.BACKEND) {
            registerCommands();
            platform.registerListener();
        }

        registerRequests();

        Logger.infoLog("LunaticFamily enabled.");
    }

    public static Platform getPlatform() {
        return platform;
    }

    public static void onDisable() {
        DatabaseRepository.shutdown();
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
        Logger.infoLog("Registering commands...");
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new Family());
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new Adopt());
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new Sibling());
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new Marry());
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new Gender());
        LunaticLib.getPlatform().registerCommand(getPlatform().getInstanceOfPlatform(), new Priest());
    }

    public static LanguageConfigImpl getLanguageConfig() {
        return languageConfig;
    }

    public static ConfigImpl getConfig() {
        return config;
    }

    public static Map<String, Integer> getMarriagesStats() {
        int totalMarriages = DatabaseRepository.getDatabase().find(Marriage.class).findCount();
        int marriages = DatabaseRepository.getDatabase().find(Marriage.class).where().isNull("divorceDate").findCount();
        int divorced = totalMarriages - marriages;

        return Map.of("Marriages", marriages, "Divorced Marriages", divorced);
    }

    public static Map<String, Integer> getSiblinghoodsStats() {
        int totalSiblings = DatabaseRepository.getDatabase().find(Siblinghood.class).findCount();
        int siblings = DatabaseRepository.getDatabase().find(Siblinghood.class).where().isNull("unsiblingDate").findCount();
        int unsiblinged = totalSiblings - siblings;

        return Map.of("Siblings", siblings, "Unsiblinged Siblings", unsiblinged);
    }

    public static Map<String, Integer> getAdoptionsStats() {
        int totalAdoptions = DatabaseRepository.getDatabase().find(Adoption.class).findCount();
        int adoptions = DatabaseRepository.getDatabase().find(Adoption.class).where().isNull("unadoptDate").findCount();
        int unadopted = totalAdoptions - adoptions;

        return Map.of("Adoptions", adoptions, "Unadopted Adoptions", unadopted);
    }

    public static int getMarriedPlayersCount() {
        return DatabaseRepository.getDatabase().find(Marriage.class).where().isNull("divorceDate").findCount() * 2;
    }

    public static FamilyPlayer getFamilyPlayer(UUID uuid) {
        return FamilyPlayer.findOrCreate(uuid);
    }

    public static FamilyPlayer getFamilyPlayer(int id) {
        return FamilyPlayer.find(id);
    }
}
