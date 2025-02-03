package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class FamilyTree extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey reloadedMK = new CommandMessageKey(this,"reloaded");
    private final CommandMessageKey failedMK = new CommandMessageKey(this,"failed");
    private final CommandMessageKey disabledMK = new CommandMessageKey(this,"disabled");


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

        if (!LunaticFamily.getConfig().isUseCrazyAdvancementAPI()){
            sender.sendMessage(getMessage(disabledMK));
            return true;
        }

            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            String name = player.getName();
            FamilyPlayerImpl playerFam = getFamilyPlayer(playerUUID);
            playerFam.update();

            if (playerFam.updateFamilyTree()) {
                player.sendMessage(getMessage(reloadedMK));
            } else {
                sender.sendMessage(getMessage(failedMK));
                Logger.errorLog("Failed to reload family tree for player " + name + " (" + playerUUID + ").");
                Logger.errorLog("Is the correct version of CrazyAdvancementsAPI installed?");
                return false;
            }

        return true;
    }
}
