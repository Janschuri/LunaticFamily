package de.janschuri.lunaticfamily.common.database.tables;

import de.janschuri.lunaticfamily.common.database.Database;
import de.janschuri.lunaticfamily.common.handler.Siblinghood;
import de.janschuri.lunaticlib.common.database.Datatype;
import de.janschuri.lunaticlib.common.database.Error;
import de.janschuri.lunaticlib.common.database.Table;
import de.janschuri.lunaticlib.common.database.columns.Column;
import de.janschuri.lunaticlib.common.database.columns.ForeignKey;
import de.janschuri.lunaticlib.common.database.columns.PrimaryKey;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SiblinghoodsTable {

    private static final String NAME = "siblinghoods";
    private static final PrimaryKey PRIMARY_KEY = new PrimaryKey("id", Datatype.INTEGER, true);

    private static final Column[] columns = {
        new ForeignKey("player1ID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new ForeignKey("player2ID", Datatype.INTEGER, false, "playerData", "id", "CASCADE"),
        new ForeignKey("priest", Datatype.INTEGER, true, "playerData", "id", "SET NULL"),
        new Column("emoji", true),
        new Column("date", Datatype.INTEGER, false, "CURRENT_TIMESTAMP"),
        new Column("unsiblingDate", Datatype.TIMESTAMP, true, "NULL"),
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

    public static Siblinghood getSiblinghood(int id) {
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
                String emoji = rs.getString("emoji");
                Timestamp date = rs.getTimestamp("date");
                Timestamp unsiblingDate = rs.getTimestamp("unsiblingDate");

                return new Siblinghood(id, player1, player2, priest, emoji, date, unsiblingDate);
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

    public static List<Siblinghood> getPlayersSiblinghoods(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Siblinghood> list = new ArrayList<>();

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE (player1ID = ? OR player2ID = ?) AND unsiblingDate IS NULL");
            ps.setInt(1, playerID);
            ps.setInt(2, playerID);

            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int player1 = rs.getInt("player1ID");
                int player2 = rs.getInt("player2ID");
                int priest = rs.getInt("priest");
                String heart = rs.getString("emoji");
                Timestamp date = rs.getTimestamp("date");
                Timestamp unsiblingDate = rs.getTimestamp("unsiblingDate");

                list.add(new Siblinghood(id, player1, player2, priest, heart, date, unsiblingDate));
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

    public static int getSibling(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player1ID, player2ID FROM " + NAME + " WHERE (player1ID = ? OR player2iD = ?) AND unsiblingDate IS NULL");
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

    public static void saveSiblinghood(int player1ID, int player2ID, int priestID) {
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

    public static void unsiblingSiblinghood(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE `" + NAME + "` SET unsiblingDate = CURRENT_TIMESTAMP WHERE player1ID = ? OR player2ID = ?");
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

    public static int getSiblinghoodsCount() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME + " WHERE unsiblingDate IS NULL");
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

    public static int getTotalSiblinghoodsCount() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME);
            rs = ps.executeQuery();
            while (rs.next()) {
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

    public static List<Siblinghood> getSiblinghoodList(int page, int pageSize) {
        List<Siblinghood> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            int offset = (page - 1) * pageSize;
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE unsiblingDate IS NULL LIMIT ? OFFSET ?");
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int player1 = rs.getInt("player1ID");
                int player2 = rs.getInt("player2ID");
                int priest = rs.getInt("priest");
                String emoji = rs.getString("emoji");
                Timestamp date = rs.getTimestamp("date");
                Timestamp unsiblingDate = rs.getTimestamp("unsiblingDate");

                list.add(new Siblinghood(id, player1, player2, priest, emoji, date, unsiblingDate));
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

    public static void saveEmojiColor(int id, String hexColor) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE `" + NAME + "` SET emoji = ? WHERE id = ?");
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

    public static int getPriestsSiblinghoodsCount(int priestID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME + " WHERE unsiblingDate IS NULL AND priest = ?");
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

    public static int getPriestsTotalSiblinghoodsCount(int priestID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM " + NAME + " WHERE priest = ?");
            ps.setInt(1, priestID);
            rs = ps.executeQuery();
            while (rs.next()) {
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
