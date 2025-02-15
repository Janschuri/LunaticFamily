package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryDeny extends FamilyCommand implements HasParentCommand {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey noRequestMK = new LunaticCommandMessageKey(this,"no_request");
    private final CommandMessageKey deniedMK = new LunaticCommandMessageKey(this,"denied");
    private final CommandMessageKey denyMK = new LunaticCommandMessageKey(this,"deny");

    private final CommandMessageKey priestCancelMK = new LunaticCommandMessageKey(this,"cancel");
    private final CommandMessageKey priestNoMK = new LunaticCommandMessageKey(new Marry(),"no");

    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
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


        if (LunaticFamily.marryRequests.containsKey(playerUUID)) {
            UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
            if (!LunaticFamily.marryPriests.containsKey(partnerUUID)) {
                player.sendMessage(getMessage(denyMK,
                placeholder("%player%", partner.getName())));
                partner.sendMessage(getMessage(deniedMK,
                placeholder("%player%", playerFam.getName())));
            } else {
                UUID priestUUID = LunaticFamily.marryPriests.get(partnerUUID);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                player.chat(getLanguageConfig().getMessageAsString(priestNoMK.noPrefix()));

                Runnable runnable = () -> {
                    priest.chat(getLanguageConfig().getMessageAsString(priestCancelMK.noPrefix()));
                };

                Utils.scheduleTask(runnable, 250, TimeUnit.MILLISECONDS);


                LunaticFamily.marryPriests.remove(partnerUUID);
            }
            LunaticFamily.marryRequests.remove(playerUUID);
            return true;
        }

        if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
            player.chat(getLanguageConfig().getMessageAsString(priestNoMK.noPrefix()));
            UUID priestUUID = LunaticFamily.marryPriests.get(playerUUID);
            PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
            priest.chat(getLanguageConfig().getMessageAsString(priestCancelMK.noPrefix()));
            LunaticFamily.marryPriestRequests.remove(playerUUID);
            LunaticFamily.marryPriests.remove(playerUUID);
            return true;
        }

        sender.sendMessage(getMessage(noRequestMK));

        return true;
    }
}
