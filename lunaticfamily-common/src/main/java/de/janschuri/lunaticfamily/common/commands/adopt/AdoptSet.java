package de.janschuri.lunaticfamily.common.commands.adopt;

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

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdoptSet extends FamilyCommand implements HasParams, HasParentCommand {

    private static final AdoptSet INSTANCE = new AdoptSet();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", INSTANCE.getDefaultHelpMessage("Set the adoption of a child by a player."))
            .defaultMessage("de", INSTANCE.getDefaultHelpMessage("Setze die Adoption eines Kindes durch einen Spieler."));
    private static final CommandMessageKey NO_SINGLE_ADOPT_MK = new LunaticCommandMessageKey(INSTANCE, "no_single_adopt")
            .defaultMessage("en", "%player% is not married. You must be married to adopt a child.")
            .defaultMessage("de", "%player% ist nicht verheiratet. Du musst verheiratet sein, um ein Kind zu adoptieren.");
    private static final CommandMessageKey ADOPT_LIMIT_MK = new LunaticCommandMessageKey(INSTANCE, "adopt_limit")
            .defaultMessage("en", "%player% already has two children.")
            .defaultMessage("de", "%player% hat bereits zwei Kinder.");
    private static final CommandMessageKey SAME_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "same_player")
            .defaultMessage("en", "A player cannot be their own child.")
            .defaultMessage("de", "Ein Spieler kann nicht sein eigenes Kind sein.");
    private static final CommandMessageKey ALREADY_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "already_adopted")
            .defaultMessage("en", "%player% is already adopted.")
            .defaultMessage("de", "%player% ist bereits adoptiert.");
    private static final CommandMessageKey HAS_SIBLING_MK = new LunaticCommandMessageKey(INSTANCE, "has_sibling")
            .defaultMessage("en", "%player% already has a sibling. Should that also be adopted?")
            .defaultMessage("de", "%player% hat bereits ein Geschwister. Soll das auch adoptiert werden?");
    private static final CommandMessageKey HAS_SIBLING_LIMIT_MK = new LunaticCommandMessageKey(INSTANCE, "has_sibling_limit")
            .defaultMessage("en", "%player% already has a sibling. %player2% cannot adopt both as one child has already been adopted.")
            .defaultMessage("de", "%player% hat bereits ein Geschwister. %player2% kann nicht beide adoptieren, da bereits ein Kind adoptiert wurde.");
    private static final CommandMessageKey CANCEL_MK = new LunaticCommandMessageKey(INSTANCE, "cancel")
            .defaultMessage("en", "You have canceled the adoption of %child% by %parent%.")
            .defaultMessage("de", "Du hast die Adoption von %child% durch %parent% abgebrochen.");
    private static final CommandMessageKey SET_MK = new LunaticCommandMessageKey(INSTANCE, "set")
            .defaultMessage("en", "You have set the adoption of %child% by %parent1% and %parent2%.")
            .defaultMessage("de", "Du hast die Adoption von %child% durch %parent1% und %parent2% festgelegt.");
    private static final CommandMessageKey SET_BY_SINGLE_MK = new LunaticCommandMessageKey(INSTANCE, "set_by_single")
            .defaultMessage("en", "You have set the adoption of %child% by %parent%.")
            .defaultMessage("de", "Du hast die Adoption von %child% durch %parent% festgelegt.");
    private static final CommandMessageKey SAME_FAMILY_MK = new LunaticCommandMessageKey(INSTANCE, "same_family")
            .defaultMessage("en", "%player1% and %player2% are already family.")
            .defaultMessage("de", "%player1% und %player2% sind bereits Familie.");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.adopt";
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public Adopt getParentCommand() {
        return new Adopt();
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
        boolean force = false;
        boolean confirm = false;
        boolean cancel = false;

        if (args.length > 2) {
            if (args[2].equalsIgnoreCase("force")) {
                force = true;
            }
        }

        if (args.length > 3) {
            if (args[3].equalsIgnoreCase("confirm")) {
                confirm = true;
            }
            if (args[3].equalsIgnoreCase("cancel")) {
                cancel = true;
            }
        }

        if (args.length < 2) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(CANCEL_MK,
                placeholder("%parent%", args[0]),
                placeholder("%child%", args[1])));
            return true;
        }

        String firstParentArg = args[0];
        FamilyPlayer firstParentFam;

        if (Utils.isUUID(firstParentArg)) {
            UUID firstParentUUID = UUID.fromString(firstParentArg);
            firstParentFam = FamilyPlayer.find(firstParentUUID);
        } else {
            firstParentFam = FamilyPlayer.find(firstParentArg);
        }

        if (firstParentFam == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", firstParentArg)));
            return true;
        }

        String childArg = args[1];
        FamilyPlayer childFam;

        if (Utils.isUUID(childArg)) {
            UUID childUUID = UUID.fromString(childArg);
            childFam = FamilyPlayer.find(childUUID);
        } else {
            childFam = FamilyPlayer.find(childArg);
        }

        if (childFam == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", childArg)));
            return true;
        }

        UUID firstParentUUID = firstParentFam.getUUID();
        UUID childUUID = childFam.getUUID();
        PlayerSender firstParent = LunaticLib.getPlatform().getPlayerSender(firstParentUUID);
        PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);


        if (args[0].equalsIgnoreCase(args[1])) {
            sender.sendMessage(getMessage(SAME_PLAYER_MK));
            return true;
        }

        firstParentFam.update();

        if (firstParentFam.isFamilyMember(childFam)) {
            sender.sendMessage(getMessage(SAME_FAMILY_MK,
                placeholder("%player1%", firstParentFam.getName()),
                placeholder("%player2%", childFam.getName())));
            return true;
        }

        if (childFam.isFamilyMember(firstParentFam)) {
            sender.sendMessage(getMessage(SAME_FAMILY_MK,
                    placeholder("%player1%", firstParentFam.getName()),
                    placeholder("%player2%", childFam.getName())));
            return true;
        }

        if (!firstParentFam.isMarried() && !LunaticFamily.getConfig().isAllowSingleAdopt()) {
            sender.sendMessage(getMessage(NO_SINGLE_ADOPT_MK,
                placeholder("%player%", firstParentFam.getName())));
            return true;
        }

        if (childFam.isAdopted()) {
            sender.sendMessage(getMessage(ALREADY_ADOPTED_MK,
                placeholder("%child%", childFam.getName())));
            return true;
        }

        int childAmount = firstParentFam.getChildrenAmount() + 1;

        if (LunaticFamily.exceedsAdoptLimit(childAmount)) {
            sender.sendMessage(getMessage(ADOPT_LIMIT_MK,
                placeholder("%player%", firstParentFam.getName())));
            return true;
        }

        if (childFam.hasSiblings() && !confirm) {
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(HAS_SIBLING_MK.noPrefix(),
                placeholder("%player%", childFam.getName())),
                    getMessage(CONFIRM_MK.noPrefix()),
                    "/family adopt set " + firstParentFam.getName() + " " + childFam.getName() + " force confirm",
                    getMessage(CANCEL_MK.noPrefix()),
                    "/family adopt set " + firstParentFam.getName() + " " + child.getName() + "force cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (childFam.hasSiblings() && firstParentFam.getChildrenAmount() > 0) {
            sender.sendMessage(getMessage(HAS_SIBLING_LIMIT_MK,
                placeholder("%player1%", childFam.getName()),
                placeholder("%player2%", firstParentFam.getName())));
            return true;
        }

        if (!firstParentFam.isMarried()) {
            sender.sendMessage(getMessage(SET_BY_SINGLE_MK,
                placeholder("%child%", childFam.getName()),
                placeholder("%parent%", firstParentFam.getName())));
        } else {
            FamilyPlayer secondParentFam = firstParentFam.getPartner();
            sender.sendMessage(getMessage(SET_MK,
                placeholder("%child%", childFam.getName()),
                placeholder("%parent1%", firstParentFam.getName()),
                placeholder("%parent2%", secondParentFam.getName())));
        }

        LunaticFamily.adoptRequests.remove(childUUID);
        firstParentFam.adopt(childFam);



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
                PLAYER_NAME_MK
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam(), getOnlinePlayersParam());
    }
}
