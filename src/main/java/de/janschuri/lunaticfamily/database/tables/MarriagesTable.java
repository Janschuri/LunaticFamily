package de.janschuri.lunaticfamily.database.tables;

import de.janschuri.lunaticfamily.database.Database;
import de.janschuri.lunaticlib.database.Datatype;
import de.janschuri.lunaticlib.database.Error;
import de.janschuri.lunaticlib.database.Table;
import de.janschuri.lunaticlib.database.columns.Column;
import de.janschuri.lunaticlib.database.columns.ForeignKey;
import de.janschuri.lunaticlib.database.columns.PrimaryKey;

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
        new Column("date", Datatype.TIMESTAMP, false, "CURRENT_TIMESTAMP")
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

    public static int getPartner(int id) {
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

    public static Timestamp getMarryDate(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player1ID, player2ID, date FROM " + NAME + " WHERE player1ID = ? OR player2ID = ?");
            ps.setInt(1, id);
            ps.setInt(2, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                int player1 = rs.getInt("player1ID");
                int player2 = rs.getInt("player2ID");
                Timestamp date = rs.getTimestamp("date");
                if (player1 == id || player2 == id) {
                    return date;
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
        return null;
    }

    public static List<Integer> getMarryList(int page, int pageSize) {
        List<Integer> marryList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            int offset = (page - 1) * pageSize;
            ps = conn.prepareStatement("SELECT player1ID FROM " + NAME + " LIMIT ? OFFSET ?;");
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            rs = ps.executeQuery();
            while (rs.next()) {
                int player1 = rs.getInt("player1ID");
                marryList.add(player1);
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
        return marryList;
    }

    public static int getPriest(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player1ID, player2ID, priest FROM " + NAME + " WHERE player1ID = ? OR player2ID = ?");
            ps.setInt(1, playerID);
            ps.setInt(2, playerID);
            rs = ps.executeQuery();
            while (rs.next()) {
                int player1 = rs.getInt("player1ID");
                int player2 = rs.getInt("player2ID");
                int priest = rs.getInt("priest");
                if (player1 == playerID || player2 == playerID) {
                    return priest;
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

    public static String getMarriageHeartColor(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM `" + NAME + "` WHERE player1ID = ? OR player2ID = ?");
            ps.setInt(1, playerID);
            ps.setInt(2, playerID);

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("player1ID") == playerID || rs.getInt("player2ID") == playerID) {
                    if (rs.getString("heart") == null) {
                        return "#FFFFFF";
                    }
                    return rs.getString("heart");
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
        return "#FFFFFF";
    }

    public static void saveMarriage(int player1ID, int player2ID) {
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
    }

    public static void saveMarriage(int player1ID, int player2ID, int priest) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO `" + NAME + "` (player1ID, player2ID, priest) VALUES(?,?,?)");
            ps.setInt(1, player1ID);
            ps.setInt(2, player2ID);
            ps.setInt(3, priest);
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

    public static void saveMarriageHeartColor(int playerID, String hexColor) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE `" + NAME + "` SET heart = ? WHERE player1ID = ? OR player2ID = ?");
            ps.setString(1, hexColor);
            ps.setInt(2, playerID);
            ps.setInt(3, playerID);
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
}
