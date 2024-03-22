package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.LunaticFamily;

import java.util.logging.Level;

public class Error {
    public static void execute(LunaticFamily plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }

    public static void close(LunaticFamily plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}