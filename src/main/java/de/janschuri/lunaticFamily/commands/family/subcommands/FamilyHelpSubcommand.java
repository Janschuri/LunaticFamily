package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.commands.adopt.subcommands.*;
import de.janschuri.lunaticFamily.config.Language;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FamilyHelpSubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.adopt";
    private static final List<String> aliases = Language.getAliases("family", "list");

    public FamilyHelpSubcommand() {
        super(permission, aliases);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        List<String> subcommandsHelp = new ArrayList<>();
        if (sender.hasPermission("lunaticFamily.family.background") && sender instanceof Player) {
            subcommandsHelp.add("background");
        }
        if (sender.hasPermission("lunaticFamily.family.list") && sender instanceof Player) {
            subcommandsHelp.add("list");
        }
        if (sender.hasPermission("lunaticFamily.family.list.others")) {
            subcommandsHelp.add("list_others");
        }

        TextComponent msg = new TextComponent(Language.getMessage("family_help") + "\n");

        for (String subcommand : subcommandsHelp) {
            msg.addExtra(Language.prefix + " " + Language.getMessage("family_" + subcommand + "_help") + "\n");
        }

        List<String> commandsHelp = new ArrayList<>();
        if (sender.hasPermission("lunaticFamily.adopt")) {
            commandsHelp.add("adopt");
        }
        if (sender.hasPermission("lunaticFamily.marry")) {
            commandsHelp.add("marry");
        }
        if (sender.hasPermission("lunaticFamily.gender")) {
            commandsHelp.add("gender");
        }
        if (sender.hasPermission("lunaticFamily.sibling")) {
            commandsHelp.add("sibling");
        }

        for (String commandHelp : commandsHelp) {
            TextComponent text = new TextComponent(Language.prefix + " " + Language.getMessage("family_" + commandHelp + "_help") + "\n");
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandHelp + " help"));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Language.getMessage(commandHelp + "_help")).create()));
            msg.addExtra(text);
        }

        sender.sendMessage(msg);

    }
}
