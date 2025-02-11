package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestAdopt;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import net.kyori.adventure.text.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdoptAccept extends Subcommand {

    private final CommandMessageKey gotAdoptedMK = new CommandMessageKey(this,"got_adopted");
    private final CommandMessageKey adoptedBySingleMK = new CommandMessageKey(this,"adopted_by_single");
    private final CommandMessageKey adoptedMK = new CommandMessageKey(this,"adopted");
    private final CommandMessageKey parentLimitMK = new CommandMessageKey(this,"parent_limit");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");
    private final CommandMessageKey openRequestParentMK = new CommandMessageKey(this,"open_request_parent");

    private final CommandMessageKey priestYesMK = new CommandMessageKey(new PriestAdopt(),"yes");
    private final CommandMessageKey priestNoMK = new CommandMessageKey(new PriestAdopt(),"no");
    private final CommandMessageKey priestCompleteMK = new CommandMessageKey(new PriestAdopt(),"complete");
    private final CommandMessageKey priestAlreadyAdoptedMK = new CommandMessageKey(new PriestAdopt(),"already_adopted");
    private final CommandMessageKey priestRequestMK = new CommandMessageKey(new PriestAdopt(),"request");
    private final CommandMessageKey priestRequestExpiredPriestMK = new CommandMessageKey(new PriestAdopt(),"request_expired_priest");
    private final CommandMessageKey priestRequestExpiredParentMK = new CommandMessageKey(new PriestAdopt(),"request_expired_parent");
    private final CommandMessageKey priestRequestExpiredChildMK = new CommandMessageKey(new PriestAdopt(),"request_expired_child");


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

        if (LunaticFamily.adoptPriestRequests.containsValue(playerUUID)) {
            sender.sendMessage(getMessage(openRequestParentMK));
            return true;
        }

        if (LunaticFamily.adoptRequests.containsKey(playerUUID)) {
            return proceedRequest(player);
        }

        if (LunaticFamily.adoptPriestRequests.containsKey(playerUUID)) {
            return proceedPriestRequest(player);
        }

        sender.sendMessage(getMessage(noRequestMK));
        return true;
    }

    private boolean proceedPriestRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        UUID childUUID = LunaticFamily.adoptPriestRequests.get(playerUUID);
        FamilyPlayer childFam = getFamilyPlayer(childUUID);
        PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);

        if (childFam.isAdopted()) {
            player.sendMessage(getMessage(priestAlreadyAdoptedMK)
                    .replaceText(getTextReplacementConfig("%player%", childFam.getName())));
            return true;
        }

        UUID priestUUID = LunaticFamily.adoptPriests.get(playerUUID);
        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_ADOPT)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", priest.getName()))
            );
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), childUUID, WithdrawKey.PRIEST_ADOPT_CHILD)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", childFam.getName()))
            );
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_ADOPT_PARENT)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", player.getName()))
            );
            return true;
        }

        LunaticFamily.adoptPriestRequests.remove(playerUUID);
        LunaticFamily.adoptRequests.put(childUUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                LunaticFamily.adoptRequests.remove(childUUID);
                LunaticFamily.adoptPriests.remove(childUUID);
                priest.sendMessage(getMessage(priestRequestExpiredPriestMK)
                        .replaceText(getTextReplacementConfig("%player1%", player.getName()))
                        .replaceText(getTextReplacementConfig("%player2%", child.getName())));
                player.sendMessage(getMessage(priestRequestExpiredParentMK)
                        .replaceText(getTextReplacementConfig("%player%", child.getName())));
                child.sendMessage(getMessage(priestRequestExpiredChildMK)
                        .replaceText(getTextReplacementConfig("%player%", player.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

        player.chat(getLanguageConfig().getMessageAsString(priestYesMK, false));


        priest.chat(getLanguageConfig().getMessageAsString(priestRequestMK, false)
                .replace("%player1%", childFam.getName())
                .replace("%player2%", playerFam.getName()));


        child.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(priestYesMK, false),
                "/family adopt accept",
                getMessage(priestNoMK, false),
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
            player.sendMessage(getMessage(priestAlreadyAdoptedMK)
                    .replaceText(getTextReplacementConfig("%player%", playerFam.getName()))
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
            player.sendMessage(getMessage(parentLimitMK)
                    .replaceText(getTextReplacementConfig("%player%", parent1Fam.getName())));
            return true;
        }
        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_CHILD)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), parent1UUID, WithdrawKey.ADOPT_PARENT)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", parent1Fam.getName())));
            return true;
        }

        if (parent1Fam.isMarried()) {
            player.sendMessage(getMessage(gotAdoptedMK).replaceText(getTextReplacementConfig("%player1%", parent1Fam.getName())).replaceText(getTextReplacementConfig("%player2%", parent1Fam.getPartner().getName())));
            parent1.sendMessage(getMessage(adoptedMK).replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
            UUID parent2UUID = parent1Fam.getPartner().getUUID();
            Utils.withdrawMoney(player.getServerName(), parent2UUID, 0.5, WithdrawKey.ADOPT_PARENT);
            Utils.withdrawMoney(player.getServerName(), parent1UUID, 0.5, WithdrawKey.ADOPT_PARENT);
            for (String command : LunaticFamily.getConfig().getSuccessCommands("adopt")) {
                command = command.replace("%parent1%", parent1Fam.getName()).replace("%parent2%", parent1Fam.getPartner().getName()).replace("%child%", playerFam.getName());

                LunaticLib.getPlatform().sendConsoleCommand(command);
            }
        } else {
            player.sendMessage(getMessage(adoptedBySingleMK).replaceText(getTextReplacementConfig("%player%", parent1Fam.getName())));
            for (String command : LunaticFamily.getConfig().getSuccessCommands("adopt_single")) {
                command = command.replace("%parent%", parent1Fam.getName()).replace("%child%", playerFam.getName());
                LunaticLib.getPlatform().sendConsoleCommand(command);
            }
        }

        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_CHILD);

        parent1.sendMessage(getMessage(adoptedMK).replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
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
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", priest.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_ADOPT_CHILD)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), parent1UUID, WithdrawKey.PRIEST_ADOPT_PARENT)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", parent1Fam.getName())));
            return true;
        }



        Utils.withdrawMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_ADOPT);
        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_ADOPT_CHILD);
        Utils.withdrawMoney(player.getServerName(), parent1UUID, WithdrawKey.PRIEST_ADOPT_PARENT);

        player.chat(getLanguageConfig().getMessageAsString(priestYesMK, false));

        priest.chat(getLanguageConfig().getMessageAsString(priestCompleteMK, false)
                .replace("%player1%", playerFam.getName())
                .replace("%player2%", parent1Fam.getName()));

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