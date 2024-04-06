package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.utils.Logger;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MySQL extends Database {
    String host, database, username, password;
    int port;

    public MySQL(LunaticFamily instance) {
        super(instance);
        host = plugin.getConfig().getString("Database.MySQL.Host", "localhost");
        port = plugin.getConfig().getInt("Database.MySQL.Port", 3306);
        database = plugin.getConfig().getString("Database.MySQL.Database", "lunaticfamily");
        username = plugin.getConfig().getString("Database.MySQL.Username", "root");
        password = plugin.getConfig().getString("Database.MySQL.Password", "");
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

                Logger.debugLog(sql);
                stmt.execute(sql);

                for (Column column : columns) {
                    addMissingColumnsTable(table, column);
                }
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean columnExists(String tableName, String columnName) {
        try {
            String queryTableInfoSQL = "SHOW COLUMNS FROM " + tableName + " LIKE '" + columnName + "'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryTableInfoSQL);

            boolean exists = rs.next();

            rs.close();
            stmt.close();

            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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
            plugin.getLogger().log(Level.SEVERE, "MySQL exception on initialize", ex);
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        //            Statement stmtPlayerData = connection.createStatement();
//            stmtPlayerData.executeUpdate(MySQLCreatePlayerDataTable);
//            stmtPlayerData.close();
//
//            Statement stmtMarriages = connection.createStatement();
//            stmtMarriages.executeUpdate(MySQLCreateMarriagesTable);
//            stmtMarriages.close();
//
//            Statement stmtAdoptions = connection.createStatement();
//            stmtAdoptions.executeUpdate(MySQLCreateAdoptionsTable);
//            stmtAdoptions.close();
//
//            Statement stmtSiblinghoods = connection.createStatement();
//            stmtSiblinghoods.executeUpdate(MySQLCreateSiblinghoodsTable);
//            stmtSiblinghoods.close();
        createTables();
        initialize();
    }

    String MySQLCreatePlayerDataTable = "CREATE TABLE IF NOT EXISTS playerData (" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "`uuid` varchar(36) NOT NULL," +
            "`name` varchar(16) NULL," +
            "`skinURL` varchar(127)," +
            "`gender` varchar(2) NULL," +
            "`background` varchar(127) NULL" +
            ") AUTO_INCREMENT=1;";

    String MySQLCreateMarriagesTable = "CREATE TABLE IF NOT EXISTS marriages (" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "`player1ID` INT," +
            "`player2ID` INT," +
            "`priest` INT NULL," +
            "`date` DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (`player1ID`) REFERENCES playerData(`id`) ON DELETE CASCADE," +
            "FOREIGN KEY (`player2ID`) REFERENCES playerData(`id`) ON DELETE CASCADE," +
            "FOREIGN KEY (`priest`) REFERENCES playerData(`id`) ON DELETE SET NULL" +
            ") AUTO_INCREMENT=1;";

    String MySQLCreateAdoptionsTable = "CREATE TABLE IF NOT EXISTS adoptions (" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "`parentID` INT," +
            "`childID` INT," +
            "`date` DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (`parentID`) REFERENCES playerData(`id`) ON DELETE CASCADE," +
            "FOREIGN KEY (`childID`) REFERENCES playerData(`id`) ON DELETE CASCADE" +
            ") AUTO_INCREMENT=1;";

    String MySQLCreateSiblinghoodsTable = "CREATE TABLE IF NOT EXISTS siblinghoods (" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "`player1ID` INT," +
            "`player2ID` INT," +
            "`date` DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (`player1ID`) REFERENCES playerData(`id`) ON DELETE CASCADE," +
            "FOREIGN KEY (`player2ID`) REFERENCES playerData(`id`) ON DELETE CASCADE" +
            ") AUTO_INCREMENT=1;";
}

