package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.commands.CommandSender;
import de.janschuri.lunaticFamily.commands.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.UUID;
//import org.bukkit.Bukkit;
//import org.bukkit.command.CommandSender;

public class AdoptUnsetSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "unset";
    private static final String permission = "lunaticfamily.admin.adopt";
    public AdoptUnsetSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                return true;
            }

            UUID childUUID;
            PlayerCommandSender child;
            if (Utils.isUUID(args[1])) {
                childUUID = UUID.fromString(args[1]);
                child = sender.getPlayerCommandSender(childUUID);
            } else {
                child = sender.getPlayerCommandSender(args[1]);
                childUUID = child.getUniqueId();
            }

            if (!child.exists()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else {

                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (!childFam.isAdopted()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_unset_not_adopted").replace("%player%", childFam.getName()));
                } else {
                    FamilyPlayer firstParentFam = childFam.getParents().get(0);

                    if (firstParentFam.isMarried()) {
                        FamilyPlayer secondParentFam = firstParentFam.getPartner();
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_unset_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                    }
                    firstParentFam.unadopt(childFam.getID());
                }
            }
        }
        return true;
    }
}
