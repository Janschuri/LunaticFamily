package de.janschuri.lunaticfamily.common.commands.gender;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.command.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GenderInfo extends FamilyCommand implements HasParentCommand, HasParams {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey helpOthersMK = new LunaticCommandMessageKey(this,"help_others");
    private final CommandMessageKey infoMK = new LunaticCommandMessageKey(this,"info");
    private final CommandMessageKey infoOthersMK = new LunaticCommandMessageKey(this,"info_others");

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                helpMK, getPermission(),
                helpOthersMK, getPermission() + ".others"
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
            FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
            sender.sendMessage(getMessage(infoMK,
                placeholder("%gender%", getGenderLang(playerFam.getGender()))));
            return true;
        }

        String playerArg = args[0];
        UUID playerUUID;


        FamilyPlayer player;

        if (Utils.isUUID(playerArg)) {
            playerUUID = UUID.fromString(playerArg);

            if (DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("uniqueId", playerUUID).findCount() == 0) {
                sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", playerArg)));
                return true;
            }

            player = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("uniqueId", playerUUID).findOne();
        } else {
            player = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", playerArg).findOne();

            if (player == null) {
                sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", playerArg)));
                return true;
            }
        }

        sender.sendMessage(getMessage(infoOthersMK,
                placeholder("%player%", player.getName()),
                placeholder("%gender%", getGenderLang(player.getGender()))));
        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK.noPrefix())
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
