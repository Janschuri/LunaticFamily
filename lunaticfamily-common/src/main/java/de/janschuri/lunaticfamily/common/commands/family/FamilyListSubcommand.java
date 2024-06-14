package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyListSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey helpOthersMK = new CommandMessageKey(this,"help_others");
    private final CommandMessageKey headerMK = new CommandMessageKey(this,"header");
    private final CommandMessageKey othersHeaderMK = new CommandMessageKey(this,"others_header");
    private final CommandMessageKey relationsMK = new CommandMessageKey(this,"relations");

    @Override
    public String getPermission() {
        return "lunaticfamily.family.list";
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public FamilySubcommand getParentCommand() {
        return new FamilySubcommand();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                helpMK, getPermission(),
                helpOthersMK, getPermission() + ".others"
        );
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            List<String> list = LunaticFamily.getConfig().getFamilyList();

            if (!(sender instanceof PlayerSender) && args.length < 1) {
                sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            } else if (args.length == 0) {
                PlayerSender player = (PlayerSender) sender;
                UUID uuid = player.getUniqueId();
                FamilyPlayerImpl playerFam = new FamilyPlayerImpl(uuid);


                Map<String, Integer> familyList = playerFam.getFamilyMap();
                ComponentBuilder msg = Component.text().append(getMessage(headerMK, false));

                sender.sendMessage(getFamilyListMessage(list, familyList, msg));
                playerFam.updateFamilyTree();
            } else {

                String playerName = args[0];
                UUID player1UUID = PlayerDataTable.getUUID(playerName);

                if (player1UUID == null) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", playerName)));
                    return true;
                }


                if (!sender.hasPermission(getPermission() + ".others")) {
                    sender.sendMessage(getMessage(NO_PERMISSION_MK));
                    return true;
                }

                    FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);
                    Map<String, Integer> familyList = player1Fam.getFamilyMap();
                    ComponentBuilder msg = Component.text();
                    msg.append(getMessage(othersHeaderMK, false)
                            .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())));

                    sender.sendMessage(getFamilyListMessage(list, familyList, msg));
                    player1Fam.updateFamilyTree();
            }
        }
        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK, false)
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }

    private Component getFamilyListMessage(List<String> list, Map<String, Integer> familyList, ComponentBuilder msg) {

        for (String e : list) {
            if (familyList.containsKey(e)) {
                int relationID = familyList.get(e);
                FamilyPlayerImpl relationFam = new FamilyPlayerImpl(relationID);
                String relationKey = e.replace("first_", "")
                        .replace("second_", "")
                        .replace("third_", "")
                        .replace("fourth_", "")
                        .replace("fifth_", "")
                        .replace("sixth_", "")
                        .replace("seventh_", "")
                        .replace("eighth_", "");

                Component relation = LegacyComponentSerializer.legacyAmpersand().deserialize(getRelation(relationKey, relationFam.getGender()));
                Component name = Component.text(relationFam.getName());

                TextReplacementConfig relationRpl = TextReplacementConfig.builder()
                        .match("%relation%")
                        .replacement(relation).build();

                TextReplacementConfig nameRpl = TextReplacementConfig.builder()
                        .match("%player%")
                        .replacement(name).build();

                Component component = getMessage(relationsMK, false)
                        .replaceText(relationRpl)
                        .replaceText(nameRpl);

                msg.append(Component.newline())
                        .append(component);
            }
        }

        return msg.build();
    }
}
