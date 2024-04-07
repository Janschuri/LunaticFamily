package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.commands.subcommands.HelpSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.family.SiblingSubcommand;

public class SiblingHelpSubcommand extends HelpSubcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingHelpSubcommand() {
        super(mainCommand, name, permission, SiblingSubcommand.class);
    }
}
