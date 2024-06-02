package de.janschuri.lunaticfamily.common.config;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FamilyTreeJSON {

    private static String content;

    private FamilyTreeJSON() {
    }

    public static boolean loadFamilyTreeJSON() {

        InputStream inputStream = LunaticFamily.class.getResourceAsStream("/familyTree.json");

        if (inputStream == null) {
            Logger.errorLog("Resource not found: familyTree.json");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            content = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            return true;
        } catch (IOException e) {
            Logger.errorLog("Error reading resource: familyTree.json");
            return false;
        }
    }

    public static String getContent() {
        return content;
    }
}
