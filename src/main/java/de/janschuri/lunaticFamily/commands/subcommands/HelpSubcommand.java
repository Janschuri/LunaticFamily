package de.janschuri.lunaticFamily.commands.subcommands;

import de.janschuri.lunaticlib.commands.AbstractHelpSubcommand;
import de.janschuri.lunaticlib.config.Language;

public class HelpSubcommand extends AbstractHelpSubcommand {

    protected static final Language language = Language.getInstance();

    public HelpSubcommand(String mainCommand, String name, String permission, Class<?> commandClass) {
        super(language, mainCommand, name, permission, commandClass);
    }
}
