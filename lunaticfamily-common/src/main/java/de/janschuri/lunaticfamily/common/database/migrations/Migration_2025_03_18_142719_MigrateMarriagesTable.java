package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class Migration_2025_03_18_142719_MigrateMarriagesTable extends Migration {


    public void run(DSLContext context) {
        String tableName = "lunaticfamily_marriages";
        context.createTableIfNotExists(tableName)
                .column("id", SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true))
                .column("player1_id", SQLDataType.BIGINTUNSIGNED.nullable(false))
                .column("player2_id", SQLDataType.BIGINTUNSIGNED.nullable(false))
                .column("priest_id", SQLDataType.BIGINTUNSIGNED.nullable(true))
                .column("emoji_color", SQLDataType.VARCHAR(7).nullable(true))
                .column("date", SQLDataType.TIMESTAMP.nullable(false).default_(DSL.currentTimestamp()))
                .column("divorce_date", SQLDataType.TIMESTAMP.nullable(true))
                .primaryKey("id")
                .execute();

        String oldTableName = "marriages";
        if (!tableExists(context, oldTableName)) {
            return;
        }

        context.insertInto(
                        DSL.table(tableName),
                        DSL.field("id"),
                        DSL.field("player1_id"),
                        DSL.field("player2_id"),
                        DSL.field("priest_id"),
                        DSL.field("emoji_color"),
                        DSL.field("date"),
                        DSL.field("divorce_date")
                )
                .select(context.select(
                                DSL.field("id"),
                                DSL.field("player1ID").as("player1_id"),
                                DSL.field("player2ID").as("player2_id"),
                                DSL.field("priest").as("priest_id"),
                                DSL.field("heart").as("emoji_color"),
                                DSL.field("date"),
                                DSL.field("divorceDate").as("divorce_date"))
                        .from(DSL.table(oldTableName))
                )
                .execute();

        context.dropTableIfExists(oldTableName).execute();
    }
}
