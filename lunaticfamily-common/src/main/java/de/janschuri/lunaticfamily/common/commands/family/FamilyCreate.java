package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.*;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyCreate extends FamilyCommand implements HasParentCommand, HasParams {

    private static final FamilyCreate INSTANCE = new FamilyCreate();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &b<%param%> &7- Create a player in the database.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &b<%param%> &7- Erstelle einen Spieler in der Datenbank.");
    private static final CommandMessageKey CREATED_MK = new LunaticCommandMessageKey(INSTANCE, "created")
            .defaultMessage("en", "You have created %name% with the UUID %uuid%.")
            .defaultMessage("de", "Du hast %name% mit der UUID %uuid% erstellt.");
    private static final CommandMessageKey CONFIRM_MK = new LunaticCommandMessageKey(INSTANCE, "confirm")
            .defaultMessage("en", "Do you really want to create %name% with the UUID %uuid%?")
            .defaultMessage("de", "Willst du %name% mit der UUID %uuid% wirklich erstellen?");
    private static final CommandMessageKey CANCEL_MK = new LunaticCommandMessageKey(INSTANCE, "cancel")
            .defaultMessage("en", "You have canceled the creation of %name% with the UUID %uuid%.")
            .defaultMessage("de", "Du hast die Erstellung von %name% mit der UUID %uuid% abgebrochen.");
    private static final MessageKey CREATE_RANDOM_MK = new LunaticCommandMessageKey(INSTANCE, "createRandom")
            .defaultMessage("en", "You didn't specified a UUID. Do you want to create a random one?")
            .defaultMessage("de", "Du hast keine UUID angegeben. Möchtest du eine zufällige erstellen?");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.create";
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            return true;
        }

        String playerName = args[0];

        if (args.length < 2) {
            UUID randomUUID = UUID.randomUUID();

            DecisionMessage decisionMessage = Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(CREATE_RANDOM_MK.noPrefix()),
                    getMessage(CONFIRM_MK.noPrefix()),
                    "/family create " + playerName + " " + randomUUID + " confirm",
                    getMessage(CANCEL_MK.noPrefix()),
                    "/family create " + playerName + " " + randomUUID + " cancel"
            );

            player.sendMessage(
                    decisionMessage,
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );

            return true;
        }

        boolean confirm = false;
        boolean cancel = false;

        if (args.length > 2) {
            if (args[2].equalsIgnoreCase("confirm")) {
                confirm = true;
            }
            if (args[2].equalsIgnoreCase("cancel")) {
                cancel = true;
            }
        }

        String playerUUIDArg = args[1];

        if (!Utils.isUUID(playerUUIDArg)) {
            sender.sendMessage(getMessage(NO_UUID_MK,
                placeholder("%input%", playerUUIDArg)));
            return true;
        }

        UUID playerUUID = UUID.fromString(playerUUIDArg);

        if (confirm) {
            FamilyPlayer familyPlayer = getFamilyPlayer(playerUUID).setName(playerName);
            familyPlayer.save();

            sender.sendMessage(getMessage(CREATED_MK,
                placeholder("%uuid%", playerUUIDArg),
                placeholder("%name%", playerName))
            );
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(CANCEL_MK,
                placeholder("%uuid%", playerUUIDArg),
                placeholder("%name%", playerName))
            );
            return true;
        }


        player.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                getMessage(CONFIRM_MK.noPrefix(),
                placeholder("%uuid%", playerUUIDArg),
                placeholder("%name%", playerName))
                ,
                getMessage(CONFIRM_MK.noPrefix()),
                "/family create " + playerName + " " + playerUUIDArg + " confirm",
                getMessage(CANCEL_MK.noPrefix()),
                "/family create " + playerName + " " + playerUUIDArg + " cancel"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );


        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
            HELP_MK, getPermission()
        );
    }

    @Override
    public List<MessageKey> getParamsNames() {
        return List.of(
            PLAYER_NAME_MK,
            UUID_MK
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of();
    }
}
