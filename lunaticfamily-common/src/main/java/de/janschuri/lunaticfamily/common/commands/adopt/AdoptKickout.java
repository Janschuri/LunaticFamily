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

    private static final AdoptKickout INSTANCE = new AdoptKickout();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE,"help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Kick a child out of your family.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &7- Kick ein Kind aus deiner Familie.");
    private static final CommandMessageKey SPECIFY_CHILD_MK = new LunaticCommandMessageKey(INSTANCE,"specify_child")
            .defaultMessage("en", "Please specify a child.")
            .defaultMessage("de", "Bitte gib ein Kind an.");
    private static final CommandMessageKey KICKOUT_MK = new LunaticCommandMessageKey(INSTANCE,"kickout")
            .defaultMessage("en", "You kicked %player% out of your family.")
            .defaultMessage("de", "Du hast %player% aus deiner Familie geworfen.");
    private static final CommandMessageKey CHILD_MK = new LunaticCommandMessageKey(INSTANCE,"child")
            .defaultMessage("en", "%player% kicked you out of their family.")
            .defaultMessage("de", "%player% hat dich aus ihrer Familie geworfen.");
    private static final CommandMessageKey SIBLING_MK = new LunaticCommandMessageKey(INSTANCE,"sibling")
            .defaultMessage("en", "%player% kicked your sibling out of their family.")
            .defaultMessage("de", "%player% hat dein Geschwisterkind aus ihrer Familie geworfen.");
    private static final CommandMessageKey PARTNER_MK = new LunaticCommandMessageKey(INSTANCE,"partner")
            .defaultMessage("en", "%player1% kicked %player2% out of their family.")
            .defaultMessage("de", "%player1% hat %player2% aus ihrer Familie geworfen.");
    private static final CommandMessageKey CONFIRM_MK = new LunaticCommandMessageKey(INSTANCE,"confirm")
            .defaultMessage("en", "Please confirm, that you want to kick %player% out of your family.")
            .defaultMessage("de", "Bitte bestätige, dass du %player% aus deiner Familie werfen möchtest.");
    private static final CommandMessageKey NOT_YOUR_CHILD_MK = new LunaticCommandMessageKey(INSTANCE,"not_your_child")
            .defaultMessage("en", "%player% is not your child.")
            .defaultMessage("de", "%player% ist nicht dein Kind.");
    private static final CommandMessageKey NO_CHILD_MK = new LunaticCommandMessageKey(INSTANCE,"no_child")
            .defaultMessage("en", "You have no children.")
            .defaultMessage("de", "Du hast keine Kinder.");
    private static final CommandMessageKey CANCEL_MK = new LunaticCommandMessageKey(INSTANCE,"cancel")
            .defaultMessage("en", "You haven't kicked %player% out of your family.")
            .defaultMessage("de", "Du hast %player% nicht aus deiner Familie geworfen.");


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
            sender.sendMessage(getMessage(NO_CHILD_MK));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(getMessage(SPECIFY_CHILD_MK));
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
            sender.sendMessage(getMessage(NOT_YOUR_CHILD_MK,
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
            player.sendMessage(getMessage(this.CANCEL_MK));
            return true;
        }

        if (!confirm) {
            Logger.debugLog(child.getName());
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(CONFIRM_MK.noPrefix(),
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

        player.sendMessage(getMessage(KICKOUT_MK,
                placeholder("%player%", childFam.getName())));

        if (playerFam.isMarried()) {
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUUID());
            partner.sendMessage(getMessage(this.PARTNER_MK,
                placeholder("%player1%", playerFam.getName()),
                placeholder("%player2%", childFam.getName())));
        }

        if (childFam.hasSiblings()) {
            FamilyPlayer siblingFam = childFam.getSibling();
            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingFam.getUUID());
            sibling.sendMessage(getMessage(SIBLING_MK,
                placeholder("%player%", playerFam.getName())));
        }

        child.sendMessage(getMessage(CHILD_MK,
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
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
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
