package de.janschuri.lunaticFamily.commands.subcommands.gender;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GenderSetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "gender";
    private static final String NAME = "set";
    private static final String PERMISSION = "lunaticfamily.gender";

    public GenderSetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof AbstractPlayerSender)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
            }
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            List<ClickableMessage> msg = new ArrayList<>();
            msg.add(new ClickableMessage(language.getPrefix() + language.getMessage("gender_set") + "\n"));
            for (String gender : Language.getGenders()) {
                ClickableMessage clickableMessage = new ClickableMessage(language.getPrefix() + " - " + Language.getGenderLang(gender) + "\n");
                clickableMessage.setCommand("/family gender set " + gender);
                clickableMessage.setHoverText(language.getMessage("gender_set_hover")
                        .replace("%gender%", Language.getGenderLang(gender)));
                msg.add(clickableMessage);
            }
            player.sendMessage(msg);
            return true;
        }
        if (args.length == 1) {
            if (!(sender instanceof AbstractPlayerSender)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
            } else {
                AbstractPlayerSender player = (AbstractPlayerSender) sender;
                UUID playerUUID = player.getUniqueId();
                FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

                if (!Language.getGenders().contains(args[0].toLowerCase())) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("gender_not_exist"));
                } else if (playerFam.getGender().equalsIgnoreCase(args[0])) {
                    player.sendMessage(language.getPrefix() + language.getMessage("gender_already")
                            .replace("%gender%", Language.getGenderLang(args[0])));
                } else {
                    playerFam.setGender(args[0].toLowerCase());
                    sender.sendMessage(language.getPrefix() + language.getMessage("gender_changed")
                            .replace("%gender%", Language.getGenderLang(args[0])));
                }
            }
            return true;
        }
        if (!sender.hasPermission("lunaticFamily.admin.gender")) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else if (!Language.getGenders().contains(args[0].toLowerCase())) {
            sender.sendMessage(language.getPrefix() + language.getMessage("gender_not_exist"));
        } else {
            UUID player1UUID;
            AbstractPlayerSender player1;
            if (Utils.isUUID(args[1])) {
                player1UUID = UUID.fromString(args[1]);
                player1 = AbstractSender.getPlayerSender(player1UUID);
            } else {
                player1 = AbstractSender.getPlayerSender(args[1]);
                player1UUID = player1.getUniqueId();
            }

            if (!player1.exists()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
                return true;
            }
            FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
            if (player1Fam.getGender().equalsIgnoreCase(args[0])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_gender_already")
                        .replace("%player%", player1.getName())
                        .replace("%gender%", Language.getGenderLang(args[1])));
                return true;
            }
            player1Fam.setGender(args[0].toLowerCase());
            sender.sendMessage(language.getPrefix() + language.getMessage("admin_gender_changed")
                    .replace("%player%", player1.getName())
                    .replace("%gender%", Language.getGenderLang(args[1])));
        }
        return true;

    }
}
