package de.janschuri.lunaticfamily.commands.subcommands.family;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.config.PluginConfig;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class FamilyBackgroundSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "background";
    private static final String PERMISSION = "lunaticfamily.family.background";

    public FamilyBackgroundSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, PluginConfig.getBackgrounds());
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {

        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else if (args.length == 0) {
            sender.sendMessage(language.getPrefix() + language.getMessage("family_background_help"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            FamilyPlayer playerFam = new FamilyPlayer(player.getUniqueId().toString());

            playerFam.setBackground(args[0]);
            sender.sendMessage(language.getPrefix() + language.getMessage("family_background_set"));
            playerFam.updateFamilyTree();
        }
        return true;
    }
}
