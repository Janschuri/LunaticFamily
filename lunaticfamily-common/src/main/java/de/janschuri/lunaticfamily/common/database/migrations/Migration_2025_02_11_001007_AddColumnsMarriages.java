package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class Migration_2025_02_11_001007_AddColumnsMarriages extends Migration {


    public void run(DSLContext context) {
        String tableName = "marriages";

        if (!columnExists(context, tableName, "player1ID")) {
            context.alterTable(tableName)
                    .addColumn("player1ID", SQLDataType.INTEGER.nullable(false))
                    .execute();
        }

        if (!columnExists(context, tableName, "player2ID")) {
            context.alterTable(tableName)
                    .addColumn("player2ID", SQLDataType.INTEGER.nullable(false))
                    .execute();
        }

        if (!columnExists(context, tableName, "priest")) {
            context.alterTable(tableName)
                    .addColumn("priest", SQLDataType.INTEGER.nullable(true))
                    .execute();
        }

        if (!columnExists(context, tableName, "heart")) {
            context.alterTable(tableName)
                    .addColumn("heart", SQLDataType.VARCHAR(255).nullable(true))
                    .execute();
        }

        if (!columnExists(context, tableName, "date")) {
            context.alterTable(tableName)
                    .addColumn("date", SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.currentTimestamp()))
                    .execute();
        }

        if (!columnExists(context, tableName, "divorceDate")) {
            context.alterTable(tableName)
                    .addColumn("divorceDate", SQLDataType.TIMESTAMP.nullable(true))
                    .execute();
        }
    }
}
