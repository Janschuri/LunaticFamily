package de.janschuri.lunaticfamily.commands.subcommands.gender;

import de.janschuri.lunaticfamily.commands.subcommands.HelpSubcommand;
import de.janschuri.lunaticfamily.commands.subcommands.family.GenderSubcommand;

public class GenderHelpSubcommand extends HelpSubcommand {
    private static final String MAIN_COMMAND = "gender";
    private static final String NAME = "help";
    private static final String PERMISSION = "lunaticfamily.gender";

    public GenderHelpSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, GenderSubcommand.class);
    }
}
