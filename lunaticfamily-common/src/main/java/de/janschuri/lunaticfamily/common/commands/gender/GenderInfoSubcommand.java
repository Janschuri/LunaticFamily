package de.janschuri.lunaticfamily.common.commands.gender;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.GenderSubcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GenderInfoSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey helpOthersMK = new CommandMessageKey(this,"help_others");
    private final CommandMessageKey infoMK = new CommandMessageKey(this,"info");
    private final CommandMessageKey infoOthersMK = new CommandMessageKey(this,"info_others");

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
    public GenderSubcommand getParentCommand() {
        return new GenderSubcommand();
    }

    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof PlayerSender)) {
                sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
                return true;
            }

            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);
            sender.sendMessage(getMessage(infoMK)
                    .replaceText(getTextReplacementConfig("%gender%", getGenderLang(playerFam.getGender()))));
            return true;
        }

        String playerArg = args[0];
        UUID playerUUID;

        if (Utils.isUUID(playerArg)) {
            playerUUID = UUID.fromString(playerArg);

            if (PlayerDataTable.getID(playerUUID) < 0) {
                sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                        .replaceText(getTextReplacementConfig("%player%", playerArg)));
                return true;
            }
        } else {
            playerUUID = PlayerDataTable.getUUID(playerArg);

            if (playerUUID == null) {
                sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                        .replaceText(getTextReplacementConfig("%player%", playerArg)));
                return true;
            }
        }

        FamilyPlayerImpl player = new FamilyPlayerImpl(playerUUID);

        sender.sendMessage(getMessage(infoOthersMK)
                .replaceText(getTextReplacementConfig("%player%", player.getName()))
                .replaceText(getTextReplacementConfig("%gender%", getGenderLang(player.getGender()))));
        return true;
    }

    @Override
    public Component getParamsName() {
        return getMessage(PLAYER_NAME_MK, false);
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
