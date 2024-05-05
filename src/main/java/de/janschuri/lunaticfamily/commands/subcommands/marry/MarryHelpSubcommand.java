package de.janschuri.lunaticfamily.commands.subcommands.marry;

import de.janschuri.lunaticfamily.commands.subcommands.HelpSubcommand;
import de.janschuri.lunaticfamily.commands.subcommands.family.MarrySubcommand;

public class MarryHelpSubcommand extends HelpSubcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "help";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryHelpSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, MarrySubcommand.class);
    }
}
