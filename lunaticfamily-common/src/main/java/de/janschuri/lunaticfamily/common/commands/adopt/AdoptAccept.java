package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestAdopt;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdoptAccept extends FamilyCommand implements HasParentCommand {

    private static final AdoptAccept INSTANCE = new AdoptAccept();
    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE,"help")
            .defaultMessage("en", "&6/%command% %subcommand% &7 - Accept an adoption request.")
            .defaultMessage("de", "&6/%command% %subcommand% &7 - Akzeptiere eine Adoptionsanfrage.");
    private static final CommandMessageKey GOT_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE,"got_adopted")
            .defaultMessage("en", "You got adopted by %player1% and %player2%.")
            .defaultMessage("de", "Du wurdest von %player1% und %player2% adoptiert.");
    private static final CommandMessageKey ADOPTED_BY_SINGLE_MK = new LunaticCommandMessageKey(INSTANCE,"adopted_by_single")
            .defaultMessage("en", "You got adopted by %player%.")
            .defaultMessage("de", "Du wurdest von %player% adoptiert.");
    private static final CommandMessageKey ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE,"adopted")
            .defaultMessage("en", "You adopted %player%.")
            .defaultMessage("de", "Du hast %player% adoptiert.");
    private static final CommandMessageKey PARENT_LIMIT_MK = new LunaticCommandMessageKey(INSTANCE,"parent_limit")
            .defaultMessage("en", "%player% cannot adopt another child. %player% has already reached the limit of two children.")
            .defaultMessage("de", "%player% kann kein weiteres Kind adoptieren. %player% hat bereits das Limit von zwei Kindern erreicht.");
    private static final CommandMessageKey NO_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE,"no_request")
            .defaultMessage("en", "You don't have any open adoption requests.")
            .defaultMessage("de", "Du hast keine offenen Adoptionsanfragen.");


    private static final PriestAdopt PRIEST_ADOPT_INSTANCE = new PriestAdopt();
    private static final CommandMessageKey PRIEST_YES_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"yes")
            .defaultMessage("en", "Yes, I do.")
            .defaultMessage("de", "Ja, ich will.");
    private static final CommandMessageKey PRIEST_NO_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"no")
            .defaultMessage("en", "No, I don't want to.")
            .defaultMessage("de", "Nein, ich will nicht.");
    private static final CommandMessageKey PRIEST_COMPLETE_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"complete")
            .defaultMessage("en", "The adoption of %child% by %parent% is completed.")
            .defaultMessage("de", "Die Adoption von %child% durch %parent% ist abgeschlossen.");
    private static final CommandMessageKey PRIEST_ALREADY_ADOPTED_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"already_adopted")
            .defaultMessage("en", "%player% is already adopted.")
            .defaultMessage("de", "%player% ist bereits adoptiert.");
    private static final CommandMessageKey PRIEST_REQUEST_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"request")
            .defaultMessage("en", "%player1%, would you like to adopt %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du %player2% auf diesem Minecraft-Server adoptieren?");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"request_expired_priest")
            .defaultMessage("en", "The adoption request of %parent% for %child% has expired.")
            .defaultMessage("de", "Die Adoptionsanfrage von %parent% für %child% ist abgelaufen.");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_PARENT_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"request_expired_parent")
            .defaultMessage("en", "The adoption request for %child% has expired.")
            .defaultMessage("de", "Die Adoptionsanfrage für %child% ist abgelaufen.");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_CHILD_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"request_expired_child")
            .defaultMessage("en", "The adoption request of %parent% for you has expired.")
            .defaultMessage("de", "Die Adoptionsanfrage von %parent% für dich ist abgelaufen.");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "accept";
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

        UUID playerUUID = player.getUniqueId();

        if (LunaticFamily.adoptRequests.containsKey(playerUUID)) {
            return proceedRequest(player);
        }

        if (LunaticFamily.adoptPriestRequests.containsKey(playerUUID)) {
            return proceedPriestRequest(player);
        }

        sender.sendMessage(getMessage(NO_REQUEST_MK));
        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }

    private boolean proceedPriestRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        UUID childUUID = LunaticFamily.adoptPriestRequests.get(playerUUID);
        FamilyPlayer childFam = getFamilyPlayer(childUUID);
        PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);

        if (childFam.isAdopted()) {
            player.sendMessage(getMessage(PRIEST_ALREADY_ADOPTED_MK,
                placeholder("%player%", childFam.getName())));
            return true;
        }

        UUID priestUUID = LunaticFamily.adoptPriests.get(playerUUID);
        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_ADOPT)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", priest.getName()))
            );
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), childUUID, WithdrawKey.PRIEST_ADOPT_CHILD)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", childFam.getName()))
            );
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_ADOPT_PARENT)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName()))
            );
            return true;
        }

        LunaticFamily.adoptPriestRequests.remove(playerUUID);
        LunaticFamily.adoptRequests.put(childUUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                LunaticFamily.adoptRequests.remove(childUUID);
                LunaticFamily.adoptPriests.remove(childUUID);
                priest.sendMessage(getMessage(PRIEST_REQUEST_EXPIRED_PRIEST_MK,
                placeholder("%parent%", player.getName()),
                placeholder("%child%", child.getName())));
                player.sendMessage(getMessage(PRIEST_REQUEST_EXPIRED_PARENT_MK,
                placeholder("%child%", child.getName())));
                child.sendMessage(getMessage(PRIEST_REQUEST_EXPIRED_CHILD_MK,
                placeholder("%parent%", player.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

        player.chat(getLanguageConfig().getMessageAsString(PRIEST_YES_MK.noPrefix()));


        priest.chat(getLanguageConfig().getMessageAsString(PRIEST_REQUEST_MK.noPrefix())
                .replace("%child%", childFam.getName())
                .replace("%parent%", playerFam.getName()));


        child.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(PRIEST_YES_MK.noPrefix()),
                "/family adopt accept",
                getMessage(PRIEST_NO_MK.noPrefix()),
                "/family adopt deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );


        return true;
    }

    private boolean proceedRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        UUID parent1UUID = LunaticFamily.adoptRequests.get(playerUUID);

        if (playerFam.isAdopted()) {
            player.sendMessage(getMessage(PRIEST_ALREADY_ADOPTED_MK,
                placeholder("%player%", playerFam.getName()))
            );
            return true;
        }

        if (LunaticFamily.adoptPriests.containsKey(parent1UUID)) {
            return acceptPriestRequest(player);
        } else {
            return acceptRequest(player);
        }
    }

    private boolean acceptRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

        UUID parent1UUID = LunaticFamily.adoptRequests.get(playerUUID);
        FamilyPlayer parent1Fam = getFamilyPlayer(parent1UUID);
        PlayerSender parent1 = LunaticLib.getPlatform().getPlayerSender(parent1UUID);

        if (parent1Fam.getChildrenAmount() > 1) {
            player.sendMessage(getMessage(PARENT_LIMIT_MK,
                placeholder("%player%", parent1Fam.getName())));
            return true;
        }
        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_CHILD)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), parent1UUID, WithdrawKey.ADOPT_PARENT)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", parent1Fam.getName())));
            return true;
        }

        if (parent1Fam.isMarried()) {
            player.sendMessage(getMessage(GOT_ADOPTED_MK,
                placeholder("%player1%", parent1Fam.getName()),
                placeholder("%player2%", parent1Fam.getPartner().getName())));
            parent1.sendMessage(getMessage(ADOPTED_MK,
                placeholder("%player%", playerFam.getName())));
            UUID parent2UUID = parent1Fam.getPartner().getUUID();
            Utils.withdrawMoney(player.getServerName(), parent2UUID, 0.5, WithdrawKey.ADOPT_PARENT);
            Utils.withdrawMoney(player.getServerName(), parent1UUID, 0.5, WithdrawKey.ADOPT_PARENT);
            for (String command : LunaticFamily.getConfig().getSuccessCommands("adopt")) {
                command = command.replace("%parent1%", parent1Fam.getName()).replace("%parent2%", parent1Fam.getPartner().getName()).replace("%child%", playerFam.getName());

                LunaticLib.getPlatform().sendConsoleCommand(command);
            }
        } else {
            player.sendMessage(getMessage(ADOPTED_BY_SINGLE_MK,
                placeholder("%player%", parent1Fam.getName())));
            for (String command : LunaticFamily.getConfig().getSuccessCommands("adopt_single")) {
                command = command.replace("%parent%", parent1Fam.getName()).replace("%child%", playerFam.getName());
                LunaticLib.getPlatform().sendConsoleCommand(command);
            }
        }

        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_CHILD);

        parent1.sendMessage(getMessage(ADOPTED_MK,
                placeholder("%player%", playerFam.getName())));
        LunaticFamily.adoptRequests.remove(playerUUID);
        parent1Fam.adopt(playerFam);

        return true;
    }

    private boolean acceptPriestRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        UUID parent1UUID = LunaticFamily.adoptRequests.get(playerUUID);

        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        FamilyPlayer parent1Fam = getFamilyPlayer(parent1UUID);

        UUID priestUUID = LunaticFamily.adoptPriests.get(parent1UUID);
        FamilyPlayer priestFam = getFamilyPlayer(priestUUID);
        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_ADOPT)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", priest.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_ADOPT_CHILD)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), parent1UUID, WithdrawKey.PRIEST_ADOPT_PARENT)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", parent1Fam.getName())));
            return true;
        }



        Utils.withdrawMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_ADOPT);
        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_ADOPT_CHILD);
        Utils.withdrawMoney(player.getServerName(), parent1UUID, WithdrawKey.PRIEST_ADOPT_PARENT);

        player.chat(getLanguageConfig().getMessageAsString(PRIEST_YES_MK.noPrefix()));

        priest.chat(getLanguageConfig().getMessageAsString(PRIEST_COMPLETE_MK.noPrefix())
                .replace("%child%", playerFam.getName())
                .replace("%parent%", parent1Fam.getName()));

        LunaticFamily.adoptRequests.remove(playerUUID);
        LunaticFamily.adoptPriestRequests.remove(parent1UUID);
        LunaticFamily.adoptPriests.remove(parent1UUID);

        parent1Fam.adopt(playerFam, priestFam);

        for (String command : LunaticFamily.getConfig().getSuccessCommands("adopt_priest")) {
            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", parent1Fam.getName()).replace("%priest%", priestFam.getName());
            LunaticLib.getPlatform().sendConsoleCommand(command);
        }

        return true;
    }
}