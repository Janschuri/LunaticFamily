package de.janschuri.lunaticFamily.config;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticlib.config.AbstractDatabaseConfig;
import de.janschuri.lunaticlib.utils.Mode;

import java.nio.file.Path;

public class DatabaseConfig extends AbstractDatabaseConfig {
    private static final String NAME = "lunaticfamily";
    private static final String DATABASE_FILE = "database.yml";
    private static final String DEFAULT_DATABASE_FILE = "database.yml";
    private static final String DEFAULT_PROXY_DATABASE_FILE = "proxyDatabase.yml";

    public DatabaseConfig(Path dataDirectory) {
        super(NAME, dataDirectory, DATABASE_FILE, (LunaticFamily.getMode() == Mode.PROXY || LunaticFamily.getMode() == Mode.BACKEND) ? DEFAULT_PROXY_DATABASE_FILE : DEFAULT_DATABASE_FILE);
        this.load();
    }
}
