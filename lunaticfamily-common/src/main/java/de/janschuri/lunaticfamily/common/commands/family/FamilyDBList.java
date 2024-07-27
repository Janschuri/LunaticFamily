package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FamilyDBList extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey headerMK = new CommandMessageKey(this,"header");
    private final CommandMessageKey playersMK = new CommandMessageKey(this,"players");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.dblist";
    }

    @Override
    public String getName() {
        return "dblist";
    }

    @Override
    public Subcommand getParentCommand() {
        return new Family();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }
        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(getMessage(NO_NUMBER_MK)
                        .replaceText(getTextReplacementConfig("%input%", args[0])));
            }
        }

        Component msg = getPlayerList(page);

        sender.sendMessage(msg);

        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PAGE_MK, false)
        );
    }


    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }

    private Component getPlayerList(int page) {
        Map<Integer, Map<String, String>> players = sortMapByReverseInteger(PlayerDataTable.getPlayerList(page));

        ComponentBuilder<TextComponent, TextComponent.Builder> msg = Component.text().append(getMessage(headerMK, false));

        int i = 1;

        for (Map<String, String> player : players.values()) {
            msg.append(Component.newline());
            ComponentBuilder<TextComponent, TextComponent.Builder> hover = Component.text()
                            .append(Component.text("ID: " + player.get("id")))
                            .append(Component.newline())
                            .append(Component.text("UUID: " + player.get("uuid")))
                            .append(Component.newline())
                            .append(Component.text("SkinURL: " + player.get("skinURL")))
                            .append(Component.newline())
                            .append(Component.text("Background: " + player.get("background")))
                    ;

            Component name = Component.text(player.get("name"))
                    .hoverEvent(HoverEvent.showText(hover.build()));

            TextReplacementConfig nameRpl = TextReplacementConfig.builder().match("%name%").replacement(name).build();

            Component row = getMessage(playersMK, false)
                    .replaceText(nameRpl)
                    .replaceText(getTextReplacementConfig("%gender%", player.get("gender")))
                    .replaceText(getTextReplacementConfig("%index%",  String.valueOf(page*i)));

            msg.append(row);
        }

        return msg.build();
    }

    private static Map<Integer, Map<String, String>> sortMapByReverseInteger(Map<Integer, Map<String, String>> inputMap) {
        // Convert map entries to a list
        List<Map.Entry<Integer, Map<String, String>>> entryList = new ArrayList<>(inputMap.entrySet());

        // Sort the list in reverse order based on the integer keys
        entryList.sort((entry1, entry2) -> entry2.getKey().compareTo(entry1.getKey()));

        // Convert the sorted list back to a map
        Map<Integer, Map<String, String>> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Map<String, String>> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
