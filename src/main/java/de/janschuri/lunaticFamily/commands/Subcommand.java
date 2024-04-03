package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.LunaticFamily;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class Subcommand {
    protected final String permission;
    protected final List<String> aliases;
    protected List<String> params = null;
    protected Subcommand[] subcommands = null;


    protected Subcommand(String permission, List<String> aliases) {
        this.permission = permission;
        this.aliases = aliases;
    }

    protected Subcommand(String permission, List<String> aliases, List<String> params) {
        this.permission = permission;
        this.aliases = aliases;
        this.params = params;
    }

    protected Subcommand(String permission, List<String> aliases, Subcommand... subcommands) {
        this.permission = permission;
        this.aliases = aliases;
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
        return list;
    }
    public abstract void execute(CommandSender sender, String[] args, LunaticFamily plugin);

}
