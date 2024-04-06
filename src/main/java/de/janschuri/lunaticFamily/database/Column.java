package de.janschuri.lunaticFamily.database;

public class Column {
    public final String name;
    public final boolean primaryKey;
    public final boolean autoIncrement;
    public final boolean notNull;
    public final String type;
    public final String defaultValue;
    public final String foreignKey;


    // primary key
    public Column(String name, String type, boolean primaryKey, boolean autoIncrement) {
        this.name = name;
        this.primaryKey = primaryKey;
        this.autoIncrement = autoIncrement;
        this.notNull = false;
        this.type = type;
        this.defaultValue = null;
        this.foreignKey = null;
    }

    // foreign key
    public Column(String name, String type, boolean notNull, String foreignKey) {
        this.name = name;
        this.primaryKey = false;
        this.autoIncrement = false;
        this.notNull = notNull;
        this.type = type;
        this.defaultValue = null;
        this.foreignKey = foreignKey;
    }

    public Column(String name, String type, String defaultValue, boolean notNull) {
        this.name = name;
        this.primaryKey = false;
        this.autoIncrement = false;
        this.notNull = notNull;
        this.type = type;
        this.defaultValue = defaultValue;
        this.foreignKey = null;
    }

    public Column(String name, String type, boolean notNull) {
        this.name = name;
        this.primaryKey = false;
        this.autoIncrement = false;
        this.notNull = notNull;
        this.type = type;
        this.defaultValue = null;
        this.foreignKey = null;
    }

    public Column(String name, String type) {
        this.name = name;
        this.primaryKey = false;
        this.autoIncrement = false;
        this.notNull = false;
        this.type = type;
        this.defaultValue = null;
        this.foreignKey = null;
    }
}
