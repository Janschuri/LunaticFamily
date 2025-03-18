package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class Migration_2025_03_18_145838_MigrateSiblinghoodsTable extends Migration {


    public void run(DSLContext context) {
        String tableName = "lunaticfamily_siblinghoods";
        context.createTableIfNotExists(tableName)
                .column("id", SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true))
                .column("player1_id", SQLDataType.BIGINTUNSIGNED.nullable(false))
                .column("player2_id", SQLDataType.BIGINTUNSIGNED.nullable(false))
                .column("priest_id", SQLDataType.BIGINTUNSIGNED.nullable(true))
                .column("emoji_color", SQLDataType.VARCHAR(7).nullable(true))
                .column("date", SQLDataType.TIMESTAMP.nullable(false).default_(DSL.currentTimestamp()))
                .column("unsibling_date", SQLDataType.TIMESTAMP.nullable(true))
                .primaryKey("id")
                .execute();

        String oldTableName = "siblinghoods";
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
                        DSL.field("unsibling_date")
                )
                .select(context.select(
                                DSL.field("id"),
                                DSL.field("player1ID").as("player1_id"),
                                DSL.field("player2ID").as("player2_id"),
                                DSL.field("priest").as("priest_id"),
                                DSL.field("emoji").as("emoji_color"),
                                DSL.field("date"),
                                DSL.field("unsiblingDate").as("unsibling_date"))
                        .from(DSL.table(oldTableName))
                )
                .execute();

        context.dropTableIfExists(oldTableName).execute();
    }
}
