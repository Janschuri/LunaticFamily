package de.janschuri.lunaticfamily.common.database;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.config.DatabaseConfig;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticfamily.common.handler.Model;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.common.database.Error;
import de.janschuri.lunaticlib.common.database.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Database {

    private static de.janschuri.lunaticlib.common.database.Database db;

    private static final Table[] tables = {
        PlayerDataTable.getTable(),
        SiblinghoodsTable.getTable(),
        AdoptionsTable.getTable(),
        MarriagesTable.getTable(),
    };

    public static boolean loadDatabase() {
        Logger.infoLog("Loading database...");
        DatabaseConfig databaseConfig = new DatabaseConfig(LunaticFamily.getDataDirectory());
        databaseConfig.load();
        db = de.janschuri.lunaticlib.common.database.Database.getDatabase(databaseConfig, tables);

        return db != null;
    }

    public static de.janschuri.lunaticlib.common.database.Database getDatabase() {
        Logger.debugLog("Returning database instance.");
        return db;
    }

    public static void save(Model model) {
        Logger.debugLog("Accessing database: save(Savable savable)");

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = db.getSQLConnection();

            Map<String, Object> fields = model.getFieldsForSave();
            StringBuilder query = new StringBuilder("REPLACE INTO `" + model.getTableName() + "` (");
            StringBuilder placeholders = new StringBuilder();

            for (String fieldName : fields.keySet()) {
                query.append(fieldName).append(",");
                placeholders.append("?,");
            }

            query.setLength(query.length() - 1); // Remove the last comma
            placeholders.setLength(placeholders.length() - 1); // Remove the last comma
            query.append(") VALUES(").append(placeholders).append(")");

            ps = conn.prepareStatement(query.toString());

            int index = 1;
            for (Object value : fields.values()) {
                if (value == null) {
                    ps.setNull(index, Types.INTEGER);
                } else {
                    ps.setObject(index, value);
                }
                index++;
            }

            ps.executeUpdate();
        } catch (SQLException ex) {
            Error.execute(ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                Error.close(ex);
            }
        }
    }

    public static void delete(Model model) {
        Logger.debugLog("Accessing database: delete(Model model)");

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = db.getSQLConnection();

            Map<String, Object> primaryKey = model.getPrimaryKey();
            StringBuilder query = new StringBuilder("DELETE FROM `" + model.getTableName() + "` WHERE ");

            for (String key : primaryKey.keySet()) {
                query.append(key).append(" = ? AND ");
            }

            query.setLength(query.length() - 5); // Remove the last " AND "

            ps = conn.prepareStatement(query.toString());

            int index = 1;
            for (Object value : primaryKey.values()) {
                ps.setObject(index, value);
                index++;
            }

            ps.executeUpdate();
        } catch (SQLException ex) {
            Error.execute(ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                Error.close(ex);
            }
        }
    }

    public static Model get(Class<? extends Model> modelClass, int id) {
        Logger.debugLog("Accessing database: get(int id)");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = db.getSQLConnection();
            String tableName = Model.class.getDeclaredConstructor().newInstance().getTableName();
            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return Model.fromResultSet(modelClass, rs);
            }
        } catch (SQLException | ReflectiveOperationException ex) {
            Error.execute(ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                Error.close(ex);
            }
        }
        return null;
    }

    public static List<Model> get(Class<? extends Model> modelClass, String field, Object value) {
        Logger.debugLog("Accessing database: get(Class<? extends Model> modelClass, String field, Object value)");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Model> models = new ArrayList<>();

        try {
            conn = db.getSQLConnection();
            String tableName = modelClass.getDeclaredConstructor().newInstance().getTableName();
            String query = "SELECT * FROM " + tableName + " WHERE " + field + " = ?";
            ps = conn.prepareStatement(query);
            ps.setObject(1, value);
            rs = ps.executeQuery();

            while (rs.next()) {
                models.add(Model.fromResultSet(modelClass, rs));
            }
        } catch (SQLException | ReflectiveOperationException ex) {
            Error.execute(ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                Error.close(ex);
            }
        }
        return models;
    }
}
