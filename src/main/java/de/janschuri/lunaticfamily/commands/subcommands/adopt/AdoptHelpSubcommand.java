package de.janschuri.lunaticfamily.commands.subcommands.adopt;

import de.janschuri.lunaticfamily.commands.subcommands.HelpSubcommand;
import de.janschuri.lunaticfamily.commands.subcommands.family.AdoptSubcommand;

public class AdoptHelpSubcommand extends HelpSubcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "help";
    private static final String PERMISSION = "lunaticfamily.adopt";

    public AdoptHelpSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, AdoptSubcommand.class);
    }
}
