package de.janschuri.lunaticfamily.commands.subcommands.adopt;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class AdoptUnsetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "unset";
    private static final String PERMISSION = "lunaticfamily.admin.adopt";
    public AdoptUnsetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length < 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            UUID childUUID;
            AbstractPlayerSender child;
            if (Utils.isUUID(args[0])) {
                childUUID = UUID.fromString(args[0]);
                child = AbstractSender.getPlayerSender(childUUID);
            } else {
                child = AbstractSender.getPlayerSender(args[0]);
                childUUID = child.getUniqueId();
            }

            if (!Utils.playerExists(child)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
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
