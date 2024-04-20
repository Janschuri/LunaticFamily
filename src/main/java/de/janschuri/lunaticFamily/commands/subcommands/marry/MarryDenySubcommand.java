package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.CommandSender;
import de.janschuri.lunaticFamily.commands.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;

import java.util.UUID;

public class MarryDenySubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "deny";
    private static final String permission = "lunaticfamily.marry";

    public MarryDenySubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_deny_no_request"));
            } else {
                if (LunaticFamily.marryRequests.containsKey(playerUUID)) {
                    UUID partnerUUID = UUID.fromString(LunaticFamily.marryRequests.get(playerUUID));
                    PlayerCommandSender partner = sender.getPlayerCommandSender(partnerUUID);
                    if (!LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                        partner.sendMessage(Language.prefix + Language.getMessage("marry_deny_denied").replace("%player%", playerFam.getName()));
                    } else {
                        UUID priestUUID = UUID.fromString(LunaticFamily.marryPriest.get(partnerUUID));
                        PlayerCommandSender priest = sender.getPlayerCommandSender(priestUUID);
                        player.chat(Language.getMessage("marry_deny_no"));
                        priest.chat(Language.getMessage("marry_deny_cancel"), 20);
                        LunaticFamily.marryPriest.remove(partnerUUID.toString());
                    }
                    LunaticFamily.marryRequests.remove(playerUUID);

                } else if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                    player.chat(Language.getMessage("marry_deny_no"));
                    UUID priestUUID = UUID.fromString(LunaticFamily.marryPriest.get(playerUUID));
                    PlayerCommandSender priest = sender.getPlayerCommandSender(priestUUID);
                    priest.chat(Language.getMessage("marry_deny_cancel"));
                    LunaticFamily.marryPriestRequests.remove(playerUUID);
                    LunaticFamily.marryPriest.remove(playerUUID);
                }
            }
        }
        return true;
    }
}
