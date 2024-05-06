package de.janschuri.lunaticfamily.config;

import de.janschuri.lunaticlib.config.AbstractDatabaseConfig;

import java.nio.file.Path;

public class DatabaseConfig extends AbstractDatabaseConfig {
    private static final String NAME = "lunaticfamily";
    private static final String DATABASE_FILE = "database.yml";
    private static final String DEFAULT_DATABASE_FILE = "database.yml";

    public DatabaseConfig(Path dataDirectory) {
        super(NAME, dataDirectory, DATABASE_FILE, DEFAULT_DATABASE_FILE);
        load();
    }
}