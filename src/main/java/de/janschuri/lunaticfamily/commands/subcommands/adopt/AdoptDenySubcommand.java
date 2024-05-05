package de.janschuri.lunaticfamily.commands.subcommands.adopt;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class AdoptDenySubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "deny";
    private static final String PERMISSION = "lunaticfamily.adopt";

    public AdoptDenySubcommand() {
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

            if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_deny_no_request"));
            } else {
                UUID parentUUID = LunaticFamily.adoptRequests.get(playerUUID);
                FamilyPlayer parentFam = new FamilyPlayer(parentUUID);
                AbstractPlayerSender parent = AbstractSender.getPlayerSender(parentUUID);
                parent.sendMessage(language.getPrefix() + language.getMessage("adopt_deny").replace("%player%", playerFam.getName()));
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_denied").replace("%player%", parentFam.getName()));
                LunaticFamily.adoptRequests.remove(playerUUID);
            }
        }
        return true;
    }
}
