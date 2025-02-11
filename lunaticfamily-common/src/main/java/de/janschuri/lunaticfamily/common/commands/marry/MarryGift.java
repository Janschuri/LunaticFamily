package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Placeholder;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class MarryGift extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noPartnerMK = new CommandMessageKey(this,"no_partner");
    private final CommandMessageKey emptyHandMK = new CommandMessageKey(this,"empty_hand");
    private final CommandMessageKey partnerFullInvMK = new CommandMessageKey(this,"partner_full_inv");
    private final CommandMessageKey sentMK = new CommandMessageKey(this,"sent");
    private final CommandMessageKey gotMK = new CommandMessageKey(this,"got");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry.gift";
    }

    @Override
    public String getName() {
        return "gift";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

            if (!playerFam.isMarried()) {
                player.sendMessage(getMessage(noPartnerMK));
                return true;
            }

            UUID partnerUUID = playerFam.getPartner().getUUID();
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

            if (!partner.isOnline()) {
                player.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                        placeholder("%player%", partner.getName())
                ));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(partner)) {
                player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                    placeholder("%player%", partner.getName())
                ));
                return true;
            }

            if (!player.hasItemInMainHand()) {
                player.sendMessage(getMessage(emptyHandMK));
                return true;
            }


                byte[] item = player.getItemInMainHand();
                if (partner.giveItemDrop(item)) {
                    player.removeItemInMainHand();
                    player.sendMessage(getMessage(sentMK,
                            placeholder("%player%", partner.getName())
                    ));
                    partner.sendMessage(getMessage(gotMK,
                            placeholder("%player%", player.getName())
                            ));
                } else {
                    Logger.errorLog("Error while giving item to partner.");
                    Logger.errorLog("Item: " + item);
                    return false;
                }
        return true;
    }
}
