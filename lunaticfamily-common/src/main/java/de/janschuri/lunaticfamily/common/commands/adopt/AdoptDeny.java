package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.FamilyAdopt;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class AdoptDeny extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey denyMK = new CommandMessageKey(this,"deny");
    private final CommandMessageKey deniedMK = new CommandMessageKey(this,"denied");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public FamilyAdopt getParentCommand() {
        return new FamilyAdopt();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
        } else if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                sender.sendMessage(getMessage(noRequestMK));
            } else {
                UUID parentUUID = LunaticFamily.adoptRequests.get(playerUUID);
                FamilyPlayerImpl parentFam = new FamilyPlayerImpl(parentUUID);
                PlayerSender parent = LunaticLib.getPlatform().getPlayerSender(parentUUID);
                parent.sendMessage(getMessage(denyMK).replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
                sender.sendMessage(getMessage(deniedMK).replaceText(getTextReplacementConfig("%player%", parentFam.getName())));
                LunaticFamily.adoptRequests.remove(playerUUID);
            }
        }
        return true;
    }
}
