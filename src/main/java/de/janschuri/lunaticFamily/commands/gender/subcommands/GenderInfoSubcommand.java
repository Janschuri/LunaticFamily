package de.janschuri.lunaticFamily.commands.gender.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GenderInfoSubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.gender";
    private static final List<String> aliases = Language.getAliases("gender", "info");

    public GenderInfoSubcommand() {
        super(permission, aliases);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 1) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
                } else {
                    Player player = (Player) sender;
                    String playerUUID = player.getUniqueId().toString();
                    FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
                    sender.sendMessage(Language.prefix + Language.getMessage("gender_info").replace("%gender%", Language.getGenderLang(playerFam.getGender())));
                }
            } else {
                if (!sender.hasPermission("lunaticFamily.admin.gender") && !sender.hasPermission("lunaticFamily.gender.info.others")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else if (!Utils.playerExists(args[1])) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("gender_info_others").replace("%player%", Utils.getName(args[1])).replace("%gender%", Language.getGenderLang(args[1])));
                }
            }
        }
    }
}
