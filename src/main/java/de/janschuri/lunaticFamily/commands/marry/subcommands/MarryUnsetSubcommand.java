package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class MarryUnsetSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "unset";
    private static final String permission = "lunaticfamily.admin.marry";

    public MarryUnsetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (!Utils.playerExists(args[1])) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else {
                String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

                if (!player1Fam.isMarried()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_unset_no_partner").replace("%player%", player1Fam.getName()));
                } else {
                    FamilyPlayer partnerFam = player1Fam.getPartner();
                    player1Fam.divorce();
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_unset_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                }
            }
        }
    }
}
