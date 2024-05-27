package de.janschuri.lunaticfamily.common.commands.gender;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class GenderInfoSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "gender";
    private static final String NAME = "info";
    private static final String PERMISSION = "lunaticfamily.gender";

    public GenderInfoSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof PlayerSender)) {
                sender.sendMessage(getPrefix() + getMessage("no_console_command"));
                return true;
            }

            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);
            sender.sendMessage( getPrefix() + getMessage("gender_info").replace("%gender%", getGenderLang(playerFam.getGender())));
            return true;
        }

        String playerArg = args[0];
        UUID playerUUID;

        if (Utils.isUUID(playerArg)) {
            playerUUID = UUID.fromString(playerArg);

            if (PlayerDataTable.getID(playerUUID) < 0) {
                sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", playerArg));
                return true;
            }
        } else {
            playerUUID = PlayerDataTable.getUUID(playerArg);

            if (playerUUID == null) {
                sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", playerArg));
                return true;
            }
        }

        FamilyPlayerImpl player = new FamilyPlayerImpl(playerUUID);

        sender.sendMessage(getPrefix() + getMessage("gender_info_others").replace("%player%", player.getName()).replace("%gender%", getGenderLang(player.getGender())));
        return true;
    }
}
