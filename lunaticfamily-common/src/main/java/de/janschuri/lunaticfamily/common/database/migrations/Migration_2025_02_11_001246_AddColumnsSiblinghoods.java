package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class Migration_2025_02_11_001246_AddColumnsSiblinghoods extends Migration {


    public void run(DSLContext context) {
        String tableName = "siblinghoods";
        if (!tableExists(context, tableName)) {
            return;
        }

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

        if (!columnExists(context, tableName, "unsiblingDate")) {
            context.alterTable(tableName)
                    .addColumn("unsiblingDate", SQLDataType.TIMESTAMP.nullable(true))
                    .execute();
        }
    }
}
