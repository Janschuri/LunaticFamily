package de.janschuri.lunaticFamily.commands.gender.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GenderSetSubcommand extends Subcommand {
    private static final String mainCommand = "gender";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.gender";

    public GenderSetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 1) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
                } else {
                    TextComponent msg = new TextComponent(Language.prefix + Language.getMessage("gender_set") + "\n");

                    for (String gender : Language.genders) {
                        TextComponent text = new TextComponent(Language.prefix + " - " + Language.getGenderLang(gender) + "\n");
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gender set " + gender));
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Language.getMessage("gender_set_hover").replace("%gender%", Language.getGenderLang(gender))).create()));
                        msg.addExtra(text);
                    }
                    sender.sendMessage(msg);
                }

            } else if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
                } else {
                    Player player = (Player) sender;
                    String uuid = player.getUniqueId().toString();
                    FamilyPlayer playerFam = new FamilyPlayer(uuid);

                    if (!Language.genders.contains(args[1].toLowerCase())) {
                        sender.sendMessage(Language.prefix + Language.getMessage("gender_not_exist"));
                    } else if (playerFam.getGender().equalsIgnoreCase(args[1])) {
                        player.sendMessage(Language.prefix + Language.getMessage("gender_already").replace("%gender%", Language.getGenderLang(args[1])));
                    } else {
                        playerFam.setGender(args[1].toLowerCase());
                        sender.sendMessage(Language.prefix + Language.getMessage("gender_changed").replace("%gender%", Language.getGenderLang(args[1])));
                    }
                }
            } else {
                if (!sender.hasPermission("lunaticFamily.admin.gender")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else if (!Utils.playerExists(args[1])) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                } else {
                    String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyPlayer player1Fam = new FamilyPlayer(player1);

                    if (!Language.genders.contains(args[2].toLowerCase())) {
                        sender.sendMessage(Language.prefix + Language.getMessage("gender_not_exist"));
                    } else if (player1Fam.getGender().equalsIgnoreCase(args[2])) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_gender_already").replace("%player%", Utils.getName(args[1])).replace("%gender%", Language.getGenderLang(args[2])));
                    } else {
                        player1Fam.setGender(args[2].toLowerCase());
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_gender_changed").replace("%player%", Utils.getName(args[1])).replace("%gender%", Language.getGenderLang(args[2])));
                    }
                }
            }
        }
    }
}
