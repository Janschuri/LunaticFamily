package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticlib.Sender;

public class FamilyReloadSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "reload";
    private static final String PERMISSION = "lunaticfamily.admin.reload";

    public FamilyReloadSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            LunaticFamily.loadConfig();
            sender.sendMessage(getPrefix() + getMessage("admin_reload"));
        }
        return true;
    }
}
