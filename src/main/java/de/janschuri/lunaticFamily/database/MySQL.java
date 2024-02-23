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
        database = plugin.getConfig().getString("Database.MySQL.Database", "playerData");
        username = plugin.getConfig().getString("Database.MySQL.Username", "root");
        password = plugin.getConfig().getString("Database.MySQL.Password", "");
    }

    public Connection getSQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        } catch (SQLException | ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "MySQL exception on initialize", ex);
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(MySQLCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }

    String MySQLCreateTokensTable = "CREATE TABLE IF NOT EXISTS playerData (" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            "`uuid` varchar(36) NOT NULL," +
            "`name` varchar(16) NULL," +
            "`skinURL` varchar(127)," +
            "`partner` INT DEFAULT 0," +
            "`marryDate` DATETIME NULL," +
            "`sibling` INT DEFAULT 0," +
            "`firstParent` INT DEFAULT 0," +
            "`secondParent` INT DEFAULT 0," +
            "`firstChild` INT DEFAULT 0," +
            "`secondChild` INT DEFAULT 0," +
            "`gender` varchar(2) NULL," +
            "`background` varchar(127) NULL," +
            "`fake` INT DEFAULT 0" +
            ") AUTO_INCREMENT=1;";
}

