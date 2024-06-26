package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class FamilyTree extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey reloadedMK = new CommandMessageKey(this,"reloaded");


    @Override
    public String getPermission() {
        return "lunaticfamily.family.tree";
    }

    @Override
    public String getName() {
        return "tree";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            String name = player.getName();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID, name);

            if (playerFam.updateFamilyTree()) {
                player.sendMessage(getMessage(reloadedMK));
            } else {
                return false;
            }

        return true;
    }
}
