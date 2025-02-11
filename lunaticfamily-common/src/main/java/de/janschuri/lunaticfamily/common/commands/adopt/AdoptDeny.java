package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestAdopt;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdoptDeny extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey deniedMK = new CommandMessageKey(this,"denied");
    private final CommandMessageKey denyMK = new CommandMessageKey(this,"deny");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");

    private final CommandMessageKey priestNoMK = new CommandMessageKey(new PriestAdopt(),"no");
    private final CommandMessageKey priestCancelMK = new CommandMessageKey(new PriestAdopt(),"cancel");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public Adopt getParentCommand() {
        return new Adopt();
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

        if (LunaticFamily.adoptRequests.containsKey(playerUUID)) {
            UUID partnerUUID = LunaticFamily.adoptRequests.get(playerUUID);
            PlayerSender parent = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
            if (!LunaticFamily.adoptPriests.containsKey(partnerUUID)) {
                player.sendMessage(getMessage(denyMK)
                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
                parent.sendMessage(getMessage(deniedMK)
                        .replaceText(getTextReplacementConfig("%player%", parent.getName())));
            } else {
                UUID priestUUID = LunaticFamily.adoptPriests.get(partnerUUID);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                player.chat(getLanguageConfig().getMessageAsString(priestNoMK, false));

                Runnable runnable = () -> {
                    priest.chat(getLanguageConfig().getMessageAsString(priestCancelMK, false));
                };

                Utils.scheduleTask(runnable, 250, TimeUnit.MILLISECONDS);


                LunaticFamily.adoptPriests.remove(partnerUUID);
            }
            LunaticFamily.adoptRequests.remove(playerUUID);
            return true;
        }

        if (LunaticFamily.adoptPriestRequests.containsKey(playerUUID)) {
            player.chat(getLanguageConfig().getMessageAsString(priestNoMK, false));
            UUID priestUUID = LunaticFamily.adoptPriests.get(playerUUID);
            PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
            priest.chat(getLanguageConfig().getMessageAsString(priestCancelMK, false));
            LunaticFamily.adoptPriestRequests.remove(playerUUID);
            LunaticFamily.adoptPriests.remove(playerUUID);
            return true;
        }

        sender.sendMessage(getMessage(noRequestMK));

        return true;
    }
}
