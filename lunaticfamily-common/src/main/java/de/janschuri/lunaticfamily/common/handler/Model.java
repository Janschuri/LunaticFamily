package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.database.Database;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Model extends Serializable {

    default String getTableName() {
        return this.getClass().getSimpleName().toLowerCase() + "s";
    }

    default Map<String, Object> getFieldsForSave() {
        Map<String, Object> fields = new HashMap<>();
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                fields.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fields;
    }

    default Map<String, Object> getPrimaryKey() {
        Map<String, Object> primaryKey = new HashMap<>();
        primaryKey.put("id", getFieldsForSave().get("id"));
        return primaryKey;
    }

    default void save() {
        Database.save(this);
    }

    default void delete() {
        Database.delete(this);
    }

    static Model get(int id) {
        Class<? extends Model> modelClass = getModelClass();
        return Database.get(modelClass, id);
    }

    static List<Model> get(String field, Object value) {
        Class<? extends Model> modelClass = getModelClass();
        return Database.get(modelClass, field, value);
    }

    static Model fromResultSet(Class<? extends Model> modelClass, ResultSet rs) throws SQLException {
        try {
            Model model = modelClass.getDeclaredConstructor().newInstance();
            Map<String, Object> fields = model.getFieldsForSave();
            for (String fieldName : fields.keySet()) {
                Field field = model.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(model, rs.getObject(fieldName));
            }
            return model;
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Error reconstructing model from ResultSet", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Model> getModelClass() {
        try {
            return (Class<? extends Model>) Class.forName(new Object() {}.getClass().getEnclosingClass().getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Model class not found", e);
        }
    }
}