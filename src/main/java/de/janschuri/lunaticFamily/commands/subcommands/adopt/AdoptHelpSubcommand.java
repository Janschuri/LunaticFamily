package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.commands.subcommands.HelpSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.family.AdoptSubcommand;

public class AdoptHelpSubcommand extends HelpSubcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.adopt";

    public AdoptHelpSubcommand() {
        super(mainCommand, name, permission, AdoptSubcommand.class);
    }
}
