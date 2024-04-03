package de.janschuri.lunaticFamily.commands.family;

import de.janschuri.lunaticFamily.commands.family.subcommands.*;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.LunaticFamily;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FamilyCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;
    private final FamilyListSubcommand familyListSubcommand = new FamilyListSubcommand();
    private final FamilyAdoptSubcommand familyAdoptSubcommand = new FamilyAdoptSubcommand();
    private final FamilyGenderSubcommand familyGenderSubcommand = new FamilyGenderSubcommand();
    private final FamilySiblingSubcommand familySiblingSubcommand = new FamilySiblingSubcommand();
    private final FamilyBackgroundSubcommand familyBackgroundSubcommand = new FamilyBackgroundSubcommand();
    private final FamilyHelpSubcommand familyHelpSubcommand = new FamilyHelpSubcommand();
    private final FamilyMarrySubcommand familyMarrySubcommand = new FamilyMarrySubcommand();
    private final FamilyReloadSubcommand familyReloadSubcommand = new FamilyReloadSubcommand();


    public FamilyCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            familyHelpSubcommand.execute(sender, args, plugin);
        } else {
            final String subcommand = args[0];
            if (Language.checkIsSubcommand("family", "gender", subcommand)) {
                familyGenderSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand("family", "adopt", subcommand)) {
                familyAdoptSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand("family", "marry", subcommand)) {
                familyMarrySubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand("family", "sibling", subcommand)) {
                familySiblingSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand("family", "reload", subcommand)) {
                familyReloadSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand("family", "list", subcommand)) {
                familyListSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand("family", "background", subcommand)) {
                familyBackgroundSubcommand.execute(sender, args, plugin);
            } else if (Language.checkIsSubcommand("family", "help", subcommand)) {
                familyHelpSubcommand.execute(sender, args, plugin);
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
            if (cmd.getName().equalsIgnoreCase("family")) {
                list.addAll(familyReloadSubcommand.tabComplete(sender, args));
                list.addAll(familyAdoptSubcommand.tabComplete(sender, args));
                list.addAll(familyGenderSubcommand.tabComplete(sender, args));
                list.addAll(familyMarrySubcommand.tabComplete(sender, args));
                list.addAll(familySiblingSubcommand.tabComplete(sender, args));
                list.addAll(familyListSubcommand.tabComplete(sender, args));
                list.addAll(familyBackgroundSubcommand.tabComplete(sender, args));
                list.addAll(familyHelpSubcommand.tabComplete(sender, args));
            }
            return list;
        }
        return null;
    }
}

