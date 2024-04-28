package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.DatabaseConfig;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticlib.utils.Mode;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public abstract class Database {
    Connection connection;
    private static Database db;
    public String playerData = "playerData";
    public String marriages = "marriages";
    public String adoptions = "adoptions";
    public String siblinghoods = "siblinghoods";
    public abstract Connection getSQLConnection();

    public static boolean loadDatabase(Path dataDirectory) {
        if (DatabaseConfig.useMySQL) {
            db = new MySQL();
            if (db.getSQLConnection() == null) {
                Logger.errorLog("Error initializing MySQL database.");
                if (LunaticFamily.getMode() == Mode.PROXY) {
                    Logger.errorLog("Proxy mode requires a MySQL database. Please check your configuration and try again.");
                    return false;
                }
                if (LunaticFamily.getMode() == Mode.BACKEND) {
                    Logger.errorLog("Backend mode requires a MySQL database. Please check your configuration and try again.");
                    return false;
                }
                Logger.warnLog("Falling back to SQLite due to initialization error");
                db = new SQLite(dataDirectory);
            } else {
                Logger.infoLog("Successfully initialized MySQL database");
            }
        } else {
            db = new SQLite(dataDirectory);
            Logger.infoLog("Successfully initialized SQLite database");
        }
        db.load();
        return true;
    }

    public static Database getDatabase() {
        return db;
    }

    public abstract void load();
    protected final List<String> tables = List.of("playerData", "marriages", "adoptions", "siblinghoods");
    protected final Map<String, List<Column>> tableColumns = Map.of(
            "playerData", List.of(
                    new Column("id", "INTEGER", true, true),
                    new Column("uuid", "varchar(36)", true),
                    new Column("name", "varchar(16)"),
                    new Column("skinURL", "varchar(127)"),
                    new Column("gender", "varchar(2)"),
                    new Column("background", "varchar(127)")
            ),
            "marriages", List.of(
                    new Column("id", "INTEGER", true, true),
                    new Column("player1ID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("player2ID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("priest", "INT", false, "playerData(id) ON DELETE SET NULL"),
                    new Column("heart", "varchar(127)"),
                    new Column("date", "DATETIME", "CURRENT_TIMESTAMP", true)
            ),
            "adoptions", List.of(
                    new Column("id", "INTEGER", true, true),
                    new Column("parentID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("childID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("date", "DATETIME", "CURRENT_TIMESTAMP", true)
            ),
            "siblinghoods", List.of(
                    new Column("id", "INTEGER", true, true),
                    new Column("player1ID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("player2ID", "INT", true, "playerData(id) ON DELETE CASCADE"),
                    new Column("date", "DATETIME", "CURRENT_TIMESTAMP", true)
            )
    );

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement psPlayerData = connection.prepareStatement("SELECT * FROM " + playerData);
            ResultSet rsPlayerData = psPlayerData.executeQuery();
            close(psPlayerData, rsPlayerData);

            PreparedStatement psMarriages = connection.prepareStatement("SELECT * FROM " + marriages);
            ResultSet rsMarriages = psMarriages.executeQuery();
            close(psMarriages, rsMarriages);

            PreparedStatement psAdoptions = connection.prepareStatement("SELECT * FROM " + adoptions);
            ResultSet rsAdoptions = psAdoptions.executeQuery();
            close(psAdoptions, rsAdoptions);

            PreparedStatement psSiblinghoods = connection.prepareStatement("SELECT * FROM " + siblinghoods);
            ResultSet rsSiblinghoods = psSiblinghoods.executeQuery();
            close(psSiblinghoods, rsSiblinghoods);

        } catch (SQLException ex) {
            Error.noConnection(ex);
        }
    }

    public int getID(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE uuid = '" + uuid.toString() + "';");

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

    public UUID getUUID(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE id = '" + id + "';");

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

    public String getName(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE id = '" + id + "';");

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

    public UUID getUUID(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE LOWER(name) = LOWER(?);");
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


    public String getSkinURL(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE id = '" + id + "';");

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

    public int getPartner(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player1ID, player2ID FROM " + marriages + " WHERE player1ID = ? OR player2iD = ?");
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

    public Timestamp getMarryDate(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player1ID, player2ID, date FROM " + marriages + " WHERE player1ID = ? OR player2ID = ?");
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

    public int getSibling(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player1ID, player2ID FROM " + siblinghoods + " WHERE player1ID = ? OR player2iD = ?");
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

    public List<Integer> getMarryList(int page, int pageSize) {
        List<Integer> marryList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            int offset = (page - 1) * pageSize;
            ps = conn.prepareStatement("SELECT player1ID FROM " + marriages + " LIMIT ? OFFSET ?;");
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

    public int getPriest(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player1ID, player2ID, priest FROM " + marriages + " WHERE player1ID = ? OR player2ID = ?");
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

    public String getGender(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE id = '" + id + "';");

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

    public String getBackground(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE id = '" + id + "';");

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

    public String getMarriageHeartColor(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM `" + marriages + "` WHERE player1ID = ? OR player2ID = ?");
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

    public List<Integer> getParents(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> parentsList = new ArrayList<>();

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT parentID FROM " + adoptions + " WHERE childID = ?");
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

    public List<Integer> getChildren(int parentID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> childsList = new ArrayList<>();

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT childID FROM " + adoptions + " WHERE parentID = ?");
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
    public void updatePlayerData(int id, String uuid, String name, String skinURL, String gender, String background) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE `" + playerData + "` SET uuid=?, name=?, skinURL=?, gender=?, background=? WHERE id=?");
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


    public void savePlayerData(String uuid, String name, String skinURL, String gender, String background) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO `" + playerData + "` (uuid,name,skinURL,gender,background) VALUES(?,?,?,?,?)");
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

    public void saveMarriage(int player1ID, int player2ID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO `" + marriages + "` (player1ID, player2ID) VALUES(?,?)");
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

    public void saveMarriage(int player1ID, int player2ID, int priest) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO `" + marriages + "` (player1ID, player2ID, priest) VALUES(?,?,?)");
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

    public void saveMarriageHeartColor(int playerID, String hexColor) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE `" + marriages + "` SET heart = ? WHERE player1ID = ? OR player2ID = ?");
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

    public void deleteMarriage(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM `" + marriages + "` WHERE player1ID = ? OR player2iD = ?");
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

    public void saveSiblinghood(int player1ID, int player2ID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO `" + siblinghoods + "` (player1ID, player2ID) VALUES(?,?)");
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

    public void deleteSiblinghood(int playerID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM `" + siblinghoods + "` WHERE player1ID = ? OR player2iD = ?");
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

    public void saveAdoption(int parentID, int childID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO `" + adoptions + "` (parentID, childID) VALUES(?,?)");
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

    public void deleteAdoption(int parentID, int childID) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM `" + adoptions + "` WHERE parentID = ? AND childID = ?");
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

    public void deletePlayerData(String uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM `" + playerData + "` WHERE uuid = ?");
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


    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(ex);
        }
    }
}
