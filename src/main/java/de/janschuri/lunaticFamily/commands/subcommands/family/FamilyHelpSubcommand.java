package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.HelpSubcommand;

public class FamilyHelpSubcommand extends HelpSubcommand {
    private static final String mainCommand = "family";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.family";

    public FamilyHelpSubcommand() {
        super(mainCommand, name, permission, FamilySubcommand.class);
    }
}
