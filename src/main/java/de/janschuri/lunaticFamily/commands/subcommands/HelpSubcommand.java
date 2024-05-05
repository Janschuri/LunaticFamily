package de.janschuri.lunaticFamily.commands.subcommands;

import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticlib.commands.AbstractHelpSubcommand;

public class HelpSubcommand extends AbstractHelpSubcommand {

    public HelpSubcommand(String mainCommand, String name, String permission, Class<?> commandClass) {
        super(Language.getLanguage(), mainCommand, name, permission, commandClass);
    }
}
