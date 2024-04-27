package de.janschuri.lunaticFamily.commands.subcommands;

import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticlib.commands.AbstractSubcommand;

import java.util.List;

public abstract class Subcommand extends AbstractSubcommand {

    protected static final Language language = Language.getInstance();

    protected Subcommand(String mainCommand, String name, String permission) {
        super(language, mainCommand, name, permission);
    }

    protected Subcommand(String mainCommand, String name, String permission, List<String> params) {
        super(language, mainCommand, name, permission, params);
    }

    protected Subcommand(String mainCommand, String name, String permission, AbstractSubcommand[] subcommands) {
        super(language, mainCommand, name, permission, subcommands);
    }
}
