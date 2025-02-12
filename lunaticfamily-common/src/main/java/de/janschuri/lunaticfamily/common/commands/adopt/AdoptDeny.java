package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestAdopt;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.command.LunaticCommandMessageKey;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdoptDeny extends FamilyCommand implements HasParentCommand {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey deniedMK = new LunaticCommandMessageKey(this,"denied");
    private final CommandMessageKey denyMK = new LunaticCommandMessageKey(this,"deny");
    private final CommandMessageKey noRequestMK = new LunaticCommandMessageKey(this,"no_request");

    private final CommandMessageKey priestNoMK = new LunaticCommandMessageKey(new PriestAdopt(),"no");
    private final CommandMessageKey priestCancelMK = new LunaticCommandMessageKey(new PriestAdopt(),"cancel");


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
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

        if (LunaticFamily.adoptRequests.containsKey(playerUUID)) {
            UUID partnerUUID = LunaticFamily.adoptRequests.get(playerUUID);
            PlayerSender parent = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
            if (!LunaticFamily.adoptPriests.containsKey(partnerUUID)) {
                player.sendMessage(getMessage(denyMK,
                placeholder("%player%", playerFam.getName())));
                parent.sendMessage(getMessage(deniedMK,
                placeholder("%player%", parent.getName())));
            } else {
                UUID priestUUID = LunaticFamily.adoptPriests.get(partnerUUID);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                player.chat(getLanguageConfig().getMessageAsString(priestNoMK.noPrefix()));

                Runnable runnable = () -> {
                    priest.chat(getLanguageConfig().getMessageAsString(priestCancelMK.noPrefix()));
                };

                Utils.scheduleTask(runnable, 250, TimeUnit.MILLISECONDS);


                LunaticFamily.adoptPriests.remove(partnerUUID);
            }
            LunaticFamily.adoptRequests.remove(playerUUID);
            return true;
        }

        if (LunaticFamily.adoptPriestRequests.containsKey(playerUUID)) {
            player.chat(getLanguageConfig().getMessageAsString(priestNoMK.noPrefix()));
            UUID priestUUID = LunaticFamily.adoptPriests.get(playerUUID);
            PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
            priest.chat(getLanguageConfig().getMessageAsString(priestCancelMK.noPrefix()));
            LunaticFamily.adoptPriestRequests.remove(playerUUID);
            LunaticFamily.adoptPriests.remove(playerUUID);
            return true;
        }

        sender.sendMessage(getMessage(noRequestMK));

        return true;
    }
}
