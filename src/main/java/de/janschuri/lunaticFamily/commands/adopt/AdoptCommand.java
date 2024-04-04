package de.janschuri.lunaticFamily.commands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.adopt.subcommands.*;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdoptCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;
    private final AdoptAcceptSubcommand adoptAcceptSubcommand = new AdoptAcceptSubcommand();
    private final AdoptDenySubcommand adoptDenySubcommand = new AdoptDenySubcommand();
    private final AdoptHelpSubcommand adoptHelpSubcommand = new AdoptHelpSubcommand();
    private final AdoptKickoutSubcommand adoptKickoutSubcommand = new AdoptKickoutSubcommand();
    private final AdoptMoveoutSubcommand adoptMoveoutSubcommand = new AdoptMoveoutSubcommand();
    private final AdoptProposeSubcommand adoptProposeSubcommand = new AdoptProposeSubcommand();
    private final AdoptSetSubcommand adoptSetSubcommand = new AdoptSetSubcommand();
    private final AdoptUnsetSubcommand adoptUnsetSubcommand = new AdoptUnsetSubcommand();

    public AdoptCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            adoptHelpSubcommand.execute(sender, args, plugin);
        } else {
            final String subcommand = args[0];
            if (Language.checkIsSubcommand(label, "set", subcommand)) {
                adoptSetSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "unset", subcommand)) {
                adoptUnsetSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "propose", subcommand)) {
                adoptProposeSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "accept", subcommand)) {
                adoptAcceptSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "deny", subcommand)) {
                adoptDenySubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "moveout", subcommand)) {
                adoptMoveoutSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "kickout", subcommand)) {
                adoptKickoutSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "help", subcommand)) {
                adoptHelpSubcommand.execute(sender, args, plugin);
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
            if (cmd.getName().equalsIgnoreCase("adopt")) {
                list.addAll(adoptAcceptSubcommand.tabComplete(sender, args));
                list.addAll(adoptDenySubcommand.tabComplete(sender, args));
                list.addAll(adoptHelpSubcommand.tabComplete(sender, args));
                list.addAll(adoptKickoutSubcommand.tabComplete(sender, args));
                list.addAll(adoptMoveoutSubcommand.tabComplete(sender, args));
                list.addAll(adoptProposeSubcommand.tabComplete(sender, args));
                list.addAll(adoptSetSubcommand.tabComplete(sender, args));
                list.addAll(adoptUnsetSubcommand.tabComplete(sender, args));
            }
            return list;
        }
        return null;
    }
}
