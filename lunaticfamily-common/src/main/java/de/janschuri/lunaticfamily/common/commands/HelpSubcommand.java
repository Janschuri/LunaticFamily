package de.janschuri.lunaticfamily.common.commands;


import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticlib.common.commands.AbstractHelpSubcommand;
import de.janschuri.lunaticlib.common.commands.AbstractSubcommand;

public abstract class HelpSubcommand extends AbstractHelpSubcommand {

    public HelpSubcommand(String mainCommand, String name, String permission, AbstractSubcommand[] subcommands) {
        super(LunaticFamily.getLanguageConfig(), mainCommand, name, permission, subcommands);
    }


}
