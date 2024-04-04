package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FamilyBackgroundSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "background";
    private static final String permission = "lunaticfamily.family.background";
    private static final List<String> params = Config.backgrounds;

    public FamilyBackgroundSubcommand() {
        super(mainCommand, name, permission, params);
    }

    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else if (args.length == 1) {
            sender.sendMessage(Language.prefix + Language.getMessage("family_background_help"));
        } else {
            Player player = (Player) sender;
            FamilyPlayer playerFam = new FamilyPlayer(player.getUniqueId().toString());

            if (args.length > 1) {
                playerFam.setBackground(args[1]);
                sender.sendMessage(Language.prefix + Language.getMessage("family_background_set"));
            } else {
                sender.sendMessage(Language.prefix + Language.getMessage("family_background_help"));
            }
        }
    }
}