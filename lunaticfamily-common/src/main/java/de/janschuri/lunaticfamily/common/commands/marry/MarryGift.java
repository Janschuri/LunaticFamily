package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.UUID;

public class MarryGift extends FamilyCommand implements HasParentCommand {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey noPartnerMK = new LunaticCommandMessageKey(this,"no_partner");
    private final CommandMessageKey emptyHandMK = new LunaticCommandMessageKey(this,"empty_hand");
    private final CommandMessageKey partnerFullInvMK = new LunaticCommandMessageKey(this,"partner_full_inv");
    private final CommandMessageKey sentMK = new LunaticCommandMessageKey(this,"sent");
    private final CommandMessageKey gotMK = new LunaticCommandMessageKey(this,"got");


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
