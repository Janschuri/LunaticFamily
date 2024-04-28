package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class SiblingDenySubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "deny";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingDenySubcommand() {
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

            if (!LunaticFamily.siblingRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("propose_deny_no_request"));
            } else {
                if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {

                }
                UUID siblingUUID = LunaticFamily.marryRequests.get(playerUUID);
                AbstractPlayerSender sibling = AbstractSender.getPlayerSender(siblingUUID);
                sibling.sendMessage(language.getPrefix() + language.getMessage("propose_deny_denied").replace("%player%", playerFam.getName()));
                LunaticFamily.marryRequests.remove(playerUUID);
            }
        }
        return true;
    }
}
