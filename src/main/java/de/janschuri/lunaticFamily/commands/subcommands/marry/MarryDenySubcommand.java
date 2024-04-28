package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryDenySubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "deny";
    private static final String permission = "lunaticfamily.marry";

    public MarryDenySubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_deny_no_request"));
            } else {
                if (LunaticFamily.marryRequests.containsKey(playerUUID)) {
                    UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                    AbstractPlayerSender partner = AbstractSender.getPlayerSender(partnerUUID);
                    if (!LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                        partner.sendMessage(language.getPrefix() + language.getMessage("marry_deny_denied").replace("%player%", playerFam.getName()));
                    } else {
                        UUID priestUUID = LunaticFamily.marryPriest.get(partnerUUID);
                        AbstractPlayerSender priest = AbstractSender.getPlayerSender(priestUUID);
                        player.chat(language.getMessage("marry_deny_no"));

                        Runnable runnable = () -> {
                            priest.chat(language.getMessage("marry_deny_cancel"));
                        };

                        Utils.scheduleTask(runnable, 250, TimeUnit.MILLISECONDS);


                        LunaticFamily.marryPriest.remove(partnerUUID);
                    }
                    LunaticFamily.marryRequests.remove(playerUUID);

                } else if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                    player.chat(language.getMessage("marry_deny_no"));
                    UUID priestUUID = LunaticFamily.marryPriest.get(playerUUID);
                    AbstractPlayerSender priest = AbstractSender.getPlayerSender(priestUUID);
                    priest.chat(language.getMessage("marry_deny_cancel"));
                    LunaticFamily.marryPriestRequests.remove(playerUUID);
                    LunaticFamily.marryPriest.remove(playerUUID);
                }
            }
        }
        return true;
    }
}
