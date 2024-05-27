package de.janschuri.lunaticfamily.common.database.tables;

import de.janschuri.lunaticfamily.common.database.Database;
import de.janschuri.lunaticlib.common.database.Datatype;
import de.janschuri.lunaticlib.common.database.Error;
import de.janschuri.lunaticlib.common.database.Table;
import de.janschuri.lunaticlib.common.database.columns.Column;
import de.janschuri.lunaticlib.common.database.columns.ForeignKey;
import de.janschuri.lunaticlib.common.database.columns.PrimaryKey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdoptionsTable {

    private static final String NAME = "adoptions";
    private static final PrimaryKey PRIMARY_KEY = new PrimaryKey("id", Datatype.INTEGER, true);

    private static final Column[] COLUMNS = {
        new ForeignKey("parentID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new ForeignKey("childID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new Column("date", Datatype.INTEGER, false, "CURRENT_TIMESTAMP"),
    };

    private static final Table TABLE = new Table(NAME, PRIMARY_KEY, COLUMNS);

    private AdoptionsTable() {
    }

    public static Table getTable() {
        return TABLE;
    }
    
    public static Connection getSQLConnection() {
        return Database.getDatabase().getSQLConnection();
    }

    public static List<Integer> getParents(int playerID) {
        List<Integer> parentsList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT parentID FROM " + NAME + " WHERE childID = ?");
            ps.setInt(1, playerID);
            rs = ps.executeQuery();

            while (rs.next()) {
                int parentID = rs.getInt("parentID");
                parentsList.add(parentID);
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

        return parentsList;
    }

    public static List<Integer> getChildren(int parentID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> childsList = new ArrayList<>();

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT childID FROM " + NAME + " WHERE parentID = ?");
            ps.setInt(1, parentID);
            rs = ps.executeQuery();

            while (rs.next()) {
                int childID = rs.getInt("childID");
                childsList.add(childID);
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

        return childsList;
    }

    public static void saveAdoption(int parentID, int childID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO `" + NAME + "` (parentID, childID) VALUES(?,?)");
            ps.setInt(1, parentID);
            ps.setInt(2, childID);
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

    public static void deleteAdoption(int parentID, int childID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM `" + NAME + "` WHERE parentID = ? AND childID = ?");
            ps.setInt(1, parentID);
            ps.setInt(2, childID);
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
