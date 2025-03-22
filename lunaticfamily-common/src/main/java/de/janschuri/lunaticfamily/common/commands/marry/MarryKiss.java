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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryKiss extends FamilyCommand implements HasParentCommand {

    private static final MarryKiss INSTANCE = new MarryKiss();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Kiss your partner.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Küss deinen Partner.");
    private static final CommandMessageKey NO_PARTNER_MK = new LunaticCommandMessageKey(INSTANCE, "no_partner")
            .defaultMessage("en", "You are not married!")
            .defaultMessage("de", "Du bist nicht verheiratet!");
    private static final CommandMessageKey KISS_MK = new LunaticCommandMessageKey(INSTANCE, "kiss")
            .defaultMessage("en", "You have kissed %player%.")
            .defaultMessage("de", "Du hast %player% geküsst.");
    private static final CommandMessageKey GOT_KISSED_MK = new LunaticCommandMessageKey(INSTANCE, "got_kissed")
            .defaultMessage("en", "%player% has kissed you.")
            .defaultMessage("de", "%player% hat dich geküsst.");



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
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = FamilyPlayer.find(playerUUID);

        if (!playerFam.isMarried()) {
            sender.sendMessage(getMessage(NO_PARTNER_MK));
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

        player.sendMessage(getMessage(KISS_MK,
                placeholder("%player%", partner.getName())));

        partner.sendMessage(getMessage(GOT_KISSED_MK,
                placeholder("%player%", player.getName())));


        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
