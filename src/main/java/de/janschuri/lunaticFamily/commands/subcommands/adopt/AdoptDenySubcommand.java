package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class AdoptDenySubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "deny";
    private static final String permission = "lunaticfamily.adopt";

    public AdoptDenySubcommand() {
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

            if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_deny_no_request"));
            } else {
                UUID parentUUID = LunaticFamily.adoptRequests.get(playerUUID);
                FamilyPlayer parentFam = new FamilyPlayer(parentUUID);
                AbstractPlayerSender parent = player.getPlayerCommandSender(parentUUID);
                parent.sendMessage(language.getPrefix() + language.getMessage("adopt_deny").replace("%player%", playerFam.getName()));
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_denied").replace("%player%", parentFam.getName()));
                LunaticFamily.adoptRequests.remove(playerUUID);
            }
        }
        return true;
    }
}
