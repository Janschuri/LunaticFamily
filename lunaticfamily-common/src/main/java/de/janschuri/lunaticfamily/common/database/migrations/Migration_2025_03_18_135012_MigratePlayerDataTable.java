package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class Migration_2025_03_18_135012_MigratePlayerDataTable extends Migration {

    public void run(DSLContext context) {
        String tableName = "lunaticfamily_players";
        context.createTableIfNotExists(tableName)
                .column("id", SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true))
                .column("uuid", SQLDataType.UUID.nullable(false))
                .column("name", SQLDataType.VARCHAR(16).nullable(false).default_("unknown-name"))
                .column("skin_url", SQLDataType.VARCHAR(255).nullable(true))
                .column("gender", SQLDataType.VARCHAR(10).nullable(false).default_("nb"))
                .column("background", SQLDataType.VARCHAR(255).nullable(false).default_("textures/block/moss_block.png"))
                .primaryKey("id")
                .constraints(
                        DSL.unique("uuid")
                )
                .execute();

        String oldTableName = "playerData";
        if (!tableExists(context, oldTableName)) {
            return;
        }

        context.insertInto(
                        DSL.table(tableName),
                        DSL.field("id"),
                        DSL.field("uuid"),
                        DSL.field("name"),
                        DSL.field("skin_url"),
                        DSL.field("gender"),
                        DSL.field("background")
                )
                .select(context.select(
                                DSL.field("id"),
                                DSL.field("uuid"),
                                DSL.field("name"),
                                DSL.field("skinUrl"),
                                DSL.field("gender"),
                                DSL.field("background"))
                        .from(DSL.table(oldTableName))
                        .where(DSL.field("uuid").isNotNull()))
                .execute();

        context.dropTableIfExists(oldTableName).execute();
    }
}
