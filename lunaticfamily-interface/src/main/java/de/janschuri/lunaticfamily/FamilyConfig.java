package de.janschuri.lunaticfamily;


import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FamilyConfig {

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

    Set<String> getFamilyList();

    List<String> getBackgrounds();

    Double getCommandWithdraw(String key);

    Map<String, String> getColors();

    String getColor(String key);
}
