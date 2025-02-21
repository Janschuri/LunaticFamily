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

import java.util.Map;
import java.util.UUID;

public class MarryGift extends FamilyCommand implements HasParentCommand {

    private static final MarryGift INSTANCE = new MarryGift();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Gift the item in your hand to your partner.");
    private static final CommandMessageKey NO_PARTNER_MK = new LunaticCommandMessageKey(INSTANCE, "no_partner")
            .defaultMessage("en", "You are not married!");
    private static final CommandMessageKey EMPTY_HAND_MK = new LunaticCommandMessageKey(INSTANCE, "empty_hand")
            .defaultMessage("en", "You must be holding an item in your hand.");
    private static final CommandMessageKey PARTNER_FULL_INV_MK = new LunaticCommandMessageKey(INSTANCE, "partner_full_inv")
            .defaultMessage("en", "Your partner has no space in their inventory.");
    private static final CommandMessageKey SENT_MK = new LunaticCommandMessageKey(INSTANCE, "sent")
            .defaultMessage("en", "You have gifted something to your partner.");
    private static final CommandMessageKey GOT_MK = new LunaticCommandMessageKey(INSTANCE, "got")
            .defaultMessage("en", "Your partner has gifted you something.");



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
                player.sendMessage(getMessage(NO_PARTNER_MK));
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
                player.sendMessage(getMessage(EMPTY_HAND_MK));
                return true;
            }


                byte[] item = player.getItemInMainHand();
                if (partner.giveItemDrop(item)) {
                    player.removeItemInMainHand();
                    player.sendMessage(getMessage(SENT_MK,
                            placeholder("%player%", partner.getName())
                    ));
                    partner.sendMessage(getMessage(GOT_MK,
                            placeholder("%player%", player.getName())
                            ));
                } else {
                    Logger.errorLog("Error while giving item to partner.");
                    Logger.errorLog("Item: " + item);
                    return false;
                }
        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
