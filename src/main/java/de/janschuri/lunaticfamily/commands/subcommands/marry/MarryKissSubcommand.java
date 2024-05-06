package de.janschuri.lunaticfamily.commands.subcommands.marry;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.config.PluginConfig;
import de.janschuri.lunaticfamily.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.LunaticLib;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.Mode;

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
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!playerFam.isMarried()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_kiss_no_partner"));
                return true;
            }

            AbstractPlayerSender partner = AbstractSender.getPlayerSender(playerFam.getPartner().getUniqueId());

            if (!partner.isOnline()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(partner.getUniqueId())) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName().replace("%server%", partner.getServerName())));
                return true;
            }

            if (!player.isSameServer(partner.getUniqueId())) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_same_server").replace("%player%", partner.getName()));
                return true;
            }

            if (!player.isInRange(partner.getUniqueId(), PluginConfig.getMarryKissRange())) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_too_far_away").replace("%player%", partner.getName()));
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