package de.janschuri.lunaticfamily.common.commands.gender;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GenderInfo extends FamilyCommand implements HasParentCommand, HasParams {

    private static final GenderInfo INSTANCE = new GenderInfo();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Show your gender.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Zeige dein Geschlecht.");
    private static final CommandMessageKey HELP_OTHERS_MK = new LunaticCommandMessageKey(INSTANCE, "help_others")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Show the gender of a player.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &7- Zeige das Geschlecht eines Spielers.");
    private static final CommandMessageKey INFO_MK = new LunaticCommandMessageKey(INSTANCE, "info")
            .defaultMessage("en", "Your gender is %gender%.")
            .defaultMessage("de", "Dein Geschlecht ist %gender%.");
    private static final CommandMessageKey INFO_OTHERS_MK = new LunaticCommandMessageKey(INSTANCE, "info_others")
            .defaultMessage("en", "%player%'s gender is %gender%.")
            .defaultMessage("de", "Das Geschlecht von %player% ist %gender%.");


    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission(),
                HELP_OTHERS_MK, getPermission() + ".others"
        );
    }

    @Override
    public String getPermission() {
        return "lunaticfamily.gender";
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public Gender getParentCommand() {
        return new Gender();
    }

    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof PlayerSender player)) {
                sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
                return true;
            }

            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = FamilyPlayer.find(playerUUID);
            sender.sendMessage(getMessage(INFO_MK,
                placeholder("%gender%", getGenderLang(playerFam.getGender()))));
            return true;
        }

        if (!sender.hasPermission(getPermission()+".others")) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        String playerArg = args[0];
        UUID playerUUID;


        FamilyPlayer playerFam;

        if (Utils.isUUID(playerArg)) {
            playerUUID = UUID.fromString(playerArg);
            playerFam = FamilyPlayer.find(playerUUID);
        } else {
            playerFam = FamilyPlayer.find(playerArg);
        }

        if (playerFam == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                    placeholder("%player%", playerArg)));
            return true;
        }

        sender.sendMessage(getMessage(INFO_OTHERS_MK,
                placeholder("%player%", playerFam.getName()),
                placeholder("%gender%", getGenderLang(playerFam.getGender()))));
        return true;
    }

    @Override
    public List<MessageKey> getParamsNames() {
        return List.of(
                PLAYER_NAME_MK
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam(getPermission()+".others"));
    }
}
