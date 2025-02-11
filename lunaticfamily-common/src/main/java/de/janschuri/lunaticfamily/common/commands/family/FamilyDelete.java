package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.Adoption;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.handler.Marriage;
import de.janschuri.lunaticfamily.common.handler.Siblinghood;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.UUID;

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
            sender.sendMessage(getMessage(NO_UUID_MK,
                placeholder("%input%", playerArg)));
            return true;
        }

        if (confirm) {
            UUID playerUUID = UUID.fromString(playerArg);
            FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

            for (Marriage marriage : playerFam.getMarriages()) {
                DatabaseRepository.getDatabase().delete(marriage);
            }

            for (Adoption adoption : playerFam.getAdoptionsAsChild()) {
                DatabaseRepository.getDatabase().delete(adoption);
            }

            for (Adoption adoption : playerFam.getAdoptionsAsParent()) {
                DatabaseRepository.getDatabase().delete(adoption);
            }

            for (Siblinghood siblinghood : playerFam.getSiblinghoods()) {
                DatabaseRepository.getDatabase().delete(siblinghood);
            }

            DatabaseRepository.getDatabase().delete(playerFam);

            sender.sendMessage(getMessage(deletedMK,
                placeholder("%uuid%", playerArg)));
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(cancelMK,
                placeholder("%uuid%", playerArg)));
            return true;
        }


        player.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                getMessage(confirmMK.noPrefix(),
                placeholder("%uuid%", playerArg)),
                getMessage(CONFIRM_MK.noPrefix()),
                "/family delete " + playerArg + " confirm",
                getMessage(CANCEL_MK.noPrefix()),
                "/family delete " + playerArg + " cancel"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );


        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
            Component.text("UUID")
        );
    }
}
