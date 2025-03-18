#!/bin/bash

# Check if a description is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <description>"
    exit 1
fi

DESCRIPTION=$(echo "$1" | tr -cd '[:alnum:] ' | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) substr($i,2)}1' | tr -d ' ')
DESCRIPTION=$(echo "$DESCRIPTION" | tr -d ' ') # Remove spaces for PascalCase

# Generate timestamp
TIMESTAMP=$(date +%Y_%m_%d_%H%M%S)


# Define class name and filename
CLASS_NAME="Migration_${TIMESTAMP}_${DESCRIPTION}"
FILENAME="${CLASS_NAME}.java"


# Create directory if not exists
mkdir -p "$(dirname "$0")/lunaticfamily-common/src/main/java/de/janschuri/lunaticfamily/common/database/migrations/"

# Generate migration file
cat <<EOF > "$(dirname "$0")/lunaticfamily-common/src/main/java/de/janschuri/lunaticfamily/common/database/migrations/$FILENAME"
package de.janschuri.lunaticfamily.common.database.migrations;

import org.jooq.DSLContext;
import de.janschuri.lunaticfamily.common.database.Migration;

public class ${CLASS_NAME} extends Migration {


    public void run(DSLContext context) {
        // Add your migration code here
    }
}
EOF

echo "Migration created: $FILENAME"
