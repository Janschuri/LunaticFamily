package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.*;

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
        Map<Integer, FamilyPlayerImpl> players = DatabaseRepository.getDatabase().find(FamilyPlayerImpl.class).findList().stream()
                .collect(LinkedHashMap::new, (m, v) -> m.put(v.getId(), v), LinkedHashMap::putAll);

        ComponentBuilder<TextComponent, TextComponent.Builder> msg = Component.text().append(getMessage(headerMK, false));

        int i = 1;

        for (FamilyPlayerImpl player : players.values()) {
            msg.append(Component.newline());

            int id = player.getId();
            String uuid = player.getUniqueId() == null ? "null" : player.getUniqueId().toString();
            String skinURL = player.getGender() == null ? "null" : player.getSkinURL();
            String background = player.getBackground() == null ? "null" : player.getBackground();
            String name = player.getName() == null ? "null" : player.getName();
            String gender = player.getGender() == null ? "null" : player.getGender();

            ComponentBuilder<TextComponent, TextComponent.Builder> hover = Component.text()
                            .append(Component.text("ID: " + id))
                            .append(Component.newline())
                            .append(Component.text("UUID: " + uuid))
                            .append(Component.newline())
                            .append(Component.text("SkinURL: " + skinURL))
                            .append(Component.newline())
                            .append(Component.text("Background: " + background))
                    ;

            Component nameCmp = Component.text(name)
                    .hoverEvent(HoverEvent.showText(hover.build()));

            TextReplacementConfig nameRpl = TextReplacementConfig.builder().match("%name%").replacement(nameCmp).build();

            Component row = getMessage(playersMK, false)
                    .replaceText(nameRpl)
                    .replaceText(getTextReplacementConfig("%gender%", gender))
                    .replaceText(getTextReplacementConfig("%index%",  String.valueOf(page*i)));

            Component delete = Component.text(" [X]")
                    .clickEvent(ClickEvent.runCommand("/family delete " + uuid))
                    .hoverEvent(HoverEvent.showText(Component.text("Delete " + name)));

            row = row.append(delete);

            msg.append(row);
        }

        return msg.build();
    }
}
