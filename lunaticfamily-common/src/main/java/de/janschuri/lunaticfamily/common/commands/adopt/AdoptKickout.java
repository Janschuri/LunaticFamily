package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdoptKickout extends FamilyCommand implements HasParentCommand, HasParams {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey specifyChildMK = new LunaticCommandMessageKey(this,"specify_child");
    private final CommandMessageKey kickoutMK = new LunaticCommandMessageKey(this,"kickout");
    private final CommandMessageKey childMK = new LunaticCommandMessageKey(this,"child");
    private final CommandMessageKey siblingMK = new LunaticCommandMessageKey(this,"sibling");
    private final CommandMessageKey partnerMK = new LunaticCommandMessageKey(this,"partner");
    private final CommandMessageKey confirmMK = new LunaticCommandMessageKey(this,"confirm");
    private final CommandMessageKey notYourChildMK = new LunaticCommandMessageKey(this,"not_your_child");
    private final CommandMessageKey noChildMK = new LunaticCommandMessageKey(this,"no_child");
    private final CommandMessageKey cancelMK = new LunaticCommandMessageKey(this,"cancel");


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

        if (playerFam.getChildren().isEmpty()) {
            sender.sendMessage(getMessage(noChildMK));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(getMessage(specifyChildMK));
            return true;
        }


        String childName = args[0];

        UUID childUUID = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", childName).findOne().getUUID();

        if (childUUID == null) {
            player.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", childName)));
            return true;
        }

        PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);
        FamilyPlayer childFam = getFamilyPlayer(childUUID);

        if (childFam.isNotChildOf(playerFam)) {
            sender.sendMessage(getMessage(notYourChildMK,
                placeholder("%player%", childFam.getName())));
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
                    getMessage(confirmMK.noPrefix(),
                placeholder("%player%", childFam.getName())),
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
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", childFam.getName())));
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(TAKE_PAYMENT_CONFIRM_MK.noPrefix()),
                    getMessage(CONFIRM_MK.noPrefix()),
                    "/family adopt kickout confirm force",
                    getMessage(CANCEL_MK.noPrefix()),
                    "/family adopt kickout confirm force"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (!force && playerFam.isMarried()) {
            UUID partnerUUID = playerFam.getPartner().getUUID();
            if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.ADOPT_KICKOUT_PARENT)) {
                player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", playerFam.getPartner().getName())));
                player.sendMessage(Utils.getClickableDecisionMessage(
                        getPrefix(),
                        getMessage(TAKE_PAYMENT_CONFIRM_MK.noPrefix()),
                        getMessage(CONFIRM_MK.noPrefix()),
                        "/family adopt kickout confirm force",
                        getMessage(CANCEL_MK.noPrefix()),
                        "/family adopt kickout confirm force"),
                        LunaticFamily.getConfig().decisionAsInvGUI()
                );
                return true;
            }
        }

        player.sendMessage(getMessage(kickoutMK,
                placeholder("%player%", childFam.getName())));

        if (playerFam.isMarried()) {
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUUID());
            partner.sendMessage(getMessage(this.partnerMK,
                placeholder("%player1%", playerFam.getName()),
                placeholder("%player2%", childFam.getName())));
        }

        if (childFam.hasSiblings()) {
            FamilyPlayer siblingFam = childFam.getSibling();
            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingFam.getUUID());
            sibling.sendMessage(getMessage(siblingMK,
                placeholder("%player%", playerFam.getName())));
        }

        child.sendMessage(getMessage(childMK,
                placeholder("%player%", playerFam.getName())));

        if (force) {
            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_KICKOUT_PARENT, WithdrawKey.ADOPT_KICKOUT_CHILD);
        } else {
            if (playerFam.isMarried()) {
                UUID partnerUUID = playerFam.getPartner().getUUID();
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

        playerFam.unadopt(childFam);

        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK.noPrefix())
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
