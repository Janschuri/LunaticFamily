package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;

public class FamilyBackgroundSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "background";
    private static final String PERMISSION = "lunaticfamily.family.background";

    public FamilyBackgroundSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, LunaticFamily.getConfig().getBackgrounds());
    }

    @Override
    public boolean execute(Sender sender, String[] args) {

        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getPrefix() + getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else if (args.length == 0) {
            sender.sendMessage(getPrefix() + getMessage("family_background_help"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(player.getUniqueId());

            playerFam.setBackground(args[0]);
            sender.sendMessage(getPrefix() + getMessage("family_background_set"));
            playerFam.updateFamilyTree();
        }
        return true;
    }
}
