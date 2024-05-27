package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryKissSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "kiss";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryKissSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (!playerFam.isMarried()) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_kiss_no_partner"));
                return true;
            }

            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUniqueId());

            if (!partner.isOnline()) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_offline").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(partner.getUniqueId())) {
                player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName().replace("%server%", partner.getServerName())));
                return true;
            }

            if (!player.isSameServer(partner.getUniqueId())) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_same_server").replace("%player%", partner.getName()));
                return true;
            }

            if (!player.isInRange(partner.getUniqueId(), LunaticFamily.getConfig().getMarryKissRange())) {
                player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_too_far_away").replace("%player%", partner.getName()));
                return true;
            }

            double[] playerPosition = player.getPosition();
            double[] partnerPosition = partner.getPosition();
            double[] position = Utils.getPositionBetweenLocations(playerPosition, partnerPosition);
            position[1] += 2;
            for (int i = 0; i < 6; i++) {

                Runnable runnable = () -> {
                    if (LunaticLib.getMode() == Mode.PROXY) {
                        new SpawnParticlesCloudRequest().get(playerUUID, position, "HEART");
                    } else {
                        Utils.spawnParticleCloud(playerUUID, position, "HEART");
                    }
                };

                Utils.scheduleTask(runnable, i * 250L, TimeUnit.MILLISECONDS);
            }

        }
        return true;
    }
}
