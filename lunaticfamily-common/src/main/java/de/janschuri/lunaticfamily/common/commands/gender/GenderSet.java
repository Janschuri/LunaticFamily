package de.janschuri.lunaticfamily.common.commands.gender;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.*;

public class GenderSet extends FamilyCommand implements HasParams, HasParentCommand {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey setMK = new LunaticCommandMessageKey(this,"set");
    private final CommandMessageKey setHoverMK = new LunaticCommandMessageKey(this,"set_hover");
    private final CommandMessageKey changedMK = new LunaticCommandMessageKey(this,"changed");
    private final CommandMessageKey notExistMK = new LunaticCommandMessageKey(this,"not_exist");
    private final CommandMessageKey alreadyMK = new LunaticCommandMessageKey(this,"already");
    private final CommandMessageKey adminHelpMK = new LunaticCommandMessageKey(this,"admin_help");
    private final CommandMessageKey adminSetMK = new LunaticCommandMessageKey(this,"admin_set");
    private final CommandMessageKey adminAlreadyMK = new LunaticCommandMessageKey(this,"admin_already");
    private final CommandMessageKey genderMK = new LunaticCommandMessageKey(this,"gender");


    @Override
    public String getPermission() {
        return "lunaticfamily.gender";
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public Gender getParentCommand() {
        return new Gender();
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
                                .hoverEvent(HoverEvent.showText(getMessage(setHoverMK.noPrefix(),
                                    placeholder("%gender%", getGenderLang(gender))
                                )))
                );
            }
            player.sendMessage(msg.build());
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof PlayerSender player)) {
                sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            } else {
                UUID playerUUID = player.getUniqueId();
                FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

                if (!LunaticFamily.getLanguageConfig().getGenders().contains(args[0].toLowerCase())) {
                    sender.sendMessage(getMessage(notExistMK));
                } else if (playerFam.getGender().equalsIgnoreCase(args[0])) {
                    player.sendMessage(getMessage(alreadyMK,
                placeholder("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0]))));
                } else {
                    playerFam.setGender(args[0].toLowerCase());
                    playerFam.save();
                    sender.sendMessage(getMessage(changedMK,
                        placeholder("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0]))
                    ));
                }
            }
            return true;
        }
        if (!sender.hasPermission("lunaticfamily.admin.gender")) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (!LunaticFamily.getLanguageConfig().getGenders().contains(args[0].toLowerCase())) {
            sender.sendMessage(getMessage(notExistMK));
            return true;
        }

        String player1Arg = args[1];
        UUID player1UUID = Utils.getUUIDFromArg(player1Arg);
        if (player1UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player1Arg)));
            return true;
        }

            PlayerSender player1 = LunaticLib.getPlatform().getPlayerSender(player1UUID);

            FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);
            if (player1Fam.getGender().equalsIgnoreCase(args[0])) {
                sender.sendMessage(getMessage(adminAlreadyMK,
                placeholder("%player%", player1.getName()),
                placeholder("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0]))));
                return true;
            }
            player1Fam.setGender(args[0].toLowerCase());
            player1Fam.save();
            sender.sendMessage(getMessage(adminSetMK,
                placeholder("%player%", player1.getName()),
                placeholder("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0]))));

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
                getMessage(genderMK.noPrefix()),
                getMessage(PLAYER_NAME_MK.noPrefix())
        );
    }
}
