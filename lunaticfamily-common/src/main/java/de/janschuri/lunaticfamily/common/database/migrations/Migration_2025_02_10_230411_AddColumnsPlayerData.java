package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;
import org.jooq.impl.SQLDataType;

public class Migration_2025_02_10_230411_AddColumnsPlayerData extends Migration {


    public void run(DSLContext context) {
        String tableName = "playerData";
        if (!tableExists(context, tableName)) {
            return;
        }

        if (!columnExists(context, tableName, "uuid")) {
            context.alterTable("playerData")
                    .addColumn("uuid", SQLDataType.VARCHAR(255).nullable(true))
                    .execute();
        }

        if (!columnExists(context, tableName, "name")) {
            context.alterTable("playerData")
                    .addColumn("name", SQLDataType.VARCHAR(255).nullable(true))
                    .execute();
        }

        if (!columnExists(context, tableName, "skinURL")) {
            context.alterTable("playerData")
                    .addColumn("skinURL", SQLDataType.VARCHAR(255).nullable(true))
                    .execute();
        }

        if (!columnExists(context, tableName, "gender")) {
            context.alterTable("playerData")
                    .addColumn("gender", SQLDataType.VARCHAR(255).nullable(true))
                    .execute();
        }

        if (!columnExists(context, tableName, "background")) {
            context.alterTable("playerData")
                    .addColumn("background", SQLDataType.VARCHAR(255).nullable(true))
                    .execute();
        }
    }
}
