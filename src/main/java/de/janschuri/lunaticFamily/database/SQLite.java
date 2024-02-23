package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.Main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;



public class SQLite extends Database{
    String dbname;
    public SQLite(Main instance){
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "playerData");
    }

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS playerData (" +
            "`id` INTEGER PRIMARY KEY," +
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
            "`fake` INT DEFAULT 0," +
            ");";


    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}
