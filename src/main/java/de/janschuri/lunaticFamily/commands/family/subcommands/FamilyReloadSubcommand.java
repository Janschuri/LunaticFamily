package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FamilyReloadSubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.admin.reload";
    private static final List<String> aliases = Language.getAliases("family", "reload");

    public FamilyReloadSubcommand() {
        super(permission, aliases);
    }
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            plugin.loadConfig(plugin);
            sender.sendMessage(Language.prefix + Language.getMessage("admin_reload"));
        }
    }
}
