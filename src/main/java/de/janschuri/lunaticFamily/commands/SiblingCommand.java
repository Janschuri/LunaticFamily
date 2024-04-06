package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.family.SiblingSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SiblingCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;

    private final SiblingSubcommand familySiblingSubcommand = new SiblingSubcommand();
//    private final SiblingAcceptSubcommand siblingAcceptSubcommand = new SiblingAcceptSubcommand();
//    private final SiblingDenySubcommand siblingDenySubcommand = new SiblingDenySubcommand();
//    private final SiblingHelpSubcommand siblingHelpSubcommand = new SiblingHelpSubcommand();
//    private final SiblingProposeSubcommand siblingProposeSubcommand = new SiblingProposeSubcommand();
//    private final SiblingSetSubcommand siblingSetSubcommand = new SiblingSetSubcommand();
//    private final SiblingUnsetSubcommand siblingUnsetSubcommand = new SiblingUnsetSubcommand();
//    private final SiblingUnsiblingSubcommand siblingUnsiblingSubcommand = new SiblingUnsiblingSubcommand();


    public SiblingCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        familySiblingSubcommand.execute(sender, args, plugin);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "sibling";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return new ArrayList<>(familySiblingSubcommand.tabComplete(sender, newArgs));
    }
}