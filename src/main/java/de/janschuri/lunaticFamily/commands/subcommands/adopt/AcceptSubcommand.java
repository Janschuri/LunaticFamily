package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.UUID;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;

public class AcceptSubcommand extends Subcommand{
    private static final String permission = "lunaticfamily.adopt";
    private static final String mainCommand = "adopt";
    private static final String name = "accept";

    public AcceptSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_accept_no_request"));
            } else {

                UUID parentUUID = UUID.fromString(LunaticFamily.adoptRequests.get(playerUUID));
                FamilyPlayer parentFam = new FamilyPlayer(parentUUID);
                PlayerCommandSender parent = player.getPlayerCommandSender(parentUUID);

                if (parentFam.getChildrenAmount() > 1) {
                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_accept_parent_limit").replace("%player%", parentFam.getName()));
                } else if (!player.hasEnoughMoney("adopt_child")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                } else if (!parent.hasEnoughMoney("adopt_parent")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", parentFam.getName()));
                } else {

                    if (parentFam.isMarried()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("adopt_accept_got_adopted").replace("%player1%", parentFam.getName()).replace("%player2%", parentFam.getPartner().getName()));
                        parent.sendMessage(Language.prefix + Language.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                        PlayerCommandSender secondParent = player.getPlayerCommandSender(parentFam.getPartner().getUniqueId());
                        secondParent.withdrawMoney(0.5, "adopt_parent");
                        parent.withdrawMoney(0.5, "adopt_parent");
                        for (String command : PluginConfig.successCommands.get("adopt")) {
                            command = command.replace("%parent1%", parentFam.getName()).replace("%parent2%", parentFam.getPartner().getName()).replace("%child%", playerFam.getName());

                            Utils.getUtils().sendConsoleCommand(command);
                        }
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("adopt_accept_adopted_by_single").replace("%player%", parentFam.getName()));
                        for (String command : PluginConfig.successCommands.get("adopt_single")) {
                            command = command.replace("%parent%", parentFam.getName()).replace("%child%", playerFam.getName());
                            Utils.getUtils().sendConsoleCommand(command);
                        }
                    }
                    player.withdrawMoney("adopt_child");

                    parent.sendMessage(Language.prefix + Language.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                    LunaticFamily.adoptRequests.remove(playerUUID);
                    parentFam.adopt(playerFam.getID());
                }
            }
        }
        return true;
    }
}