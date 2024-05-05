package de.janschuri.lunaticfamily.commands.subcommands.sibling;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class SiblingDenySubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "deny";
    private static final String PERMISSION = "lunaticfamily.sibling";

    public SiblingDenySubcommand() {
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

            if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("propose_deny_no_request"));
            } else {
                UUID siblingUUID = LunaticFamily.siblingRequests.get(playerUUID);
                AbstractPlayerSender sibling = AbstractSender.getPlayerSender(siblingUUID);
                sibling.sendMessage(language.getPrefix() + language.getMessage("propose_deny_denied").replace("%player%", playerFam.getName()));
                LunaticFamily.siblingRequests.remove(playerUUID);
            }
        }
        return true;
    }
}
