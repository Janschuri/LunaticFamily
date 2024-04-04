package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class Subcommand {
    protected final String permission;
    protected final List<String> aliases;
    protected final String name;
    protected final String mainCommand;
    protected List<String> params = null;
    protected Subcommand[] subcommands = null;


    protected Subcommand(String mainCommand, String name, String permission) {
        this.mainCommand = mainCommand;
        this.name = name;
        this.permission = permission;
        this.aliases = Language.getAliases(mainCommand, name);
    }

    protected Subcommand(String mainCommand, String name, String permission, List<String> params) {
        this.mainCommand = mainCommand;
        this.name = name;
        this.permission = permission;
        this.aliases = Language.getAliases(mainCommand, name);
        this.params = params;
    }

    protected Subcommand(String mainCommand, String name, String permission, Subcommand... subcommands) {
        this.mainCommand = mainCommand;
        this.name = name;
        this.permission = permission;
        this.aliases = Language.getAliases(mainCommand, name);
        this.subcommands = subcommands;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (sender.hasPermission(permission)) {
            if (args.length == 0) {
                list.addAll(aliases);
            } else if (args.length == 1) {
                for (String s : aliases) {
                    if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                        list.add(s);
                    }
                }
            } else if (args.length == 2) {
                if (Language.checkIsSubcommand(mainCommand, name, args[0])) {
                    if (args[1].equalsIgnoreCase("")) {
                        if (params != null) {
                            list.addAll(params);
                        }
                        if (subcommands != null) {
                            String[] newArgs = new String[args.length - 1];
                            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                            for (Subcommand subcommand : subcommands) {
                                list.addAll(subcommand.tabComplete(sender, newArgs));
                            }
                        }
                    } else {
                        if (params != null) {
                            for (String s : params) {
                                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                    list.add(s);
                                }
                            }
                        }
                        if (subcommands != null) {
                            String[] newArgs = new String[args.length - 1];
                            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                            for (Subcommand subcommand : subcommands) {
                                list.addAll(subcommand.tabComplete(sender, newArgs));
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    public TextComponent getHelp(CommandSender sender) {
        if (sender.hasPermission(permission)) {
            TextComponent text = new net.md_5.bungee.api.chat.TextComponent(Language.getMessage(mainCommand + "_" + name + "_help") + "\n");
            if (subcommands != null) {
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + name + " help"));
                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Language.getMessage(name + "_help")).create()));
            }
            return text;
        } else {
            return new TextComponent("");
        }
    }
    public abstract void execute(CommandSender sender, String[] args, LunaticFamily plugin);

}
