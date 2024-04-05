package de.janschuri.lunaticFamily.commands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.marry.subcommands.*;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MarryCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;
    private final MarryAcceptSubcommand marryAcceptSubcommand = new MarryAcceptSubcommand();
    private final MarryBackpackSubcommand marryBackpackSubcommand = new MarryBackpackSubcommand();
    private final MarryDenySubcommand marryDenySubcommand = new MarryDenySubcommand();
    private final MarryDivorceSubcommand marryDivorceSubcommand = new MarryDivorceSubcommand();
    private final MarryGiftSubcommand marryGiftSubcommand = new MarryGiftSubcommand();
    private final MarryHelpSubcommand marryHelpSubcommand = new MarryHelpSubcommand();
    private final MarryKissSubcommand marryKissSubcommand = new MarryKissSubcommand();
    private final MarryListSubcommand marryListSubcommand = new MarryListSubcommand();
    private final MarryPriestSubcommand marryPriestSubcommand = new MarryPriestSubcommand();
    private final MarryProposeSubcommand marryProposeSubcommand = new MarryProposeSubcommand();
    private final MarrySetSubcommand marrySetSubcommand = new MarrySetSubcommand();
    private final MarryUnsetSubcommand marryUnsetSubcommand = new MarryUnsetSubcommand();
    private final MarryHeartSubcommand marryHeartSubcommand = new MarryHeartSubcommand();

    public MarryCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {


        if (args.length == 0) {
            marryHelpSubcommand.execute(sender, args, plugin);
        } else {
            final String subcommand = args[0];
            if (Language.checkIsSubcommand(label, "set", subcommand)) {
                marrySetSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "unset", subcommand)) {
                marryUnsetSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "propose", subcommand)) {
                marryProposeSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "priest", subcommand)) {
                marryPriestSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "accept", subcommand)) {
                marryAcceptSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "deny", subcommand)) {
                marryDenySubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "divorce", subcommand)) {
                marryDivorceSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "kiss", subcommand)) {
                marryKissSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "gift", subcommand)) {
                marryGiftSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "backpack", subcommand)) {
                marryBackpackSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "heart", subcommand)) {
                marryHeartSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "list", subcommand)) {
                marryListSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "help", subcommand)) {
                marryHelpSubcommand.execute(sender, args, plugin);
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
            if (cmd.getName().equalsIgnoreCase("marry")) {
                list.addAll(marryAcceptSubcommand.tabComplete(sender, args));
                list.addAll(marryBackpackSubcommand.tabComplete(sender, args));
                list.addAll(marryDenySubcommand.tabComplete(sender, args));
                list.addAll(marryDivorceSubcommand.tabComplete(sender, args));
                list.addAll(marryGiftSubcommand.tabComplete(sender, args));
                list.addAll(marryHelpSubcommand.tabComplete(sender, args));
                list.addAll(marryKissSubcommand.tabComplete(sender, args));
                list.addAll(marryListSubcommand.tabComplete(sender, args));
                list.addAll(marryPriestSubcommand.tabComplete(sender, args));
                list.addAll(marryProposeSubcommand.tabComplete(sender, args));
                list.addAll(marrySetSubcommand.tabComplete(sender, args));
                list.addAll(marryUnsetSubcommand.tabComplete(sender, args));
                list.addAll(marryHeartSubcommand.tabComplete(sender, args));
            }
            return list;
        }
        return null;
    }
}

