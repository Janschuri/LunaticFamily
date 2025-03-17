package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;
import org.jooq.impl.SQLDataType;

public class Migration_2025_02_10_224924_CreateTables extends Migration {


    public void run(DSLContext context) {
        context.createTableIfNotExists("playerData")
                .column("id", SQLDataType.INTEGER.nullable(false).identity(true))
                .primaryKey("id")
                .execute();

        context.createTableIfNotExists("adoptions")
                .column("id", SQLDataType.INTEGER.nullable(false).identity(true))
                .primaryKey("id")
                .execute();

        context.createTableIfNotExists("marriages")
                .column("id", SQLDataType.INTEGER.nullable(false).identity(true))
                .primaryKey("id")
                .execute();

        context.createTableIfNotExists("siblinghoods")
                .column("id", SQLDataType.INTEGER.nullable(false).identity(true))
                .primaryKey("id")
                .execute();
    }
}
