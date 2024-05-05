package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class FamilyReloadSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "reload";
    private static final String PERMISSION = "lunaticfamily.admin.reload";

    public FamilyReloadSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            LunaticFamily.loadConfig();
            sender.sendMessage(language.getPrefix() + language.getMessage("admin_reload"));
        }
        return true;
    }
}
