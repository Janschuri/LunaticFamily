package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

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
    public boolean execute(AbstractSender sender, String[] args) {

        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else if (args.length == 1) {
            sender.sendMessage(language.getPrefix() + language.getMessage("family_background_help"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            FamilyPlayer playerFam = new FamilyPlayer(player.getUniqueId().toString());

            if (args.length > 1) {
                playerFam.setBackground(args[1]);
                sender.sendMessage(language.getPrefix() + language.getMessage("family_background_set"));
                playerFam.updateFamilyTree();
            } else {
                sender.sendMessage(language.getPrefix() + language.getMessage("family_background_help"));
            }
        }
        return true;
    }
}
