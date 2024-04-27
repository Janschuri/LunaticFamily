package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.utils.Logger;

public class Error {

    public static void execute(Exception ex) {
        Logger.errorLog("Couldn't execute SQL statement: " + ex);
    }

    public static void close(Exception ex) {
        Logger.errorLog("Failed to close SQL connection: " + ex);
    }

    public static void noConnection(Exception ex) {
        Logger.errorLog("Unable to retrieve SQL connection: " + ex);
    }
    public static void errorOnInitilization(Exception ex) {
        Logger.errorLog("Error on initialization: " + ex);
    }

    public static void noTable(Exception ex) {
        Logger.errorLog("Database Error: No Table Found: " + ex);
    }
}