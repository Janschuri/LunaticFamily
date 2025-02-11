package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryKiss extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noPartnerMK = new CommandMessageKey(this,"no_partner");
    private final CommandMessageKey kissMK = new CommandMessageKey(this,"kiss");
    private final CommandMessageKey gotKissedMK = new CommandMessageKey(this,"got_kissed");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry.kiss";
    }

    @Override
    public String getName() {
        return "kiss";
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
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

        if (!playerFam.isMarried()) {
            sender.sendMessage(getMessage(noPartnerMK));
            return true;
        }

        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUUID());

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

        if (!player.isSameServer(partner.getUniqueId())) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", partner.getName())));
            return true;
        }

        if (!player.isInRange(partner.getUniqueId(), LunaticFamily.getConfig().getMarryKissRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", partner.getName())));
            return true;
        }

        double[] playerPosition = player.getPosition();
        double[] partnerPosition = partner.getPosition();
        double[] position = Utils.getPositionBetweenLocations(playerPosition, partnerPosition);
        position[1] += 2;
        for (int i = 0; i < 6; i++) {

            Runnable runnable = () -> {
                Utils.spawnParticleCloud(playerUUID, position, "HEART");
            };

            Utils.scheduleTask(runnable, i * 250L, TimeUnit.MILLISECONDS);
        }

        player.sendMessage(getMessage(kissMK,
                placeholder("%player%", partner.getName())));

        partner.sendMessage(getMessage(gotKissedMK,
                placeholder("%player%", player.getName())));


        return true;
    }
}
