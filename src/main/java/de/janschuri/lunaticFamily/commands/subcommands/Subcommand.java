package de.janschuri.lunaticFamily.commands.subcommands;

import de.janschuri.lunaticFamily.config.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class Subcommand {
    protected final String permission;
    protected final List<String> aliases;
    protected final String name;
    protected final String mainCommand;
    protected List<String> params;
    protected Subcommand[] subcommands;


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

    protected Subcommand(String mainCommand, String name, String permission, Subcommand[] subcommands) {
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
            } else {
                if (Language.checkIsSubcommand(mainCommand, name, args[0])) {
                    if (args[1].equalsIgnoreCase("")) {
                        if (params != null && args.length == 2) {
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
                        if (params != null && args.length == 2) {
                            for (String s : params) {
                                if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
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

    public Component getHelp(CommandSender sender) {
        Component msg = Component.empty();
        if (sender.hasPermission(permission)) {
            if (subcommands != null) {
                msg = Component.text(Language.getMessage(mainCommand + "_" + name + "_help") + "\n")
                        .clickEvent(ClickEvent.runCommand("/lunaticfamily:" + mainCommand + " " + name + " help"))
                        .hoverEvent(HoverEvent.showText(Component.text(Language.getMessage(name + "_help"))))
                        .toBuilder().build();
            } else {
                msg = Component.text(Language.getMessage(mainCommand + "_" + name + "_help") + "\n");
            }
            return msg;
        } else {
          return msg;
        }
    }

    public abstract void execute(CommandSender sender, String[] args);

}