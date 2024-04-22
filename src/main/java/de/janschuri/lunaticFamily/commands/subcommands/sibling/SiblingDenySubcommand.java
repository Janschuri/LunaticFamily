package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.senders.CommandSender;
import de.janschuri.lunaticFamily.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;

import java.util.UUID;

public class SiblingDenySubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "deny";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingDenySubcommand() {
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
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.siblingRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(Language.prefix + Language.getMessage("propose_deny_no_request"));
            } else {
                if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {

                }
                UUID siblingUUID = LunaticFamily.marryRequests.get(playerUUID);
                PlayerCommandSender sibling = player.getPlayerCommandSender(siblingUUID);
                sibling.sendMessage(Language.prefix + Language.getMessage("propose_deny_denied").replace("%player%", playerFam.getName()));
                LunaticFamily.marryRequests.remove(playerUUID);
            }
        }
        return true;
    }
}
