package de.janschuri.lunaticfamily.commands.subcommands.family;


import de.janschuri.lunaticfamily.commands.subcommands.HelpSubcommand;

public class FamilyHelpSubcommand extends HelpSubcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "help";
    private static final String PERMISSION = "lunaticfamily.family";

    public FamilyHelpSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, FamilySubcommand.class);
    }
}
