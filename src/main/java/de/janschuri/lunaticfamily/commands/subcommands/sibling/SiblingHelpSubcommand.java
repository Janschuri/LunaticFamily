package de.janschuri.lunaticfamily.commands.subcommands.sibling;

import de.janschuri.lunaticfamily.commands.subcommands.HelpSubcommand;
import de.janschuri.lunaticfamily.commands.subcommands.family.SiblingSubcommand;

public class SiblingHelpSubcommand extends HelpSubcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "help";
    private static final String PERMISSION = "lunaticfamily.sibling";

    public SiblingHelpSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, SiblingSubcommand.class);
    }
}
