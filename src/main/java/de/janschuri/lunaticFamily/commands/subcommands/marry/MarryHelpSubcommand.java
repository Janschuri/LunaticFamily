package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.subcommands.HelpSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.family.MarrySubcommand;

public class MarryHelpSubcommand extends HelpSubcommand {
    private static final String mainCommand = "marry";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.marry";

    public MarryHelpSubcommand() {
        super(mainCommand, name, permission, MarrySubcommand.class);
    }
}
