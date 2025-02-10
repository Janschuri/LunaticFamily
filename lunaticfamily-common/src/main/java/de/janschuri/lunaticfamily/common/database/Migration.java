package de.janschuri.lunaticfamily.common.database;

import org.jetbrains.annotations.NotNull;
import org.jooq.*;

import java.util.List;

public abstract class Migration {

    public abstract void run(DSLContext context);

    protected boolean columnExists(DSLContext create, String tableName, String columnName) {
        @NotNull List<Table<?>> tables = create.meta().getTables(tableName);

        if (tables.isEmpty()) {
            return false;
        }

        Table<?> table = tables.get(0);
        for (Field<?> field : table.fields()) {
            if (field.getName().equalsIgnoreCase(columnName)) {
                return true;
            }
        }

        return false;
    }
}
