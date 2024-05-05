package de.janschuri.lunaticFamily.commands.subcommands.gender;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class GenderInfoSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "gender";
    private static final String NAME = "info";
    private static final String PERMISSION = "lunaticfamily.gender";

    public GenderInfoSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof AbstractPlayerSender)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
                return true;
            }
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
            sender.sendMessage( language.getPrefix() + language.getMessage("gender_info").replace("%gender%", Language.getGenderLang(playerFam.getGender())));
            return true;
        }
        AbstractPlayerSender player1 = (AbstractPlayerSender) sender;
        if (!player1.exists()) {
            sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
            return true;
        }
        sender.sendMessage(language.getPrefix() + language.getMessage("gender_info_others").replace("%player%", player1.getName()).replace("%gender%", Language.getGenderLang(args[0])));
        return true;
    }
}
