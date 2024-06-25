package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryDeny extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");
    private final CommandMessageKey deniedMK = new CommandMessageKey(this,"denied");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this,"cancel");
    private final CommandMessageKey marryNoMK = new CommandMessageKey(new Marry(),"no");

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
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);


        if (LunaticFamily.marryRequests.containsKey(playerUUID)) {
            UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
            if (!LunaticFamily.marryPriests.containsKey(partnerUUID)) {
                partner.sendMessage(getMessage(deniedMK)
                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
            } else {
                UUID priestUUID = LunaticFamily.marryPriests.get(partnerUUID);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                player.chat(getLanguageConfig().getMessageAsString(marryNoMK, false));

                Runnable runnable = () -> {
                    priest.chat(getLanguageConfig().getMessageAsString(cancelMK, false));
                };

                Utils.scheduleTask(runnable, 250, TimeUnit.MILLISECONDS);


                LunaticFamily.marryPriests.remove(partnerUUID);
            }
            LunaticFamily.marryRequests.remove(playerUUID);
            return true;
        }

        if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
            player.chat(getLanguageConfig().getMessageAsString(marryNoMK, false));
            UUID priestUUID = LunaticFamily.marryPriests.get(playerUUID);
            PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
            priest.chat(getLanguageConfig().getMessageAsString(cancelMK, false));
            LunaticFamily.marryPriestRequests.remove(playerUUID);
            LunaticFamily.marryPriests.remove(playerUUID);
            return true;
        }

        sender.sendMessage(getMessage(noRequestMK));

        return true;
    }
}
