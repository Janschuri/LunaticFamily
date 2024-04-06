package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.LunaticFamily;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class SQLite extends Database {
    private final String dbname;

    public SQLite(LunaticFamily instance) {
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "lunaticfamily");
    }

    public void createTables() {
        connection = getSQLConnection();
        try {
            Statement stmt = connection.createStatement();
            for (String table : tables) {
                List<Column> columns = tableColumns.get(table);

                String sql = "CREATE TABLE IF NOT EXISTS " + table + " (" +
                        columns.stream()
                                .map(column -> "`" + column.name + "` " + column.type +
                                        (column.primaryKey ? " PRIMARY KEY" : "") +
                                        (column.notNull ? " NOT NULL" : "") +
                                        (column.defaultValue != null ? " DEFAULT " + column.defaultValue : ""))
                                .collect(Collectors.joining(", ")) +
                        columns.stream()
                                .filter(column -> column.foreignKey != null)
                                .map(column -> ", FOREIGN KEY (" + column.name + ") REFERENCES " + column.foreignKey)
                                .collect(Collectors.joining()) +
                        ")";

                stmt.execute(sql);
            }
            stmt.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db");
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        createTables();
        initialize();
    }
}
