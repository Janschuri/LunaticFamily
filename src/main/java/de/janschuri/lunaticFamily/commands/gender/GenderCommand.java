package de.janschuri.lunaticFamily.commands.gender;

import de.janschuri.lunaticFamily.commands.gender.subcommands.GenderHelpSubcommand;
import de.janschuri.lunaticFamily.commands.gender.subcommands.GenderInfoSubcommand;
import de.janschuri.lunaticFamily.commands.gender.subcommands.GenderSetSubcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.LunaticFamily;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GenderCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;
    private final GenderHelpSubcommand genderHelpSubcommand = new GenderHelpSubcommand();
    private final GenderInfoSubcommand genderInfoSubcommand = new GenderInfoSubcommand();
    private final GenderSetSubcommand genderSetSubcommand = new GenderSetSubcommand();

    public GenderCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            genderHelpSubcommand.execute(sender, args, plugin);
        } else {
            final String subcommand = args[0];
            if (Language.checkIsSubcommand(label, "set", subcommand)) {
                genderSetSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "info", subcommand)) {
                genderInfoSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand(label, "help", subcommand)) {
                genderHelpSubcommand.execute(sender, args, plugin);
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
                list.addAll(genderHelpSubcommand.tabComplete(sender, args));
                list.addAll(genderInfoSubcommand.tabComplete(sender, args));
                list.addAll(genderSetSubcommand.tabComplete(sender, args));
            }
            return list;
        }
        return null;
    }
}
