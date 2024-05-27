package de.janschuri.lunaticfamily.common.config;

import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.config.AbstractDatabaseConfig;
import de.janschuri.lunaticlib.platform.PlatformType;

import java.nio.file.Path;

public class DatabaseConfig extends AbstractDatabaseConfig {
    private static final String NAME = "lunaticfamily";
    private static final String DATABASE_FILE = "database.yml";
    private static final String DEFAULT_DATABASE_FILE = "database.yml";
    private static final String DATABASE_CONFIG_VELOCITY = "database-velocity.yml";

    public DatabaseConfig(Path dataDirectory) {
        super(NAME, dataDirectory, DATABASE_FILE, LunaticLib.getPlatform().getPlatformType() == PlatformType.VELOCITY ? DATABASE_CONFIG_VELOCITY : DEFAULT_DATABASE_FILE);
    }
}
