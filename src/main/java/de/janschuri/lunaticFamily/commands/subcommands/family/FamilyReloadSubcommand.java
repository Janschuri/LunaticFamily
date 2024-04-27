package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class FamilyReloadSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "reload";
    private static final String permission = "lunaticfamily.admin.reload";

    public FamilyReloadSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            LunaticFamily.getInstance().loadConfig();
            sender.sendMessage(language.getPrefix() + language.getMessage("admin_reload"));
        }
        return true;
    }
}
