package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class AdoptUnsetSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "unset";
    private static final String permission = "lunaticfamily.admin.adopt";
    public AdoptUnsetSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            UUID childUUID;
            AbstractPlayerSender child;
            if (Utils.isUUID(args[1])) {
                childUUID = UUID.fromString(args[1]);
                child = AbstractSender.getPlayerSender(childUUID);
            } else {
                child = AbstractSender.getPlayerSender(args[1]);
                childUUID = child.getUniqueId();
            }

            if (!child.exists()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else {

                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (!childFam.isAdopted()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_unset_not_adopted").replace("%player%", childFam.getName()));
                } else {
                    FamilyPlayer firstParentFam = childFam.getParents().get(0);

                    if (firstParentFam.isMarried()) {
                        FamilyPlayer secondParentFam = firstParentFam.getPartner();
                        sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                    } else {
                        sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_unset_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                    }
                    firstParentFam.unadopt(childFam.getID());
                }
            }
        }
        return true;
    }
}
