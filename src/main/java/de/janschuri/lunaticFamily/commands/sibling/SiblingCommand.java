package de.janschuri.lunaticFamily.commands.sibling;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.sibling.subcommands.*;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SiblingCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;
    private final SiblingAcceptSubcommand siblingAcceptSubcommand = new SiblingAcceptSubcommand();
    private final SiblingDenySubcommand siblingDenySubcommand = new SiblingDenySubcommand();
    private final SiblingHelpSubcommand siblingHelpSubcommand = new SiblingHelpSubcommand();
    private final SiblingProposeSubcommand siblingProposeSubcommand = new SiblingProposeSubcommand();
    private final SiblingSetSubcommand siblingSetSubcommand = new SiblingSetSubcommand();
    private final SiblingUnsetSubcommand siblingUnsetSubcommand = new SiblingUnsetSubcommand();
    private final SiblingUnsiblingSubcommand siblingUnsiblingSubcommand = new SiblingUnsiblingSubcommand();


    public SiblingCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            siblingHelpSubcommand.execute(sender, args, plugin);
        } else {
            final String subcommand = args[0];
            if (checkIsSubcommand("set", subcommand)) {
                siblingSetSubcommand.execute(sender, args, plugin);
            } else if (checkIsSubcommand("unset", subcommand)) {
                siblingUnsetSubcommand.execute(sender, args, plugin);
            } else if (checkIsSubcommand("propose", subcommand)) {
                siblingProposeSubcommand.execute(sender, args, plugin);
            } else if (checkIsSubcommand("accept", subcommand)) {
                siblingAcceptSubcommand.execute(sender, args, plugin);
            } else if (checkIsSubcommand("deny", subcommand)) {
                siblingDenySubcommand.execute(sender, args, plugin);
            } else if (checkIsSubcommand("unsibling", subcommand)) {
                siblingUnsiblingSubcommand.execute(sender, args, plugin);
            } else if (checkIsSubcommand("help", subcommand)) {
                siblingHelpSubcommand.execute(sender, args, plugin);
            } else {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            }
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("sibling")) {
                list.addAll(siblingAcceptSubcommand.tabComplete(sender, args));
                list.addAll(siblingDenySubcommand.tabComplete(sender, args));
                list.addAll(siblingProposeSubcommand.tabComplete(sender, args));
                list.addAll(siblingSetSubcommand.tabComplete(sender, args));
                list.addAll(siblingUnsetSubcommand.tabComplete(sender, args));
                list.addAll(siblingUnsiblingSubcommand.tabComplete(sender, args));
            }
            return list;
        }
        return null;
    }

    private boolean checkIsSubcommand(final String subcommand, final String arg) {
        return subcommand.equalsIgnoreCase(arg) || Language.getAliases("sibling", subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }
}