package de.janschuri.lunaticfamily.common.commands.gender;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.GenderSubcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.*;

public class GenderSetSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey setMK = new CommandMessageKey(this,"set");
    private final CommandMessageKey setHoverMK = new CommandMessageKey(this,"set_hover");
    private final CommandMessageKey changedMK = new CommandMessageKey(this,"changed");
    private final CommandMessageKey notExistMK = new CommandMessageKey(this,"not_exist");
    private final CommandMessageKey alreadyMK = new CommandMessageKey(this,"already");
    private final CommandMessageKey adminHelpMK = new CommandMessageKey(this,"admin_help");
    private final CommandMessageKey adminSetMK = new CommandMessageKey(this,"admin_set");
    private final CommandMessageKey adminAlreadyMK = new CommandMessageKey(this,"admin_already");
    private final CommandMessageKey genderMK = new CommandMessageKey(this,"gender");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.gender";
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public GenderSubcommand getParentCommand() {
        return new GenderSubcommand();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof PlayerSender)) {
                sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            }
            PlayerSender player = (PlayerSender) sender;

            ComponentBuilder msg = Component.text().append(getMessage(setMK));

            for (String gender : LunaticFamily.getLanguageConfig().getGenders()) {
                msg.append(
                        Component.text("\n - " + LunaticFamily.getLanguageConfig().getGenderLang(gender))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/family gender set " + gender))
                                .hoverEvent(HoverEvent.showText(getMessage(setHoverMK, false)
                                        .replaceText(getTextReplacementConfig("%gender%", getGenderLang(gender)))))
                );
            }
            player.sendMessage(msg.build());
            return true;
        }
        if (args.length == 1) {
            if (!(sender instanceof PlayerSender)) {
                sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            } else {
                PlayerSender player = (PlayerSender) sender;
                UUID playerUUID = player.getUniqueId();
                FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

                if (!LunaticFamily.getLanguageConfig().getGenders().contains(args[0].toLowerCase())) {
                    sender.sendMessage(getMessage(notExistMK));
                } else if (playerFam.getGender().equalsIgnoreCase(args[0])) {
                    player.sendMessage(getMessage(alreadyMK)
                            .replaceText(getTextReplacementConfig("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0]))));
                } else {
                    playerFam.setGender(args[0].toLowerCase());
                    sender.sendMessage(getMessage(changedMK)
                            .replaceText(getTextReplacementConfig("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0]))));
                }
            }
            return true;
        }
        if (!sender.hasPermission("lunaticfamily.admin.gender")) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else if (!LunaticFamily.getLanguageConfig().getGenders().contains(args[0].toLowerCase())) {
            sender.sendMessage(getMessage(notExistMK));
        } else {
            String playerArg = args[1];

            UUID player1UUID;

            if (Utils.isUUID(playerArg)) {
                player1UUID = UUID.fromString(args[1]);

                if (PlayerDataTable.getID(player1UUID) < 0) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", playerArg)));
                    return true;
                }
            } else {
                player1UUID = PlayerDataTable.getUUID(playerArg);

                if (player1UUID == null) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", playerArg)));
                    return true;
                }
            }

            PlayerSender player1 = LunaticLib.getPlatform().getPlayerSender(player1UUID);

            FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);
            if (player1Fam.getGender().equalsIgnoreCase(args[0])) {
                sender.sendMessage(getMessage(adminAlreadyMK)
                        .replaceText(getTextReplacementConfig("%player%", player1.getName()))
                        .replaceText(getTextReplacementConfig("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[1]))));
                return true;
            }
            player1Fam.setGender(args[0].toLowerCase());
            sender.sendMessage(getMessage(adminSetMK)
                    .replaceText(getTextReplacementConfig("%player%", player1.getName()))
                    .replaceText(getTextReplacementConfig("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[1]))));
        }
        return true;

    }

    @Override
    public List<Map<String, String>> getParams() {
        List<String> genders = LunaticFamily.getLanguageConfig().getGenders();

        Map<String, String> genderParams = new HashMap<>();

        for (String gender : genders) {
            genderParams.put(gender, getPermission());
        }

        return List.of(genderParams, getOnlinePlayersParam());
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                helpMK, getPermission(),
                adminHelpMK, "lunaticfamily.admin.gender"
        );
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(genderMK, false),
                getMessage(PLAYER_NAME_MK, false)
        );
    }
}
