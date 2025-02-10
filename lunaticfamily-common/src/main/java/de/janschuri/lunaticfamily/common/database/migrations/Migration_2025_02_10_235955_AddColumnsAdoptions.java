package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class Migration_2025_02_10_235955_AddColumnsAdoptions extends Migration {


    public void run(DSLContext context) {
        String tableName = "adoptions";

        if (!columnExists(context, tableName, "parentID")) {
            context.alterTable(tableName)
                    .addColumn("parentID", SQLDataType.INTEGER.nullable(false))
                    .execute();
        }

        if (!columnExists(context, tableName, "childID")) {
            context.alterTable(tableName)
                    .addColumn("childID", SQLDataType.INTEGER.nullable(false))
                    .execute();
        }

        if (!columnExists(context, tableName, "priest")) {
            context.alterTable(tableName)
                    .addColumn("priest", SQLDataType.INTEGER.nullable(true))
                    .execute();
        }

        if (!columnExists(context, tableName, "emoji")) {
            context.alterTable(tableName)
                    .addColumn("emoji", SQLDataType.VARCHAR(255).nullable(true))
                    .execute();
        }

        if (!columnExists(context, tableName, "date")) {
            context.alterTable(tableName)
                    .addColumn("date", SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.currentTimestamp()))
                    .execute();
        }

        if (!columnExists(context, tableName, "unadoptDate")) {
            context.alterTable(tableName)
                    .addColumn("unadoptDate", SQLDataType.TIMESTAMP.nullable(true))
                    .execute();
        }
    }
}
