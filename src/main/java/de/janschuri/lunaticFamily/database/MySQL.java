package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.Main;

import java.sql.*;
import java.util.logging.Level;

public class MySQL extends Database {
    String host, database, username, password;
    int port;

    public MySQL(Main instance) {
        super(instance);
        host = plugin.getConfig().getString("Database.MySQL.Host", "localhost");
        port = plugin.getConfig().getInt("Database.MySQL.Port", 3306);
        database = plugin.getConfig().getString("Database.MySQL.Database", "lunaticfamily");
        username = plugin.getConfig().getString("Database.MySQL.Username", "root");
        password = plugin.getConfig().getString("Database.MySQL.Password", "");
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
        try {
            Statement stmtPlayerData = connection.createStatement();
            stmtPlayerData.executeUpdate(MySQLCreatePlayerDataTable);
            stmtPlayerData.close();

            Statement stmtMarriages = connection.createStatement();
            stmtMarriages.executeUpdate(MySQLCreateMarriagesTable);
            stmtMarriages.close();

            Statement stmtAdoptions = connection.createStatement();
            stmtAdoptions.executeUpdate(MySQLCreateAdoptionsTable);
            stmtAdoptions.close();

            Statement stmtSiblinghoods = connection.createStatement();
            stmtSiblinghoods.executeUpdate(MySQLCreateSiblinghoodsTable);
            stmtSiblinghoods.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }

    String MySQLCreatePlayerDataTable = "CREATE TABLE IF NOT EXISTS playerData (" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "`uuid` varchar(36) NOT NULL," +
            "`name` varchar(16) NULL," +
            "`skinURL` varchar(127)," +
            "`firstParent` INT DEFAULT 0," +
            "`secondParent` INT DEFAULT 0," +
            "`firstChild` INT DEFAULT 0," +
            "`secondChild` INT DEFAULT 0," +
            "`gender` varchar(2) NULL," +
            "`background` varchar(127) NULL" +
            ") AUTO_INCREMENT=1;";

    String MySQLCreateMarriagesTable = "CREATE TABLE IF NOT EXISTS marriages (" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "`player1ID` INT DEFAULT 0," +
            "`player2ID` INT DEFAULT 0," +
            "`date` DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ") AUTO_INCREMENT=1;";

    String MySQLCreateAdoptionsTable = "CREATE TABLE IF NOT EXISTS adoptions (" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "`parentID` INT DEFAULT 0," +
            "`childID` INT DEFAULT 0," +
            "`date` DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ") AUTO_INCREMENT=1;";

    String MySQLCreateSiblinghoodsTable = "CREATE TABLE IF NOT EXISTS siblinghoods (" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "`player1ID` INT DEFAULT 0," +
            "`player2ID` INT DEFAULT 0," +
            "`date` DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ") AUTO_INCREMENT=1;";
}

