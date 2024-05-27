package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryDenySubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "deny";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryDenySubcommand() {
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

            if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_deny_no_request"));
            } else {
                if (LunaticFamily.marryRequests.containsKey(playerUUID)) {
                    UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                    PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
                    if (!LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                        partner.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_deny_denied").replace("%player%", playerFam.getName()));
                    } else {
                        UUID priestUUID = LunaticFamily.marryPriest.get(partnerUUID);
                        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                        player.chat(LunaticFamily.getLanguageConfig().getMessage("marry_deny_no"));

                        Runnable runnable = () -> {
                            priest.chat(LunaticFamily.getLanguageConfig().getMessage("marry_deny_cancel"));
                        };

                        Utils.scheduleTask(runnable, 250, TimeUnit.MILLISECONDS);


                        LunaticFamily.marryPriest.remove(partnerUUID);
                    }
                    LunaticFamily.marryRequests.remove(playerUUID);

                } else if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                    player.chat(LunaticFamily.getLanguageConfig().getMessage("marry_deny_no"));
                    UUID priestUUID = LunaticFamily.marryPriest.get(playerUUID);
                    PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                    priest.chat(LunaticFamily.getLanguageConfig().getMessage("marry_deny_cancel"));
                    LunaticFamily.marryPriestRequests.remove(playerUUID);
                    LunaticFamily.marryPriest.remove(playerUUID);
                }
            }
        }
        return true;
    }
}
