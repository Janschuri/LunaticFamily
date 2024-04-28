package de.janschuri.lunaticFamily.commands.subcommands.gender;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GenderSetSubcommand extends Subcommand {
    private static final String mainCommand = "gender";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.gender";

    public GenderSetSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
            return true;
        }
        if (args.length == 1) {
            if (!(sender instanceof AbstractPlayerSender)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
            }
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            List<ClickableMessage> msg = new ArrayList<>();
            msg.add(new ClickableMessage(language.getPrefix() + language.getMessage("gender_set") + "\n"));
            for (String gender : language.genders) {
                ClickableMessage clickableMessage = new ClickableMessage(language.getPrefix() + " - " + language.getGenderLang(gender) + "\n");
                clickableMessage.setCommand("/family gender set " + gender);
                clickableMessage.setHoverText(language.getMessage("gender_set_hover").replace("%gender%", language.getGenderLang(gender)));
                msg.add(clickableMessage);

                Logger.debugLog(clickableMessage.getColor());
                Logger.debugLog(clickableMessage.getCommand());
                Logger.debugLog(clickableMessage.getHoverText());
                Logger.debugLog(clickableMessage.getText());
            }
            player.sendMessage(msg);
            return true;
        }
        if (args.length == 2) {
            if (!(sender instanceof AbstractPlayerSender)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
            } else {
                AbstractPlayerSender player = (AbstractPlayerSender) sender;
                UUID playerUUID = player.getUniqueId();
                FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

                if (!language.genders.contains(args[1].toLowerCase())) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("gender_not_exist"));
                } else if (playerFam.getGender().equalsIgnoreCase(args[1])) {
                    player.sendMessage(language.getPrefix() + language.getMessage("gender_already").replace("%gender%", language.getGenderLang(args[1])));
                } else {
                    playerFam.setGender(args[1].toLowerCase());
                    sender.sendMessage(language.getPrefix() + language.getMessage("gender_changed").replace("%gender%", language.getGenderLang(args[1])));
                }
            }
            return true;
        }
        if (!sender.hasPermission("lunaticFamily.admin.gender")) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else if (!language.genders.contains(args[1].toLowerCase())) {
            sender.sendMessage(language.getPrefix() + language.getMessage("gender_not_exist"));
        } else {
            UUID player1UUID;
            AbstractPlayerSender player1;
            if (Utils.isUUID(args[2])) {
                player1UUID = UUID.fromString(args[2]);
                player1 = AbstractSender.getPlayerSender(player1UUID);
            } else {
                player1 = AbstractSender.getPlayerSender(args[2]);
                player1UUID = player1.getUniqueId();
            }

            if (!player1.exists()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
                return true;
            }
            FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
            if (player1Fam.getGender().equalsIgnoreCase(args[1])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_gender_already").replace("%player%", player1.getName()).replace("%gender%", language.getGenderLang(args[2])));
                return true;
            }
            player1Fam.setGender(args[1].toLowerCase());
            sender.sendMessage(language.getPrefix() + language.getMessage("admin_gender_changed").replace("%player%", player1.getName()).replace("%gender%", language.getGenderLang(args[2])));
        }
        return true;

    }
}
