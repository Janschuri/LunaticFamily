package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
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
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (!playerFam.isMarried()) {
                player.sendMessage(getMessage(noPartnerMK));
                return true;
            } else if (!player.hasPermission("lunaticfamily.marry.gift")) {
                player.sendMessage(getMessage(NO_PERMISSION_MK));
                return true;
            }

            UUID partnerUUID = playerFam.getPartner().getUniqueId();
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

            if (!partner.isOnline()) {
                player.sendMessage(getMessage(PLAYER_NAME_MK)
                        .replaceText(getTextReplacementConfig("%player%", partner.getName())));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(partner)) {
                player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK)
                        .replaceText(getTextReplacementConfig("%player%", partner.getName()))
                        .replaceText(getTextReplacementConfig("%server%", partner.getServerName())));
                return true;
            }

            if (!player.hasItemInMainHand()) {
                player.sendMessage(getMessage(emptyHandMK));
                return true;
            }


                byte[] item = player.getItemInMainHand();
                if (partner.giveItemDrop(item)) {
                    player.removeItemInMainHand();
                    player.sendMessage(getMessage(sentMK)
                            .replaceText(getTextReplacementConfig("%player%", partner.getName())));
                    partner.sendMessage(getMessage(gotMK)
                            .replaceText(getTextReplacementConfig("%player%", player.getName())));
                } else {
                    Logger.errorLog("Error while giving item to partner.");
                }
        }
        return true;
    }
}
