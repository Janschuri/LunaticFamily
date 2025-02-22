package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.*;

public class FamilyDBList extends FamilyCommand implements HasParams, HasParentCommand {

    private static final FamilyDBList INSTANCE = new FamilyDBList();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Show a list of all players in the database.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Zeige eine Liste aller Spieler in der Datenbank.");
    private static final CommandMessageKey HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "header")
            .defaultMessage("en", "All players in the database:")
            .defaultMessage("de", "Alle Spieler in der Datenbank:");
    private static final CommandMessageKey PLAYERS_MK = new LunaticCommandMessageKey(INSTANCE, "players")
            .defaultMessage("en", "&6%index%: &b%name% &7(%gender%)")
            .defaultMessage("de", "&6%index%: &b%name% &7(%gender%)");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.dblist";
    }

    @Override
    public String getName() {
        return "dblist";
    }

    @Override
    public FamilyCommand getParentCommand() {
        return new Family();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(getMessage(NO_NUMBER_MK,
                placeholder("%input%", args[0])));
            }
        }

        Component msg = getPlayerList(page);

        sender.sendMessage(msg);

        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PAGE_MK.noPrefix())
        );
    }


    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }

    private Component getPlayerList(int page) {
        Map<Integer, FamilyPlayer> players = DatabaseRepository.getDatabase().find(FamilyPlayer.class).findList().stream()
                .collect(LinkedHashMap::new, (m, v) -> m.put(v.getId(), v), LinkedHashMap::putAll);

        ComponentBuilder<TextComponent, TextComponent.Builder> msg = Component.text().append(getMessage(HEADER_MK.noPrefix()));

        int i = 1;

        for (FamilyPlayer player : players.values()) {
            msg.append(Component.newline());

            int id = player.getId();
            String uuid = player.getUUID() == null ? "null" : player.getUUID().toString();
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

            Component row = getMessage(PLAYERS_MK.noPrefix(),
                placeholder("%name%", nameCmp),
                placeholder("%gender%", gender),
                placeholder("%index%", String.valueOf(page*i))
            );

            Component delete = Component.text(" [X]")
                    .clickEvent(ClickEvent.runCommand("/family delete " + uuid))
                    .hoverEvent(HoverEvent.showText(Component.text("Delete " + name)));

            row = row.append(delete);

            msg.append(row);
        }

        return msg.build();
    }
}
