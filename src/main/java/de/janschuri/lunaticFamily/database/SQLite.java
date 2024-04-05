package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.LunaticFamily;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class SQLite extends Database {
    private final String dbname;

    private final Map<String, Set<Column>> tables = Map.of(
            "playerData", Set.of(
                    new Column("id", "INT", true, true),
                    new Column("uuid", "varchar(36)", true),
                    new Column("name", "varchar(16)"),
                    new Column("skinURL", "varchar(127)"),
                    new Column("gender", "varchar(2)"),
                    new Column("background", "varchar(127)")
            ),
            "marriages", Set.of(
                    new Column("id", "INT", true, true),
                    new Column("player1ID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("player2ID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("priest", "INT", false, "playerData(id) ON DELETE SET NULL"),
                    new Column("heart", "varchar(127)"),
                    new Column("date", "DATETIME", "CURRENT_TIMESTAMP", true)
            ),
            "adoptions", Set.of(
                    new Column("id", "INT", true, true),
                    new Column("parentID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("childID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("date", "DATETIME", "CURRENT_TIMESTAMP", true)
            ),
            "siblinghoods", Set.of(
                    new Column("id", "INT", true, true),
                    new Column("player1ID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("player2ID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("date", "DATETIME", "CURRENT_TIMESTAMP", true)
            )
    );

    public SQLite(LunaticFamily instance) {
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "lunaticfamily");
    }

    public void createTables() {
        connection = getSQLConnection();
        try {
            Statement stmt = connection.createStatement();
            for (Map.Entry<String, Set<Column>> entry : tables.entrySet()) {
                String table = entry.getKey();
                Set<Column> columns = entry.getValue();

                String sql = "CREATE TABLE IF NOT EXISTS " + table + " (" +
                        columns.stream()
                                .map(column -> "'" + column.name + "' " + column.type +
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

                for (Column column : columns) {
                    addMissingColumnsTable(table, column);
                }
            }
            stmt.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean columnExists(String tableName, String columnName) {
        try {
            String queryTableInfoSQL = "PRAGMA table_info(" + tableName + ")";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryTableInfoSQL);

            while (rs.next()) {
                if (columnName.equals(rs.getString("name"))) {
                    return true;
                }
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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
