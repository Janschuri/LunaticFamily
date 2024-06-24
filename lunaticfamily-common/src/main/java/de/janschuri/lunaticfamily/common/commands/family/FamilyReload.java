package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;

public class FamilyReload extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey reloadedMK = new CommandMessageKey(this,"reloaded");

    @Override
    public String getPermission() {
        return "lunaticfamily.admin.reload";
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            LunaticFamily.loadConfig();
            sender.sendMessage(getMessage(reloadedMK));
        }
        return true;
    }
}
