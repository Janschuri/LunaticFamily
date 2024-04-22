package de.janschuri.lunaticFamily.commands.subcommands.gender;

import de.janschuri.lunaticFamily.senders.CommandSender;
import de.janschuri.lunaticFamily.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;

import java.util.UUID;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;

public class GenderInfoSubcommand extends Subcommand {
    private static final String mainCommand = "gender";
    private static final String name = "info";
    private static final String permission = "lunaticfamily.gender";

    public GenderInfoSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            return true;
        }
        if (args.length == 1) {
            if (!(sender instanceof PlayerCommandSender)) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
                return true;
            }
            PlayerCommandSender player = (PlayerCommandSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
            sender.sendMessage(Language.prefix + Language.getMessage("gender_info").replace("%gender%", Language.getGenderLang(playerFam.getGender())));
            return true;
        }
        PlayerCommandSender player1 = (PlayerCommandSender) sender;
        if (!player1.exists()) {
            sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
            return true;
        }
        sender.sendMessage(Language.prefix + Language.getMessage("gender_info_others").replace("%player%", player1.getName()).replace("%gender%", Language.getGenderLang(args[1])));
        return true;
    }
}
