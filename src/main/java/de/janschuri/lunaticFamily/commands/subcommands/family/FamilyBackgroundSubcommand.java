package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.senders.CommandSender;
import de.janschuri.lunaticFamily.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
//import org.bukkit.entity.Player;

import java.util.List;

public class FamilyBackgroundSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "background";
    private static final String permission = "lunaticfamily.family.background";
    private static final List<String> params = PluginConfig.backgrounds;

    public FamilyBackgroundSubcommand() {
        super(mainCommand, name, permission, params);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else if (args.length == 1) {
            sender.sendMessage(Language.prefix + Language.getMessage("family_background_help"));
        } else {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            FamilyPlayer playerFam = new FamilyPlayer(player.getUniqueId().toString());

            if (args.length > 1) {
                playerFam.setBackground(args[1]);
                sender.sendMessage(Language.prefix + Language.getMessage("family_background_set"));
                playerFam.updateFamilyTree();
            } else {
                sender.sendMessage(Language.prefix + Language.getMessage("family_background_help"));
            }
        }
        return true;
    }
}
