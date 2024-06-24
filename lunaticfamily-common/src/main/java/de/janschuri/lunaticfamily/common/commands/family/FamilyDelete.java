package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;

import java.util.List;

public class FamilyDelete extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey confirmMK = new CommandMessageKey(this,"confirm");
    private final CommandMessageKey deletedMK = new CommandMessageKey(this,"deleted");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this, "cancel");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.delete";
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            if (args.length < 1) {
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("FamilyDeleteSubcommand: Wrong usage");
                return true;
            }

            boolean confirm = false;
            boolean cancel = false;

            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("confirm")) {
                    confirm = true;
                }
                if (args[1].equalsIgnoreCase("cancel")) {
                    cancel = true;
                }
            }

            String playerArg = args[0];

            if (!Utils.isUUID(playerArg)) {
                sender.sendMessage(getMessage(NO_UUID_MK)
                        .replaceText(getTextReplacementConfig("%input%", playerArg)));
                return true;
            }

                if (confirm) {
                    PlayerDataTable.deletePlayerData(playerArg);
                    sender.sendMessage(getMessage(deletedMK).replaceText(getTextReplacementConfig("%uuid%", playerArg)));
                }

            if (cancel) {
                PlayerDataTable.deletePlayerData(playerArg);
                sender.sendMessage(getMessage(cancelMK).replaceText(getTextReplacementConfig("%uuid%", playerArg)));
            }


            sender.sendMessage(Utils.getClickableDecisionMessage(
                    getMessage(confirmMK).replaceText(getTextReplacementConfig("%uuid%", playerArg)),
                            getMessage(CONFIRM_MK, false),
                            "/family delete " + playerArg + " confirm",
                            getMessage(CANCEL_MK, false),
                            "/family delete " + playerArg + " cancel"));

        }
        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
            Component.text("UUID")
        );
    }
}
