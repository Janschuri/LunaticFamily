package de.janschuri.lunaticFamily.commands.subcommands.gender;

import de.janschuri.lunaticFamily.commands.subcommands.HelpSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.family.GenderSubcommand;

public class GenderHelpSubcommand extends HelpSubcommand {
    private static final String mainCommand = "gender";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.gender";

    public GenderHelpSubcommand() {
        super(mainCommand, name, permission, GenderSubcommand.class);
    }
}
