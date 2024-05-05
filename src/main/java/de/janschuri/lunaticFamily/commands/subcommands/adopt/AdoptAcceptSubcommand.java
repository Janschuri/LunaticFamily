package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class AdoptAcceptSubcommand extends Subcommand {
    private static final String PERMISSION = "lunaticfamily.adopt";
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "accept";

    public AdoptAcceptSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {

        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_accept_no_request"));
            } else {

                UUID parent1UUID = LunaticFamily.adoptRequests.get(playerUUID);
                FamilyPlayer parentFam = new FamilyPlayer(parent1UUID);
                AbstractPlayerSender parent = AbstractSender.getPlayerSender(parent1UUID);

                if (parentFam.getChildrenAmount() > 1) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("adopt_accept_parent_limit").replace("%player%", parentFam.getName()));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_child")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), parent1UUID, "adopt_parent")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", parentFam.getName()));
                } else {

                    if (parentFam.isMarried()) {
                        sender.sendMessage(language.getPrefix() + language.getMessage("adopt_accept_got_adopted").replace("%player1%", parentFam.getName()).replace("%player2%", parentFam.getPartner().getName()));
                        parent.sendMessage(language.getPrefix() + language.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                        UUID parent2UUID = parentFam.getPartner().getUniqueId();
                        Utils.withdrawMoney(player.getServerName(), parent2UUID, 0.5, "adopt_parent");
                        Utils.withdrawMoney(player.getServerName(), parent1UUID, 0.5, "adopt_parent");
                        for (String command : PluginConfig.getSuccessCommands("adopt")) {
                            command = command.replace("%parent1%", parentFam.getName()).replace("%parent2%", parentFam.getPartner().getName()).replace("%child%", playerFam.getName());

                            Utils.sendConsoleCommand(command);
                        }
                    } else {
                        sender.sendMessage(language.getPrefix() + language.getMessage("adopt_accept_adopted_by_single").replace("%player%", parentFam.getName()));
                        for (String command : PluginConfig.getSuccessCommands("adopt_single")) {
                            command = command.replace("%parent%", parentFam.getName()).replace("%child%", playerFam.getName());
                            Utils.sendConsoleCommand(command);
                        }
                    }
                    Utils.withdrawMoney(player.getServerName(), playerUUID, "adopt_child");

                    parent.sendMessage(language.getPrefix() + language.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                    LunaticFamily.adoptRequests.remove(playerUUID);
                    parentFam.adopt(playerFam.getID());
                }
            }
        }
        return true;
    }
}