package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.UUID;

public class FamilyCreate extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey confirmMK = new CommandMessageKey(this,"confirm");
    private final CommandMessageKey createdMK = new CommandMessageKey(this,"created");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this, "cancel");


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

        if (args.length < 2) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("FamilyCreate: Wrong usage");
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

        String playerName = args[0];

        String playerUUIDArg = args[1];

        if (!Utils.isUUID(playerUUIDArg)) {
            sender.sendMessage(getMessage(NO_UUID_MK)
                    .replaceText(getTextReplacementConfig("%input%", playerUUIDArg)));
            return true;
        }

        UUID playerUUID = UUID.fromString(playerUUIDArg);

        if (confirm) {
            new FamilyPlayerImpl(playerUUID, playerName);
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
