package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;

import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.handler.Marriage;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Placeholder;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MarryList extends FamilyCommand implements HasParentCommand, HasParams {

    private static final MarryList INSTANCE = new MarryList();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- List all married couples.");
    private static final CommandMessageKey HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "header")
            .defaultMessage("en", "All married couples on this server: ");
    private static final CommandMessageKey PAIRS_MK = new LunaticCommandMessageKey(INSTANCE, "pairs")
            .defaultMessage("en", "&6%index%: &b%player1% %emoji% &b%player2%");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry.list";
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        Logger.debugLog("MarryList: " + Arrays.toString(args));

        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (Exception e) {
                Logger.debugLog("MarryList1: ");
                sender.sendMessage(getMessage(NO_NUMBER_MK,
                placeholder("%input%", args[0])));
                return true;
            }
        }

        Component msg = getMarryList(page);

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

    private Component getMarryList(int page) {
        List<Marriage> marryList = DatabaseRepository.getDatabase().find(Marriage.class).where().isNull("divorceDate").setFirstRow(10*(page-1)).setMaxRows(10).findList();

        Component msg = getMessage(HEADER_MK.noPrefix());

        int index = 1 + (10*(page-1));
        Logger.debugLog("MarryList: " + marryList);
        for (Marriage e : marryList) {
            FamilyPlayer player1Fam = e.getPlayer1();
            FamilyPlayer player2Fam = e.getPlayer2();


            String hoverText = " (" + e.getDate() + ")";
            if (e.getPriest() != null) {
                hoverText = hoverText + " -> " + e.getPriest().getName();
            }

            Component heart = Component.text(" " + Marriage.getDefaultEmoji() + " ", TextColor.fromHexString(e.getEmojiColor())).hoverEvent(HoverEvent.showText(Component.text(hoverText)));

            Placeholder indexRpl = placeholder("%index%", String.valueOf(index));
            Placeholder player1Rpl = placeholder("%player1%", player1Fam.getName());
            Placeholder player2Rpl = placeholder("%player2%", player2Fam.getName());
            Placeholder heartRpl = placeholder("%emoji%", heart);

            msg = msg.append(Component.newline())
                    .append(getMessage(PAIRS_MK.noPrefix(),
                            indexRpl,
                            player1Rpl,
                            player2Rpl,
                            heartRpl
                    ));



            index++;
        }

        return msg;
    }
}
