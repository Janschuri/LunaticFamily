package de.janschuri.lunaticfamily.database;

import de.janschuri.lunaticfamily.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
            "`uuid` varchar(36) NOT NULL," +
            "`name` varchar(16) NULL," +
            "`skinURL` varchar(127)," +
            "`partner` varchar(36) NULL," +
            "`sibling` varchar(36) NULL," +
            "`firstParent` varchar(36) NULL," +
            "`secondParent` varchar(36) NULL," +
            "`firstChild` varchar(36) NULL," +
            "`secondChild` varchar(36) NULL," +
            "`gender` varchar(2) NULL," +
            "PRIMARY KEY (`uuid`)" +
            ");";
}
