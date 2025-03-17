package de.janschuri.lunaticfamily.common.config;

import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.config.LunaticDatabaseConfig;
import de.janschuri.lunaticlib.platform.PlatformType;

import java.nio.file.Path;

public class DatabaseConfig extends LunaticDatabaseConfig {
    private static final String NAME = "lunaticfamily";
    private static final String DATABASE_FILE = "database.yml";

    public DatabaseConfig(Path dataDirectory) {
        super(dataDirectory, DATABASE_FILE);
    }

    public void load() {
        super.load();
    }

    @Override
    protected String defaultName() {
        return getString("database", NAME);
    }
}
