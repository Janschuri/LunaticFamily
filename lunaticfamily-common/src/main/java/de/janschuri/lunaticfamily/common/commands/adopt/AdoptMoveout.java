package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.UUID;

public class AdoptMoveout extends FamilyCommand implements HasParentCommand {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey moveoutMK = new LunaticCommandMessageKey(this,"moveout");
    private final CommandMessageKey confirmMK = new LunaticCommandMessageKey(this,"confirm");
    private final CommandMessageKey childMK = new LunaticCommandMessageKey(this,"child");
    private final CommandMessageKey noParentsMK = new LunaticCommandMessageKey(this,"no_parents");
    private final CommandMessageKey siblingMK = new LunaticCommandMessageKey(this,"sibling");
    private final CommandMessageKey cancelMK = new LunaticCommandMessageKey(this,"cancel");

    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "moveout";
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
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

        boolean confirm = false;
        boolean cancel = false;
        boolean force = false;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("confirm")) {
                confirm = true;
            }
            if (args[0].equalsIgnoreCase("cancel")) {
                cancel = true;
            }
        }

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("force")) {
                force = true;
            }
        }


        if (!playerFam.isAdopted()) {
            player.sendMessage(getMessage(noParentsMK));
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(cancelMK));
            return true;
        }

        if (!confirm) {
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(confirmMK.noPrefix()),
                    getMessage(CONFIRM_MK.noPrefix()),
                    "/family adopt moveout confirm",
                    getMessage(CANCEL_MK.noPrefix()),
                    "/family adopt moveout cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (!force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_MOVEOUT_CHILD)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        UUID parent1UUID = playerFam.getParents().get(0).getUUID();
        PlayerSender firstParent = LunaticLib.getPlatform().getPlayerSender(parent1UUID);

        if (!force && playerFam.getParents().size() == 2 && !Utils.hasEnoughMoney(player.getServerName(), parent1UUID, 0.5, WithdrawKey.ADOPT_MOVEOUT_PARENT)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", playerFam.getParents().get(1).getName())));
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(TAKE_PAYMENT_CONFIRM_MK.noPrefix()),
                    getMessage(CONFIRM_MK.noPrefix()),
                    "/family adopt moveout confirm force",
                    getMessage(CANCEL_MK.noPrefix()),
                    "/family adopt moveout cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (!force && playerFam.getParents().size() == 1 && !Utils.hasEnoughMoney(player.getServerName(), parent1UUID, WithdrawKey.ADOPT_MOVEOUT_PARENT)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", playerFam.getParents().get(0).getName())));
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(TAKE_PAYMENT_CONFIRM_MK.noPrefix()),
                    getMessage(CONFIRM_MK.noPrefix()),
                    "/family adopt moveout confirm force",
                    getMessage(CANCEL_MK.noPrefix()),
                    "/family adopt moveout cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (playerFam.getParents().size() > 1) {
            UUID parent2UUID = playerFam.getParents().get(1).getUUID();
            PlayerSender secondParent = LunaticLib.getPlatform().getPlayerSender(parent2UUID);

            if (!force && !Utils.hasEnoughMoney(player.getServerName(), parent2UUID, 0.5, WithdrawKey.ADOPT_MOVEOUT_PARENT)) {
                player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", playerFam.getParents().get(1).getName())));
                player.sendMessage(Utils.getClickableDecisionMessage(
                                getPrefix(),
                                getMessage(TAKE_PAYMENT_CONFIRM_MK.noPrefix()),
                                getMessage(CONFIRM_MK.noPrefix()),
                                "/family adopt moveout confirm force",
                                getMessage(CANCEL_MK.noPrefix()),
                                "/family adopt moveout cancel"),
                        LunaticFamily.getConfig().decisionAsInvGUI()
                );
                return true;
            }
        }

        if (force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_MOVEOUT_PARENT, WithdrawKey.ADOPT_MOVEOUT_CHILD)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        FamilyPlayer firstParentFam = playerFam.getParents().get(0);

        if (playerFam.hasSiblings()) {
            FamilyPlayer siblingFam = playerFam.getSibling();
            Sender sibling = LunaticLib.getPlatform().getPlayerSender(siblingFam.getUUID());
            sibling.sendMessage(getMessage(siblingMK));
        }

        sender.sendMessage(getMessage(moveoutMK));


        firstParent.sendMessage(getMessage(childMK,
                placeholder("%player%", playerFam.getName())));
        if (playerFam.getParents().size() > 1) {
            UUID parent2UUID = playerFam.getParents().get(1).getUUID();
            PlayerSender secondParent = LunaticLib.getPlatform().getPlayerSender(parent2UUID);
            secondParent.sendMessage(getMessage(childMK,
                placeholder("%player%", playerFam.getName())));
        }

        if (force) {
            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_MOVEOUT_CHILD, WithdrawKey.ADOPT_MOVEOUT_PARENT);
        } else {
            if (playerFam.getParents().size() > 1) {
                UUID parent2UUID = playerFam.getParents().get(1).getUUID();
                FamilyPlayer secondParentFam = firstParentFam.getPartner();
                Utils.withdrawMoney(player.getServerName(), parent2UUID, 0.5, WithdrawKey.ADOPT_MOVEOUT_PARENT);
                Utils.withdrawMoney(player.getServerName(), parent1UUID, 0.5, WithdrawKey.ADOPT_MOVEOUT_PARENT);

                for (String command : LunaticFamily.getConfig().getSuccessCommands("moveout")) {
                    command = command.replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()).replace("%child%", playerFam.getName());
                    LunaticLib.getPlatform().sendConsoleCommand(command);
                }
            } else {
                Utils.withdrawMoney(player.getServerName(), parent1UUID, WithdrawKey.ADOPT_MOVEOUT_PARENT);

                for (String command : LunaticFamily.getConfig().getSuccessCommands("moveout_single")) {
                    command = command.replace("%parent%", firstParentFam.getName()).replace("%child%", playerFam.getName());
                    LunaticLib.getPlatform().sendConsoleCommand(command);
                }
            }
            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_MOVEOUT_CHILD);
        }

        firstParentFam.unadopt(playerFam);



        return true;
    }
}
