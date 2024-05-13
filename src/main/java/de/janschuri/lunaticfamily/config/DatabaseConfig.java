package de.janschuri.lunaticfamily.config;

import de.janschuri.lunaticlib.LunaticLib;
import de.janschuri.lunaticlib.config.AbstractDatabaseConfig;
import de.janschuri.lunaticlib.utils.Platform;

import java.nio.file.Path;

public class DatabaseConfig extends AbstractDatabaseConfig {
    private static final String NAME = "lunaticfamily";
    private static final String DATABASE_FILE = "database.yml";
    private static final String DEFAULT_DATABASE_FILE = "database.yml";
    private static final String DATABASE_CONFIG_VELOCITY = "database-velocity.yml";

    public DatabaseConfig(Path dataDirectory) {
        super(NAME, dataDirectory, DATABASE_FILE, LunaticLib.getPlatform() == Platform.VELOCITY ? DATABASE_CONFIG_VELOCITY : DEFAULT_DATABASE_FILE);
        load();
    }
}
