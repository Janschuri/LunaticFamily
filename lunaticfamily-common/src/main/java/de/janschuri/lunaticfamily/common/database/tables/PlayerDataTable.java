package de.janschuri.lunaticfamily.common.database.tables;

import de.janschuri.lunaticfamily.common.database.Database;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.common.database.Datatype;
import de.janschuri.lunaticlib.common.database.Error;
import de.janschuri.lunaticlib.common.database.Table;
import de.janschuri.lunaticlib.common.database.columns.Column;
import de.janschuri.lunaticlib.common.database.columns.PrimaryKey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerDataTable {

    private static final String NAME = "playerData";
    private static final PrimaryKey PRIMARY_KEY = new PrimaryKey("id", Datatype.INTEGER, true);
    private static final Column[] COLUMNS = {
            new Column("uuid", true),
            new Column("name", true),
            new Column("skinURL", true),
            new Column("gender", true),
            new Column("background", true),
    };

    private static final Table TABLE = new Table(NAME, PRIMARY_KEY, COLUMNS);

    private PlayerDataTable() {
    }

    public static Table getTable() {
        return TABLE;
    }

    private static Connection getSQLConnection() {
        return Database.getDatabase().getSQLConnection();
    }

    public static int getID(UUID uuid) {
        Logger.debugLog("Accessing database: getID(UUID uuid)");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE uuid = ?;");
            ps.setString(1, uuid.toString());

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("uuid").equals(uuid.toString())) {
                    return rs.getInt("id");
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
        return -1;
    }

    public static UUID getUUID(int id) {
        Logger.debugLog("Accessing database: getUUID(" + id + ")");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE id = ?;");
            ps.setInt(1, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == id) {
                    return UUID.fromString(rs.getString("uuid"));
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

    public static String getName(int id) {
        Logger.debugLog("Accessing database: getName(" + id + ")");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE id = ?;");
            ps.setInt(1, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == id) {
                    return rs.getString("name");
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

    public static UUID getUUID(String name) {
        Logger.debugLog("Accessing database: getUUID(String name)");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE LOWER(name) = LOWER(?);");
            ps.setString(1, name);

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(name)) {
                    return UUID.fromString(rs.getString("uuid"));
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

    public static String getSkinURL(int id) {
        Logger.debugLog("Accessing database: getSkinURL(int id)");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE id = ?;");
            ps.setInt(1, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == id) {
                    return rs.getString("skinURL");
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

    public static String getGender(int id) {
        Logger.debugLog("Accessing database: getGender(int id)");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE id = ?;");
            ps.setInt(1, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == id) {
                    return rs.getString("gender");
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

    public static String getBackground(int id) {
        Logger.debugLog("Accessing database: getBackground(int id)");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " WHERE id = ?;");
            ps.setInt(1, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == id) {
                    return rs.getString("background");
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

    public static void updatePlayerData(int id, String uuid, String name, String skinURL, String gender, String background) {
        Logger.debugLog("Accessing database: updatePlayerData(int id, String uuid, String name, String skinURL, String gender, String background)");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE `" + NAME + "` SET uuid = ?, name = ?, skinURL = ?, gender = ?, background = ? WHERE id = ?");
            ps.setString(1, uuid);
            ps.setString(2, name);
            ps.setString(3, skinURL);
            ps.setString(4, gender);
            ps.setString(5, background);
            ps.setInt(6, id);
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

    public static void savePlayerData(String uuid, String name, String skinURL, String gender, String background) {
        Logger.debugLog("Accessing database: savePlayerData(String uuid, String name, String skinURL, String gender, String background)");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO `" + NAME + "` (uuid,name,skinURL,gender,background) VALUES(?,?,?,?,?)");
            ps.setString(1, uuid);
            ps.setString(2, name);
            ps.setString(3, skinURL);
            ps.setString(4, gender);
            ps.setString(5, background);
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

    public static void deletePlayerData(String uuid) {
        Logger.debugLog("Accessing database: deletePlayerData(String uuid)");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM `" + NAME + "` WHERE uuid = ?");
            ps.setString(1, uuid);
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

    public static Map<Integer, Map<String, String>> getPlayerList(int page) {
        Logger.debugLog("Accessing database: getPlayerList(int page)");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        Map<Integer, Map<String, String>> players = new HashMap<>();

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + NAME + " ORDER BY id DESC LIMIT ?,?");
            ps.setInt(1, (page - 1) * 10);
            ps.setInt(2, page * 10);

            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, String> player = new HashMap<>();
                int id = rs.getInt("id");
                player.put("id", String.valueOf(id));
                player.put("uuid", rs.getString("uuid"));
                player.put("name", rs.getString("name"));
                player.put("skinURL", rs.getString("skinURL"));
                player.put("gender", rs.getString("gender"));
                player.put("background", rs.getString("background"));
                players.put(id, player);
            }

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

        return players;
    }
}
