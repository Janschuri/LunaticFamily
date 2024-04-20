package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.config.DatabaseConfig;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MySQL extends Database {
    String host, database, username, password;
    int port;

    public MySQL() {
        host = DatabaseConfig.host;
        port = DatabaseConfig.port;
        database = DatabaseConfig.database;
        username = DatabaseConfig.username;
        password = DatabaseConfig.password;
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
                                        (column.notNull ? " NOT NULL" : "") +
                                        (column.autoIncrement ? " AUTO_INCREMENT" : "") +
                                        (column.primaryKey ? " PRIMARY KEY" : "") +
                                        (column.defaultValue != null ? " DEFAULT " + column.defaultValue : ""))
                                .collect(Collectors.joining(", ")) +
                        columns.stream()
                                .filter(column -> column.foreignKey != null)
                                .map(column -> ", FOREIGN KEY (`" + column.name + "`) REFERENCES " + column.foreignKey)
                                .collect(Collectors.joining()) +
                        ") AUTO_INCREMENT=1;";

                stmt.execute(sql);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getSQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/", username, password);

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
            stmt.close();

            conn.setCatalog(database);

            return conn;
        } catch (SQLException | ClassNotFoundException ex) {
            Error.errorOnInitilization(ex);
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        createTables();
        initialize();
    }
}

