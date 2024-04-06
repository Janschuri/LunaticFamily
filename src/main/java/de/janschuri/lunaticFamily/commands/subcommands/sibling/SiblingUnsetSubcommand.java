package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SiblingUnsetSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "unset";
    private static final String permission = "lunaticfamily.admin.sibling";

    public SiblingUnsetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (!Utils.playerExists(args[1])) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", Utils.getName(args[1])));
            } else {
                String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

                if (!player1Fam.hasSibling()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_unset_no_sibling").replace("%player%", player1Fam.getName()));
                } else {
                    FamilyPlayer siblingFam = player1Fam.getSibling();
                    player1Fam.removeSibling();
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_unset_sibling").replace("%player1%", player1Fam.getName()).replace("%player2%", siblingFam.getName()));
                }
            }
        }
    }
}
