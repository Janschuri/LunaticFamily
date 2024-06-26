package de.janschuri.lunaticfamily.common.database.tables;

import de.janschuri.lunaticfamily.common.database.Database;
import de.janschuri.lunaticfamily.common.handler.Adoption;
import de.janschuri.lunaticfamily.common.handler.Marriage;
import de.janschuri.lunaticlib.common.database.Datatype;
import de.janschuri.lunaticlib.common.database.Error;
import de.janschuri.lunaticlib.common.database.Table;
import de.janschuri.lunaticlib.common.database.columns.Column;
import de.janschuri.lunaticlib.common.database.columns.ForeignKey;
import de.janschuri.lunaticlib.common.database.columns.PrimaryKey;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdoptionsTable {

    private static final String NAME = "adoptions";
    private static final PrimaryKey PRIMARY_KEY = new PrimaryKey("id", Datatype.INTEGER, true);

    private static final Column[] COLUMNS = {
        new ForeignKey("parentID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new ForeignKey("childID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new ForeignKey("priest", Datatype.INTEGER, true, "playerData", "id", "SET NULL"),
        new Column("emoji", true),
        new Column("date", Datatype.INTEGER, false, "CURRENT_TIMESTAMP"),
        new Column("unadoptDate", Datatype.TIMESTAMP_NULL, true, "NULL"),
    };

    private static final Table TABLE = new Table(NAME, PRIMARY_KEY, COLUMNS);

    private AdoptionsTable() {
    }

    public static Adoption getAdoption(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE (id = ?)");
            ps.setInt(1, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                int player1 = rs.getInt("parentID");
                int player2 = rs.getInt("childID");
                int priest = rs.getInt("priest");
                String emoji = rs.getString("emoji");
                Timestamp date = rs.getTimestamp("date");
                Timestamp unsiblingDate = rs.getTimestamp("unadoptDate");

                return new Adoption(id, player1, player2, priest, emoji, date, unsiblingDate);
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
        return null;
    }

    public static List<Adoption> getPlayerAsParentAdoptions(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Adoption> list = new ArrayList<>();

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE (parentID = ?)");
            ps.setInt(1, playerID);

            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int parent = rs.getInt("parentID");
                int child = rs.getInt("childID");
                int priest = rs.getInt("priest");
                String emoji = rs.getString("emoji");
                Timestamp date = rs.getTimestamp("date");
                Timestamp unadoptDate = rs.getTimestamp("unadoptDate");

                list.add(new Adoption(id, parent, child, priest, emoji, date, unadoptDate));
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
        return list;
    }

    public static List<Adoption> getPlayerAsChildAdoptions(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Adoption> list = new ArrayList<>();

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE (childID = ?)");
            ps.setInt(1, playerID);

            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int parent = rs.getInt("parentID");
                int child = rs.getInt("childID");
                int priest = rs.getInt("priest");
                String emoji = rs.getString("emoji");
                Timestamp date = rs.getTimestamp("date");
                Timestamp unadoptDate = rs.getTimestamp("unadoptDate");

                list.add(new Adoption(id, parent, child, priest, emoji, date, unadoptDate));
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
        return list;
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
            ps = conn.prepareStatement("SELECT parentID FROM " + NAME + " WHERE childID = ? AND unadoptDate IS NULL");
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
            ps = conn.prepareStatement("SELECT childID FROM " + NAME + " WHERE parentID = ? AND unadoptDate IS NULL");
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

    public static void unadoptAdoption(int playerID, int childID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE `" + NAME + "` SET unadoptDate = CURRENT_TIMESTAMP WHERE parentID = ? AND childID = ?");
            ps.setInt(1, playerID);
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

    public static int getAdoptionsCount() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME + " WHERE unadoptDate IS NULL");
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
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

    public static int getTotalAdoptionsCount() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
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

    public static List<Adoption> getAdoptionList(int page, int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Adoption> adoptionsList = new ArrayList<>();

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE unadoptDate IS NULL LIMIT ? OFFSET ?");
            ps.setInt(1, limit);
            ps.setInt(2, (page - 1) * limit);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int parentID = rs.getInt("parentID");
                int childID = rs.getInt("childID");
                int priest = rs.getInt("priest");
                String emoji = rs.getString("emoji");
                Timestamp date = rs.getTimestamp("date");
                Timestamp unadoptDate = rs.getTimestamp("unadoptDate");

                Adoption adoption = new Adoption(id, parentID, childID, priest, emoji, date, unadoptDate);
                adoptionsList.add(adoption);
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

        return adoptionsList;
    }

    public static void saveEmojiColor(int id, String hexColor) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE `" + NAME + "` SET heart = ? WHERE id = ?");
            ps.setString(1, hexColor);
            ps.setInt(2, id);
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

    public static int getPriestsAdoptionsCount(int priestID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME + " WHERE unadoptDate IS NULL AND priest = ?");
            ps.setInt(1, priestID);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
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

    public static int getPriestsTotalAdoptionsCount(int priestID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME + " WHERE priest = ?");
            ps.setInt(1, priestID);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
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
}
