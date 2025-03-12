package de.janschuri.lunaticfamily.common.commands.gender;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
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

    private static final GenderSet INSTANCE = new GenderSet();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &3(<%param%>) &7- Set your gender.")
            .defaultMessage("de", "&6/%command% %subcommand% &3(<%param%>) &7- Setze dein Geschlecht.");
    private static final CommandMessageKey SET_MK = new LunaticCommandMessageKey(INSTANCE, "set")
            .defaultMessage("en", "Choose your gender: ")
            .defaultMessage("de", "Wähle dein Geschlecht: ");
    private static final CommandMessageKey SET_HOVER_MK = new LunaticCommandMessageKey(INSTANCE, "set_hover")
            .defaultMessage("en", "Set your gender to %gender%.")
            .defaultMessage("de", "Setze dein Geschlecht auf %gender%.");
    private static final CommandMessageKey CHANGED_MK = new LunaticCommandMessageKey(INSTANCE, "changed")
            .defaultMessage("en", "You have changed your gender to %gender%.")
            .defaultMessage("de", "Du hast dein Geschlecht auf %gender% geändert.");
    private static final CommandMessageKey NOT_EXIST_MK = new LunaticCommandMessageKey(INSTANCE, "not_exist")
            .defaultMessage("en", "This gender does not exist.")
            .defaultMessage("de", "Dieses Geschlecht existiert nicht.");
    private static final CommandMessageKey ALREADY_MK = new LunaticCommandMessageKey(INSTANCE, "already")
            .defaultMessage("en", "Your gender is already %gender%.")
            .defaultMessage("de", "Dein Geschlecht ist bereits %gender%.");
    private static final CommandMessageKey ADMIN_HELP_MK = new LunaticCommandMessageKey(INSTANCE, "admin_help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &b<%param%> &7- Set the gender of a player.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &b<%param%> &7- Setze das Geschlecht eines Spielers.");
    private static final CommandMessageKey ADMIN_SET_MK = new LunaticCommandMessageKey(INSTANCE, "admin_set")
            .defaultMessage("en", "You have changed the gender of %player% to %gender%.")
            .defaultMessage("de", "Du hast das Geschlecht von %player% auf %gender% geändert.");
    private static final CommandMessageKey ADMIN_ALREADY_MK = new LunaticCommandMessageKey(INSTANCE, "admin_already")
            .defaultMessage("en", "The gender of %player% is already %gender%.")
            .defaultMessage("de", "Das Geschlecht von %player% ist bereits %gender%.");
    private static final CommandMessageKey GENDER_MK = new LunaticCommandMessageKey(INSTANCE, "gender")
            .defaultMessage("en", "gender")
            .defaultMessage("de", "geschlecht");



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

            ComponentBuilder msg = Component.text().append(getMessage(SET_MK));

            for (String gender : LunaticFamily.getLanguageConfig().getGenders()) {
                msg.append(
                        Component.text("\n - " + LunaticFamily.getLanguageConfig().getGenderLang(gender))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/family gender set " + gender))
                                .hoverEvent(HoverEvent.showText(getMessage(SET_HOVER_MK.noPrefix(),
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
                    sender.sendMessage(getMessage(NOT_EXIST_MK));
                } else if (playerFam.getGender().equalsIgnoreCase(args[0])) {
                    player.sendMessage(getMessage(ALREADY_MK,
                placeholder("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0]))));
                } else {
                    playerFam.setGender(args[0].toLowerCase());
                    playerFam.save();
                    sender.sendMessage(getMessage(CHANGED_MK,
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
            sender.sendMessage(getMessage(NOT_EXIST_MK));
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
                sender.sendMessage(getMessage(ADMIN_ALREADY_MK,
                placeholder("%player%", player1.getName()),
                placeholder("%gender%", LunaticFamily.getLanguageConfig().getGenderLang(args[0]))));
                return true;
            }
            player1Fam.setGender(args[0].toLowerCase());
            player1Fam.save();
            sender.sendMessage(getMessage(ADMIN_SET_MK,
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

        return List.of(genderParams, getOnlinePlayersParam("lunaticfamily.admin.gender"));
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission(),
                ADMIN_HELP_MK, "lunaticfamily.admin.gender"
        );
    }

    @Override
    public List<MessageKey> getParamsNames() {
        return List.of(
                GENDER_MK,
                PLAYER_NAME_MK
        );
    }
}
