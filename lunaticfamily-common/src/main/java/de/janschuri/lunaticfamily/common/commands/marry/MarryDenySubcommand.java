package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryDenySubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");
    private final CommandMessageKey deniedMK = new CommandMessageKey(this,"denied");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this,"cancel");
    private final CommandMessageKey marryNoMK = new CommandMessageKey(new MarrySubcommand(),"no");

    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public MarrySubcommand getParentCommand() {
        return new MarrySubcommand();
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

            if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(getMessage(noRequestMK));
            } else {
                if (LunaticFamily.marryRequests.containsKey(playerUUID)) {
                    UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                    PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
                    if (!LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                        partner.sendMessage(getMessage(deniedMK)
                                .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
                    } else {
                        UUID priestUUID = LunaticFamily.marryPriest.get(partnerUUID);
                        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                        player.chat(getMessage(marryNoMK).toString());

                        Runnable runnable = () -> {
                            priest.chat(getMessage(cancelMK).toString());
                        };

                        Utils.scheduleTask(runnable, 250, TimeUnit.MILLISECONDS);


                        LunaticFamily.marryPriest.remove(partnerUUID);
                    }
                    LunaticFamily.marryRequests.remove(playerUUID);

                } else if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                    player.chat(getMessage(marryNoMK).toString());
                    UUID priestUUID = LunaticFamily.marryPriest.get(playerUUID);
                    PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                    priest.chat(getMessage(cancelMK).toString());
                    LunaticFamily.marryPriestRequests.remove(playerUUID);
                    LunaticFamily.marryPriest.remove(playerUUID);
                }
            }
        }
        return true;
    }
}
