package de.janschuri.lunaticfamily.common.database.tables;

import de.janschuri.lunaticfamily.common.database.Database;
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

public class MarriagesTable {

    private static final String NAME = "marriages";
    private static final PrimaryKey PRIMARY_KEY = new PrimaryKey("id", Datatype.INTEGER, true);

    private static final Column[] COLUMNS = {
        new ForeignKey("player1ID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new ForeignKey("player2ID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new ForeignKey("priest", Datatype.INTEGER, true, "playerData", "id", "SET NULL"),
        new Column("heart", true),
        new Column("date", Datatype.TIMESTAMP, false, "CURRENT_TIMESTAMP"),
        new Column("divorceDate", Datatype.TIMESTAMP, true, "NULL"),
    };

    private static final Table TABLE = new Table(NAME, PRIMARY_KEY, COLUMNS);

    private MarriagesTable() {
    }

    public static Table getTable() {
        return TABLE;
    }

    private static Connection getSQLConnection() {
        return Database.getDatabase().getSQLConnection();
    }

    public static Marriage getMarriage(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE (id = ?)");
            ps.setInt(1, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                int player1 = rs.getInt("player1ID");
                int player2 = rs.getInt("player2ID");
                int priest = rs.getInt("priest");
                String heart = rs.getString("heart");
                Timestamp date = rs.getTimestamp("date");
                Timestamp divorceDate = rs.getTimestamp("divorceDate");

                return new Marriage(id, player1, player2, priest, heart, date, divorceDate);
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

    public static List<Marriage> getPlayersMarriages(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Marriage> list = new ArrayList<>();

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE (player1ID = ? OR player2ID = ?) AND divorceDate IS NULL");
            ps.setInt(1, playerID);
            ps.setInt(2, playerID);

            rs = ps.executeQuery();
            while (rs.next()) {
                int player1 = rs.getInt("player1ID");
                int player2 = rs.getInt("player2ID");
                int priest = rs.getInt("priest");
                String heart = rs.getString("heart");
                Timestamp date = rs.getTimestamp("date");
                Timestamp divorceDate = rs.getTimestamp("divorceDate");

                list.add(new Marriage(rs.getInt("id"), player1, player2, priest, heart, date, divorceDate));
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

    public static List<Marriage> getMarriageList(int page, int pageSize) {
        List<Marriage> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            int offset = (page - 1) * pageSize;
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE divorceDate IS NULL LIMIT ? OFFSET ?");
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            rs = ps.executeQuery();
            while (rs.next()) {
                int player1 = rs.getInt("player1ID");
                int player2 = rs.getInt("player2ID");
                int priest = rs.getInt("priest");
                String heart = rs.getString("heart");
                Timestamp date = rs.getTimestamp("date");
                Timestamp divorceDate = rs.getTimestamp("divorceDate");
                list.add(new Marriage(rs.getInt("id"), player1, player2, priest, heart, date, divorceDate));
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

    public static void saveMarriage(int player1ID, int player2ID, int priestID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO `" + NAME + "` (player1ID, player2ID, priest) VALUES(?,?,?)");
            ps.setInt(1, player1ID);
            ps.setInt(2, player2ID);
            if (priestID < 0) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, priestID);
            }
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

    public static void deleteMarriage(int playerID) {
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

    public static void divorceMarriage(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE `" + NAME + "` SET divorceDate = CURRENT_TIMESTAMP WHERE player1ID = ? OR player2ID = ?");
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

    public static int getMarriagesCount() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME + " WHERE divorceDate IS NULL");
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

    public static int getTotalMarriagesCount() {
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

    public static int getPriestsMarriagesCount(int priestID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME + " WHERE divorceDate IS NULL AND priest = ?");
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

    public static int getPriestsTotalMarriagesCount(int priestID) {
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
