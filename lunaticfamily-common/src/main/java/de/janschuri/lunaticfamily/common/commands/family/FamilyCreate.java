package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.*;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.command.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyCreate extends FamilyCommand implements HasParentCommand, HasParams {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey confirmMK = new LunaticCommandMessageKey(this,"confirm");
    private final CommandMessageKey createdMK = new LunaticCommandMessageKey(this,"created");
    private final CommandMessageKey cancelMK = new LunaticCommandMessageKey(this, "cancel");
    private final MessageKey createRandomMK = new LunaticCommandMessageKey(this, "createRandom")
            .defaultMessage("You didn't specified a UUID. Do you want to create a random one?");


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
            Logger.debugLog("FamilyCreate: Wrong usage");
            return true;
        }

        String playerName = args[0];

        if (args.length < 2) {
            UUID randomUUID = UUID.randomUUID();

            DecisionMessage decisionMessage = Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(createRandomMK.noPrefix()),
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

            sender.sendMessage(getMessage(createdMK,
                placeholder("%uuid%", playerUUIDArg),
                placeholder("%name%", playerName))
            );
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(cancelMK,
                placeholder("%uuid%", playerUUIDArg),
                placeholder("%name%", playerName))
            );
            return true;
        }


        player.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                getMessage(confirmMK.noPrefix(),
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
    public List<Component> getParamsNames() {
        return List.of(
            getMessage(PLAYER_NAME_MK.noPrefix()),
            Component.text("UUID")
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of();
    }
}
