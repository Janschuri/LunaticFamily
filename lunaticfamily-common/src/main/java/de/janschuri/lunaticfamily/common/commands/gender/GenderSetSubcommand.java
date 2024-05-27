package de.janschuri.lunaticfamily.common.commands.gender;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.UUID;

public class GenderSetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "gender";
    private static final String NAME = "set";
    private static final String PERMISSION = "lunaticfamily.gender";

    public GenderSetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof PlayerSender)) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_console_command"));
            }
            PlayerSender player = (PlayerSender) sender;

            Component msg = Component.text(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("gender_set") + "\n");

            for (String gender : LunaticFamily.getLanguageConfig().getGenders()) {
                msg.append(
                        Component.text(" - " + LunaticFamily.getLanguageConfig().getGenderLang(gender) + "\n")
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/family gender set " + gender))
                                .hoverEvent(HoverEvent.showText(Component.text(LunaticFamily.getLanguageConfig().getMessage("gender_set_hover").replace("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(gender)))))
                );
            }
            player.sendMessage(msg);
            return true;
        }
        if (args.length == 1) {
            if (!(sender instanceof PlayerSender)) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_console_command"));
            } else {
                PlayerSender player = (PlayerSender) sender;
                UUID playerUUID = player.getUniqueId();
                FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

                if (!LunaticFamily.getLanguageConfig().getGenders().contains(args[0].toLowerCase())) {
                    sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("gender_not_exist"));
                } else if (playerFam.getGender().equalsIgnoreCase(args[0])) {
                    player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("gender_already")
                            .replace("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0])));
                } else {
                    playerFam.setGender(args[0].toLowerCase());
                    sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("gender_changed")
                            .replace("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0])));
                }
            }
            return true;
        }
        if (!sender.hasPermission("lunaticFamily.admin.gender")) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
        } else if (!LunaticFamily.getLanguageConfig().getGenders().contains(args[0].toLowerCase())) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("gender_not_exist"));
        } else {
            String playerArg = args[1];

            UUID player1UUID;

            if (Utils.isUUID(playerArg)) {
                player1UUID = UUID.fromString(args[1]);

                if (PlayerDataTable.getID(player1UUID) < 0) {
                    sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_exist").replace("%player%", playerArg));
                    return true;
                }
            } else {
                player1UUID = PlayerDataTable.getUUID(playerArg);

                if (player1UUID == null) {
                    sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_exist").replace("%player%", playerArg));
                    return true;
                }
            }

            PlayerSender player1 = LunaticLib.getPlatform().getPlayerSender(player1UUID);

            FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);
            if (player1Fam.getGender().equalsIgnoreCase(args[0])) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("admin_gender_already")
                        .replace("%player%", player1.getName())
                        .replace("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[1])));
                return true;
            }
            player1Fam.setGender(args[0].toLowerCase());
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("admin_gender_changed")
                    .replace("%player%", player1.getName())
                    .replace("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[1])));
        }
        return true;

    }
}
