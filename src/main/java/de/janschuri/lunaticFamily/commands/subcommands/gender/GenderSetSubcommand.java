package de.janschuri.lunaticFamily.commands.subcommands.gender;

import de.janschuri.lunaticFamily.commands.ClickableMessage;
import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//import net.md_5.bungee.api.chat.ClickEvent;
//import net.md_5.bungee.api.chat.ComponentBuilder;
//import net.md_5.bungee.api.chat.HoverEvent;
//import net.md_5.bungee.api.chat.TextComponent;
//import org.bukkit.Bukkit;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;

public class GenderSetSubcommand extends Subcommand {
    private static final String mainCommand = "gender";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.gender";

    public GenderSetSubcommand() {
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
            }
            PlayerCommandSender player = (PlayerCommandSender) sender;
            List<ClickableMessage> msg = new ArrayList<>();
            msg.add(new ClickableMessage(Language.prefix + Language.getMessage("gender_set") + "\n"));
            for (String gender : Language.genders) {
                msg.add(new ClickableMessage(Language.prefix + " - " + Language.getGenderLang(gender) + "\n"));
                msg.add(new ClickableMessage("/lunaticfamily:gender set " + gender));
                msg.add(new ClickableMessage(Language.getMessage("gender_set_hover").replace("%gender%", Language.getGenderLang(gender))));
            }
            player.sendMessage(msg);
            return true;
        }
        if (args.length == 2) {
            if (!(sender instanceof PlayerCommandSender)) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
            } else {
                PlayerCommandSender player = (PlayerCommandSender) sender;
                UUID playerUUID = player.getUniqueId();
                FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

                if (!Language.genders.contains(args[1].toLowerCase())) {
                    sender.sendMessage(Language.prefix + Language.getMessage("gender_not_exist"));
                } else if (playerFam.getGender().equalsIgnoreCase(args[1])) {
                    player.sendMessage(Language.prefix + Language.getMessage("gender_already").replace("%gender%", Language.getGenderLang(args[1])));
                } else {
                    playerFam.setGender(args[1].toLowerCase());
                    sender.sendMessage(Language.prefix + Language.getMessage("gender_changed").replace("%gender%", Language.getGenderLang(args[1])));
                }
            }
            return true;
        }
        if (!sender.hasPermission("lunaticFamily.admin.gender")) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else if (!Language.genders.contains(args[1].toLowerCase())) {
            sender.sendMessage(Language.prefix + Language.getMessage("gender_not_exist"));
        } else {
            UUID player1UUID;
            PlayerCommandSender player1;
            if (Utils.isUUID(args[2])) {
                player1UUID = UUID.fromString(args[2]);
                player1 = sender.getPlayerCommandSender(player1UUID);
            } else {
                player1 = sender.getPlayerCommandSender(args[2]);
                player1UUID = player1.getUniqueId();
            }

            if (!player1.exists()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                return true;
            }
            FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
            if (player1Fam.getGender().equalsIgnoreCase(args[1])) {
                sender.sendMessage(Language.prefix + Language.getMessage("admin_gender_already").replace("%player%", player1.getName()).replace("%gender%", Language.getGenderLang(args[2])));
                return true;
            }
            player1Fam.setGender(args[1].toLowerCase());
            sender.sendMessage(Language.prefix + Language.getMessage("admin_gender_changed").replace("%player%", player1.getName()).replace("%gender%", Language.getGenderLang(args[2])));
        }
        return true;

    }
}
