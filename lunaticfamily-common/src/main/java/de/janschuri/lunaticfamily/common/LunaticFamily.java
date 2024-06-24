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
import de.janschuri.lunaticfamily.common.database.Database;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticfamily.common.futurerequests.*;
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
        int totalMarriages = MarriagesTable.getTotalMarriagesCount();
        int marriages = MarriagesTable.getMarriagesCount();
        int divorced = totalMarriages - marriages;

        return Map.of("Marriages", marriages, "Divorced Marriages", divorced);
    }

    public static Map<String, Integer> getSiblinghoodsStats() {
        int totalSiblings = SiblinghoodsTable.getTotalSiblinghoodsCount();
        int siblings = SiblinghoodsTable.getSiblinghoodsCount();
        int unsiblinged = totalSiblings - siblings;

        return Map.of("Siblings", siblings, "Unsiblinged Siblings", unsiblinged);
    }

    public static Map<String, Integer> getAdoptionsStats() {
        int totalAdoptions = AdoptionsTable.getTotalAdoptionsCount();
        int adoptions = AdoptionsTable.getAdoptionsCount();
        int unadopted = totalAdoptions - adoptions;

        return Map.of("Adoptions", adoptions, "Unadopted Adoptions", unadopted);
    }

    public static int getMarriedPlayersCount() {
        return MarriagesTable.getMarriagesCount()*2;
    }
}
