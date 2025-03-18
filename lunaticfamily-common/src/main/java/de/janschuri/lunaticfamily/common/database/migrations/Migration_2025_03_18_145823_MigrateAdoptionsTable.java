package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class Migration_2025_03_18_145823_MigrateAdoptionsTable extends Migration {


    public void run(DSLContext context) {
        String tableName = "lunaticfamily_adoptions";
        context.createTableIfNotExists(tableName)
                .column("id", SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true))
                .column("parent_id", SQLDataType.BIGINTUNSIGNED.nullable(false))
                .column("child_id", SQLDataType.BIGINTUNSIGNED.nullable(false))
                .column("priest_id", SQLDataType.BIGINTUNSIGNED.nullable(true))
                .column("emoji_color", SQLDataType.VARCHAR(7).nullable(true))
                .column("date", SQLDataType.TIMESTAMP.nullable(false).default_(DSL.currentTimestamp()))
                .column("unadopt_date", SQLDataType.TIMESTAMP.nullable(true))
                .primaryKey("id")
                .execute();

        String oldTableName = "adoptions";
        if (!tableExists(context, oldTableName)) {
            return;
        }

        context.insertInto(
                        DSL.table(tableName),
                        DSL.field("id"),
                        DSL.field("parent_id"),
                        DSL.field("child_id"),
                        DSL.field("priest_id"),
                        DSL.field("emoji_color"),
                        DSL.field("date"),
                        DSL.field("unadopt_date")
                )
                .select(context.select(
                                DSL.field("id"),
                                DSL.field("parentID").as("parent_id"),
                                DSL.field("childID").as("child_id"),
                                DSL.field("priest").as("priest_id"),
                                DSL.field("emoji").as("emoji_color"),
                                DSL.field("date"),
                                DSL.field("unadoptDate").as("unadopt_date"))
                        .from(DSL.table(oldTableName))
                )
                .execute();

        context.dropTableIfExists(oldTableName).execute();
    }
}
