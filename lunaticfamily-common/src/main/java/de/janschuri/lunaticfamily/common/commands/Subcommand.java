package de.janschuri.lunaticfamily.common.commands;


import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticlib.common.commands.AbstractSubcommand;

import java.util.List;

public abstract class Subcommand extends AbstractSubcommand {

    protected Subcommand(String mainCommand, String name, String permission) {
        super(LunaticFamily.getLanguageConfig(), mainCommand, name, permission);
    }

    protected Subcommand(String mainCommand, String name, String permission, List<String> params) {
        super(LunaticFamily.getLanguageConfig(), mainCommand, name, permission, params);
    }

    protected Subcommand(String mainCommand, String name, String permission, AbstractSubcommand[] subcommands) {
        super(LunaticFamily.getLanguageConfig(), mainCommand, name, permission, subcommands);
    }

    protected static String getPrefix() {
        return LunaticFamily.getLanguageConfig().getPrefix();
    }

    protected static String getMessage(String key) {
        return LunaticFamily.getLanguageConfig().getMessage(key);
    }

    protected static boolean checkIsSubcommand(String mainCommand, String subcommand, String input) {
        return LunaticFamily.getLanguageConfig().checkIsSubcommand(mainCommand, subcommand, input);
    }

    protected static String getRelation(String mainCommand, String subcommand) {
        return LunaticFamily.getLanguageConfig().getRelation(mainCommand, subcommand);
    }

    protected static String getGenderLang(String gender) {
        return LunaticFamily.getLanguageConfig().getGenderLang(gender);
    }
}
