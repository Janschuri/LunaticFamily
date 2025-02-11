package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.*;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.UUID;

public class FamilyCreate extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey confirmMK = new CommandMessageKey(this,"confirm");
    private final CommandMessageKey createdMK = new CommandMessageKey(this,"created");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this, "cancel");
    private final MessageKey createRandomMK = new CommandMessageKey(this, "createRandom")
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
                    getMessage(createRandomMK, false),
                    getMessage(CONFIRM_MK, false),
                    "/family create " + playerName + " " + randomUUID + " confirm",
                    getMessage(CANCEL_MK, false),
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
            sender.sendMessage(getMessage(NO_UUID_MK)
                    .replaceText(getTextReplacementConfig("%input%", playerUUIDArg)));
            return true;
        }

        UUID playerUUID = UUID.fromString(playerUUIDArg);

        if (confirm) {
            FamilyPlayerImpl familyPlayer = getFamilyPlayer(playerUUID).setName(playerName);
            familyPlayer.save();

            sender.sendMessage(getMessage(createdMK)
                    .replaceText(getTextReplacementConfig("%uuid%", playerUUIDArg))
                    .replaceText(getTextReplacementConfig("%name%", playerName))
            );
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(cancelMK)
                    .replaceText(getTextReplacementConfig("%uuid%", playerUUIDArg))
                    .replaceText(getTextReplacementConfig("%name%", playerName))
            );
            return true;
        }


        player.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                getMessage(confirmMK, false)
                        .replaceText(getTextReplacementConfig("%uuid%", playerUUIDArg))
                        .replaceText(getTextReplacementConfig("%name%", playerName))
                ,
                getMessage(CONFIRM_MK, false),
                "/family create " + playerName + " " + playerUUIDArg + " confirm",
                getMessage(CANCEL_MK, false),
                "/family create " + playerName + " " + playerUUIDArg + " cancel"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );


        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
            getMessage(PLAYER_NAME_MK, false),
            Component.text("UUID")
        );
    }
}
