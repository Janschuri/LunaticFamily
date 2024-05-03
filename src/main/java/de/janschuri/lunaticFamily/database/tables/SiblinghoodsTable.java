package de.janschuri.lunaticFamily.database.tables;

import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticlib.database.Datatype;
import de.janschuri.lunaticlib.database.Error;
import de.janschuri.lunaticlib.database.Table;
import de.janschuri.lunaticlib.database.columns.Column;
import de.janschuri.lunaticlib.database.columns.ForeignKey;
import de.janschuri.lunaticlib.database.columns.PrimaryKey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SiblinghoodsTable {

    private static final String NAME = "siblinghoods";
    private static final PrimaryKey PRIMARY_KEY = new PrimaryKey("id", Datatype.INTEGER, true);

    private static final Column[] columns = {
        new ForeignKey("player1ID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new ForeignKey("player2ID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new Column("date", Datatype.INTEGER, false, "CURRENT_TIMESTAMP"),
    };

    private static final Table TABLE = new Table(NAME, PRIMARY_KEY, columns);

    private SiblinghoodsTable() {
    }

    public static Table getTable() {
        return TABLE;
    }

    private static Connection getSQLConnection() {
        return Database.getDatabase().getSQLConnection();
    }

    public static int getSibling(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player1ID, player2ID FROM " + NAME + " WHERE player1ID = ? OR player2iD = ?");
            ps.setInt(1, id);
            ps.setInt(2, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                int player1 = rs.getInt("player1ID");
                int player2 = rs.getInt("player2ID");
                if (player1 == id) {
                    return player2;
                } else if (player2 == id) {
                    return player1;
                }
            }
        } catch (SQLException ex) {
            Error.execute(ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Error.close(ex);
            }
        }
        return 0;
    }

    public static void saveSiblinghood(int player1ID, int player2ID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO `" + NAME + "` (player1ID, player2ID) VALUES(?,?)");
            ps.setInt(1, player1ID);
            ps.setInt(2, player2ID);
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            Error.execute(ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Error.close(ex);
            }
        }
        return;
    }

    public static void deleteSiblinghood(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM `" + NAME + "` WHERE player1ID = ? OR player2iD = ?");
            ps.setInt(1, playerID);
            ps.setInt(2, playerID);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Error.execute(ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Error.close(ex);
            }
        }
    }
}
