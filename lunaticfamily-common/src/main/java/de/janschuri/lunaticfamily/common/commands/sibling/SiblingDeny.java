package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestSibling;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SiblingDeny extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");
    private final CommandMessageKey deniedMK = new CommandMessageKey(this,"denied");
    private final CommandMessageKey denyMK = new CommandMessageKey(this,"deny");

    private final CommandMessageKey priestNoMK = new CommandMessageKey(new PriestSibling(),"no");
    private final CommandMessageKey priestCancelMK = new CommandMessageKey(new PriestSibling(),"cancel");


    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public Sibling getParentCommand() {
        return new Sibling();
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
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

        if (LunaticFamily.siblingRequests.containsKey(playerUUID)) {
            UUID partnerUUID = LunaticFamily.siblingRequests.get(playerUUID);
            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
            if (!LunaticFamily.siblingPriests.containsKey(partnerUUID)) {
                player.sendMessage(getMessage(denyMK)
                        .replaceText(getTextReplacementConfig("%player%", sibling.getName())));
                sibling.sendMessage(getMessage(deniedMK)
                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
            } else {
                UUID priestUUID = LunaticFamily.siblingPriests.get(partnerUUID);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                player.chat(getLanguageConfig().getMessageAsString(priestNoMK, false));

                Runnable runnable = () -> {
                    priest.chat(getLanguageConfig().getMessageAsString(priestCancelMK, false));
                };

                Utils.scheduleTask(runnable, 250, TimeUnit.MILLISECONDS);


                LunaticFamily.siblingPriests.remove(partnerUUID);
            }
            LunaticFamily.siblingRequests.remove(playerUUID);
            return true;
        }

        if (LunaticFamily.siblingPriestRequests.containsKey(playerUUID)) {
            player.chat(getLanguageConfig().getMessageAsString(priestNoMK, false));
            UUID priestUUID = LunaticFamily.siblingPriests.get(playerUUID);
            PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
            priest.chat(getLanguageConfig().getMessageAsString(priestCancelMK, false));
            LunaticFamily.siblingPriestRequests.remove(playerUUID);
            LunaticFamily.siblingPriests.remove(playerUUID);
            return true;
        }

        sender.sendMessage(getMessage(noRequestMK));

        return true;
    }
}
