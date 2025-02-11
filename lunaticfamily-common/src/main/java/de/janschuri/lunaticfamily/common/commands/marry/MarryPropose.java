package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryPropose extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey alreadyMarriedMK = new CommandMessageKey(this,"already_married");
    private final CommandMessageKey playerAlreadyMarriedMK = new CommandMessageKey(this,"player_already_married");
    private final CommandMessageKey requestMK = new CommandMessageKey(this,"request");
    private final CommandMessageKey requestSentMK = new CommandMessageKey(this,"request_sent");
    private final CommandMessageKey requestExpiredMK = new CommandMessageKey(this,"request_expired");
    private final CommandMessageKey requestSentExpiredMK = new CommandMessageKey(this,"request_sent_expired");
    private final CommandMessageKey openRequestMK = new CommandMessageKey(this,"open_request");
    private final CommandMessageKey familyRequestMK = new CommandMessageKey(this,"family_request");
    private final CommandMessageKey tooManyChildrenMK = new CommandMessageKey(this,"too_many_children");
    private final CommandMessageKey selfRequestMK = new CommandMessageKey(this,"self_request");
    private final CommandMessageKey marryYesMK = new CommandMessageKey(new Marry(),"yes");
    private final CommandMessageKey marryNoMK = new CommandMessageKey(new Marry(),"no");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "propose";
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

        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("MarryProposeSubcommand: Wrong usage");
            return true;
        }

        if (playerFam.getName().equalsIgnoreCase(args[0])) {
            sender.sendMessage(getMessage(selfRequestMK));
            return true;
        }

        if (playerFam.isMarried()) {
            sender.sendMessage(getMessage(alreadyMarriedMK,
                placeholder("%player%", playerFam.getName())));
            return true;
        }

        String partnerName = args[0];

        FamilyPlayer partnerFam = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", partnerName).findOneOrEmpty().orElse(null);

        if (partnerFam == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", partnerName)));
            return true;
        }

        UUID partnerUUID = partnerFam.getUUID();
        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

        if (!partner.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                placeholder("%player%", partner.getName())));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(partner)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                placeholder("%player%", partner.getName()),
                placeholder("%server%", partner.getServerName())));
            return true;
        }

        if (!player.isSameServer(partnerUUID) && LunaticFamily.getConfig().getMarryProposeRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", partner.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSING_PLAYER)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        partnerFam.update();

        if (playerFam.isFamilyMember(partnerFam)) {
            sender.sendMessage(getMessage(familyRequestMK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }

        if (partnerFam.isFamilyMember(playerFam)) {
            sender.sendMessage(getMessage(familyRequestMK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }

        if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
            int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
            sender.sendMessage(getMessage(tooManyChildrenMK,
                placeholder("%player%", partnerFam.getName()),
                placeholder("%amount%", Integer.toString(amountDiff))));
        }

        if (LunaticFamily.marryRequests.containsKey(partner.getUniqueId()) || LunaticFamily.marryPriests.containsKey(partner.getUniqueId())) {
            sender.sendMessage(getMessage(openRequestMK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }

        if (partnerFam.isMarried()) {
            sender.sendMessage(getMessage(playerAlreadyMarriedMK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }

        if (!player.isInRange(partner.getUniqueId(), LunaticFamily.getConfig().getMarryProposeRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", partner.getName())));
            return true;
        }

        partner.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                getMessage(requestMK.noPrefix(),
                placeholder("%player1%", partnerFam.getName()),
                placeholder("%player2%", playerFam.getName())),
                getMessage(marryYesMK.noPrefix()),
                "/family marry accept",
                getMessage(marryNoMK.noPrefix()),
                "/family marry deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );


        LunaticFamily.marryRequests.put(partnerUUID, playerUUID);

        sender.sendMessage(getMessage(requestSentMK,
                placeholder("%player%", partnerFam.getName())
        ));

        Runnable runnable = () -> {
            if (LunaticFamily.marryRequests.containsKey(partnerUUID)) {
                LunaticFamily.marryRequests.remove(partnerUUID);
                player.sendMessage(getMessage(requestSentExpiredMK,
                placeholder("%player%", partner.getName())));
                partner.sendMessage(getMessage(requestExpiredMK,
                placeholder("%player%", player.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);


        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK.noPrefix())
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
