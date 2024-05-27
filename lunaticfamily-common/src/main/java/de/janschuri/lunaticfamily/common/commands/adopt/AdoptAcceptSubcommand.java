package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class AdoptAcceptSubcommand extends Subcommand {
    private static final String PERMISSION = "lunaticfamily.adopt";
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "accept";

    public AdoptAcceptSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(Sender sender, String[] args) {

        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getPrefix() + getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                sender.sendMessage(getPrefix() + getMessage("adopt_accept_no_request"));
            } else {

                UUID parent1UUID = LunaticFamily.adoptRequests.get(playerUUID);
                FamilyPlayerImpl parentFam = new FamilyPlayerImpl(parent1UUID);
                PlayerSender parent = LunaticLib.getPlatform().getPlayerSender(parent1UUID);

                if (parentFam.getChildrenAmount() > 1) {
                    sender.sendMessage(getPrefix() + getMessage("adopt_accept_parent_limit").replace("%player%", parentFam.getName()));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_child")) {
                    sender.sendMessage(getPrefix() + getMessage("not_enough_money"));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), parent1UUID, "adopt_parent")) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", parentFam.getName()));
                } else {

                    if (parentFam.isMarried()) {
                        sender.sendMessage(getPrefix() + getMessage("adopt_accept_got_adopted").replace("%player1%", parentFam.getName()).replace("%player2%", parentFam.getPartner().getName()));
                        parent.sendMessage(getPrefix() + getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                        UUID parent2UUID = parentFam.getPartner().getUniqueId();
                        Utils.withdrawMoney(player.getServerName(), parent2UUID, 0.5, "adopt_parent");
                        Utils.withdrawMoney(player.getServerName(), parent1UUID, 0.5, "adopt_parent");
                        for (String command : LunaticFamily.getConfig().getSuccessCommands("adopt")) {
                            command = command.replace("%parent1%", parentFam.getName()).replace("%parent2%", parentFam.getPartner().getName()).replace("%child%", playerFam.getName());

                            LunaticLib.getPlatform().sendConsoleCommand(command);
                        }
                    } else {
                        sender.sendMessage(getPrefix() + getMessage("adopt_accept_adopted_by_single").replace("%player%", parentFam.getName()));
                        for (String command : LunaticFamily.getConfig().getSuccessCommands("adopt_single")) {
                            command = command.replace("%parent%", parentFam.getName()).replace("%child%", playerFam.getName());
                            LunaticLib.getPlatform().sendConsoleCommand(command);
                        }
                    }
                    Utils.withdrawMoney(player.getServerName(), playerUUID, "adopt_child");

                    parent.sendMessage(getPrefix() + getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                    LunaticFamily.adoptRequests.remove(playerUUID);
                    parentFam.adopt(playerFam.getId());
                }
            }
        }
        return true;
    }
}