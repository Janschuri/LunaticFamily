package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdoptKickout extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey specifyChildMK = new CommandMessageKey(this,"specify_child");
    private final CommandMessageKey kickoutMK = new CommandMessageKey(this,"kickout");
    private final CommandMessageKey childMK = new CommandMessageKey(this,"child");
    private final CommandMessageKey siblingMK = new CommandMessageKey(this,"sibling");
    private final CommandMessageKey partnerMK = new CommandMessageKey(this,"partner");
    private final CommandMessageKey confirmMK = new CommandMessageKey(this,"confirm");
    private final CommandMessageKey notYourChildMK = new CommandMessageKey(this,"not_your_child");
    private final CommandMessageKey noChildMK = new CommandMessageKey(this,"no_child");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this,"cancel");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "kickout";
    }

    @Override
    public Adopt getParentCommand() {
        return new Adopt();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }


        PlayerSender player = (PlayerSender) sender;
        UUID playerUUID = player.getUniqueId();
        FamilyPlayerImpl playerFam = getFamilyPlayer(playerUUID);

        if (playerFam.getChildren().isEmpty()) {
            sender.sendMessage(getMessage(noChildMK));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(getMessage(specifyChildMK));
            return true;
        }


        String childName = args[0];

        UUID childUUID = PlayerDataTable.getUUID(childName);

        if (childUUID == null) {
            player.sendMessage(getMessage(PLAYER_NOT_EXIST_MK).replaceText(getTextReplacementConfig("%player%", childName)));
            return true;
        }

        PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);
        FamilyPlayerImpl childFam = getFamilyPlayer(childUUID);

        if (!childFam.isChildOf(playerFam.getId())) {
            sender.sendMessage(getMessage(notYourChildMK).replaceText(getTextReplacementConfig("%player%", childFam.getName())));
            return true;
        }

        boolean confirm = false;
        boolean cancel = false;
        boolean force = false;

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("confirm")) {
                confirm = true;
            }
            if (args[1].equalsIgnoreCase("cancel")) {
                cancel = true;
            }
        }

        if (args.length > 2) {
            if (args[2].equalsIgnoreCase("force")) {
                force = true;
            }
        }

        if (cancel) {
            player.sendMessage(getMessage(this.cancelMK));
            return true;
        }

        if (!confirm) {
            Logger.debugLog(child.getName());
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(confirmMK, false)
                            .replaceText(getTextReplacementConfig("%player%", childFam.getName())),
                    getMessage(CONFIRM_MK),
                    "/family adopt kickout " + childName + " confirm",
                    getMessage(CANCEL_MK),
                    "/family adopt kickout " + childName + " cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (!force && playerFam.isMarried() && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, 0.5, WithdrawKey.ADOPT_KICKOUT_CHILD)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        if (!force && !playerFam.isMarried() && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_KICKOUT_PARENT)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), childUUID, WithdrawKey.ADOPT_KICKOUT_CHILD)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK).replaceText(getTextReplacementConfig("%player%", childFam.getName())));
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(TAKE_PAYMENT_CONFIRM_MK, false),
                    getMessage(CONFIRM_MK, false),
                    "/family adopt kickout confirm force",
                    getMessage(CANCEL_MK, false),
                    "/family adopt kickout confirm force"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (!force && playerFam.isMarried()) {
            UUID partnerUUID = playerFam.getPartner().getUniqueId();
            if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.ADOPT_KICKOUT_PARENT)) {
                player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK).replaceText(getTextReplacementConfig("%player%", playerFam.getPartner().getName())));
                player.sendMessage(Utils.getClickableDecisionMessage(
                        getPrefix(),
                        getMessage(TAKE_PAYMENT_CONFIRM_MK, false),
                        getMessage(CONFIRM_MK, false),
                        "/family adopt kickout confirm force",
                        getMessage(CANCEL_MK, false),
                        "/family adopt kickout confirm force"),
                        LunaticFamily.getConfig().decisionAsInvGUI()
                );
                return true;
            }
        }

        player.sendMessage(getMessage(kickoutMK).replaceText(getTextReplacementConfig("%player%", childFam.getName())));

        if (playerFam.isMarried()) {
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUniqueId());
            partner.sendMessage(getMessage(this.partnerMK).replaceText(getTextReplacementConfig("%player1%", playerFam.getName())).replaceText(getTextReplacementConfig("%player2%", childFam.getName())));
        }

        if (childFam.hasSibling()) {
            FamilyPlayerImpl siblingFam = childFam.getSibling();
            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingFam.getUniqueId());
            sibling.sendMessage(getMessage(siblingMK).replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
        }

        child.sendMessage(getMessage(childMK).replaceText(getTextReplacementConfig("%player%", playerFam.getName())));

        if (force) {
            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_KICKOUT_PARENT, WithdrawKey.ADOPT_KICKOUT_CHILD);
        } else {
            if (playerFam.isMarried()) {
                UUID partnerUUID = playerFam.getPartner().getUniqueId();
                Utils.withdrawMoney(player.getServerName(), partnerUUID, 0.5, WithdrawKey.ADOPT_KICKOUT_PARENT);
                Utils.withdrawMoney(player.getServerName(), playerUUID, 0.5, WithdrawKey.ADOPT_KICKOUT_PARENT);

                for (String command : LunaticFamily.getConfig().getSuccessCommands("kickout")) {
                    command = command.replace("%parent1%", playerFam.getName()).replace("%parent2%", playerFam.getPartner().getName()).replace("%child%", childFam.getName());
                    LunaticLib.getPlatform().sendConsoleCommand(command);
                }
            } else {
                Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_KICKOUT_PARENT);

                for (String command : LunaticFamily.getConfig().getSuccessCommands("kickout_single")) {
                    command = command.replace("%parent%", playerFam.getName()).replace("%child%", childFam.getName());
                    LunaticLib.getPlatform().sendConsoleCommand(command);
                }
            }

            Utils.withdrawMoney(player.getServerName(), childUUID, WithdrawKey.ADOPT_KICKOUT_CHILD);
        }

        playerFam.unadopt(childFam.getId());

        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK, false)
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
