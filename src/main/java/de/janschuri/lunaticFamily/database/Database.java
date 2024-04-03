package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.LunaticFamily;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public abstract class Database {
    LunaticFamily plugin;
    Connection connection;
    public String playerData = "playerData";
    public String marriages = "marriages";
    public String adoptions = "adoptions";
    public String siblinghoods = "siblinghoods";

    public Database(LunaticFamily instance) {
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

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
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public int getID(String uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE uuid = '" + uuid + "';");

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("uuid").equals(uuid)) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }

    public String getUUID(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE id = '" + id + "';");

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == id) {
                    return rs.getString("uuid");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }

    public String getBackground(String uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + playerData + " WHERE uuid = '" + uuid + "';");

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("uuid").equals(uuid)) {
                    return rs.getString("background");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }

        return parentsList;
    }

    public List<Integer> getChilds(int parentID) {
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
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
            Error.close(plugin, ex);
        }
    }
}
