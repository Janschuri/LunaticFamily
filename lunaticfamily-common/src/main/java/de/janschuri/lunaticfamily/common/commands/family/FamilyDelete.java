package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.Adoption;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.handler.Marriage;
import de.janschuri.lunaticfamily.common.handler.Siblinghood;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyDelete extends FamilyCommand implements HasParentCommand, HasParams {

    private static final FamilyDelete INSTANCE = new FamilyDelete();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", INSTANCE.getDefaultHelpMessage("Delete a player from the database."))
            .defaultMessage("de", INSTANCE.getDefaultHelpMessage("Lösche einen Spieler aus der Datenbank."));
    private static final CommandMessageKey CONFIRM_MK = new LunaticCommandMessageKey(INSTANCE, "confirm")
            .defaultMessage("en", "Do you really want to delete %uuid% from the database?")
            .defaultMessage("de", "Willst du %uuid% wirklich aus der Datenbank löschen?");
    private static final CommandMessageKey DELETED_MK = new LunaticCommandMessageKey(INSTANCE, "deleted")
            .defaultMessage("en", "You have deleted %uuid% from the database.")
            .defaultMessage("de", "Du hast %uuid% aus der Datenbank gelöscht.");
    private static final CommandMessageKey CANCEL_MK = new LunaticCommandMessageKey(INSTANCE, "cancel")
            .defaultMessage("en", "You have canceled the deletion of %uuid%.")
            .defaultMessage("de", "Du hast die Löschung von %uuid% abgebrochen.");



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
            FamilyPlayer playerFam = FamilyPlayer.find(playerUUID);

            for (Marriage marriage : playerFam.getMarriages()) {
                marriage.delete();
            }

            for (Adoption adoption : playerFam.getAdoptionsAsChild()) {
                adoption.delete();
            }

            for (Adoption adoption : playerFam.getAdoptionsAsParent()) {
                adoption.delete();
            }

            for (Siblinghood siblinghood : playerFam.getSiblinghoods()) {
                siblinghood.delete();
            }

            playerFam.delete();

            sender.sendMessage(getMessage(DELETED_MK,
                placeholder("%uuid%", playerArg)));
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(CANCEL_MK,
                placeholder("%uuid%", playerArg)));
            return true;
        }


        player.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                getMessage(CONFIRM_MK.noPrefix(),
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
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
            HELP_MK, getPermission()
        );
    }

    @Override
    public List<MessageKey> getParamsNames() {
        return List.of(
                UUID_MK
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of();
    }
}
