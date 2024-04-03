package de.janschuri.lunaticFamily.commands.adopt.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AdoptUnsetSubcommand extends Subcommand {

    private static final String permission = "lunaticfamily.admin.adopt";
    private static final List<String> aliases = Language.getAliases("adopt", "unset");
    public AdoptUnsetSubcommand() {
        super(permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (!sender.hasPermission("lunaticFamily.admin.adopt.unset")) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (!Utils.playerExists(args[1])) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", Utils.getName(args[1])));
            } else {

                String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
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
    }
}
