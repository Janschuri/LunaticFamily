package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class AdoptUnsetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "unset";
    private static final String PERMISSION = "lunaticfamily.admin.adopt";
    public AdoptUnsetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            if (args.length < 1) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("AdoptUnsetSubcommand: Wrong usage");
                return true;
            }


            String childArg = args[0];
            UUID childUUID;


            if (Utils.isUUID(childArg)) {
                childUUID = UUID.fromString(childArg);

                if (PlayerDataTable.getID(childUUID) < 0) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", childArg));
                    return true;
                }
            } else {
                childUUID = PlayerDataTable.getUUID(childArg);

                if (childUUID == null) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", childArg));
                    return true;
                }
            }


                FamilyPlayerImpl childFam = new FamilyPlayerImpl(childUUID);

                if (!childFam.isAdopted()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_adopt_unset_not_adopted").replace("%player%", childFam.getName()));
                } else {
                    FamilyPlayerImpl firstParentFam = (FamilyPlayerImpl) childFam.getParents().get(0);

                    if (firstParentFam.isMarried()) {
                        FamilyPlayerImpl secondParentFam = firstParentFam.getPartner();
                        sender.sendMessage(getPrefix() + getMessage("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                    } else {
                        sender.sendMessage(getPrefix() + getMessage("admin_adopt_unset_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                    }
                    firstParentFam.unadopt(childFam.getId());
                }

        }
        return true;
    }
}
