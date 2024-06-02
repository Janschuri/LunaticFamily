package de.janschuri.lunaticfamily;

import de.janschuri.lunaticlib.LunaticConfig;

import java.util.List;
import java.util.Map;

public interface Config extends LunaticConfig {

    String getLanguageKey();

    String getDefaultGender();

    String getDefaultBackground();

    boolean isAllowSingleAdopt();

    List<String> getServers();

    boolean isUseCrazyAdvancementAPI();

    boolean isUseVault();

    boolean isUseProxy();

    String getDateFormat();

    double getMarryKissRange();

    double getMarryProposeRange();

    double getMarryPriestRange();

    double getAdoptProposeRange();

    double getSiblingProposeRange();

    List<String> getSuccessCommands(String key);

    List<String> getFamilyList();

    List<String> getBackgrounds();

    Double getCommandWithdraw(String key);

    Map<String, String> getColors();

    String getColor(String key);
}
