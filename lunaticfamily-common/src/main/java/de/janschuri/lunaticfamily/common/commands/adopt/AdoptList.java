package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.handler.Adoption;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;
import java.util.Map;

public class AdoptList extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey headerMK = new CommandMessageKey(this,"header");
    private final CommandMessageKey pairsMK = new CommandMessageKey(this,"pairs");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt.list";
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public Adopt getParentCommand() {
        return new Adopt();
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

        Component msg = getAdoptList(page);

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

    private Component getAdoptList(int page) {
        List<Adoption> adoptList = AdoptionsTable.getAdoptionList(page, 10);

        Component msg = getMessage(headerMK, false);

        int index = 1 + (10*(page-1));
        Logger.debugLog("AdoptList: " + adoptList);
        for (Adoption e : adoptList) {
            FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(e.getParentID());
            FamilyPlayerImpl player2Fam = new FamilyPlayerImpl(e.getChildID());


            String hoverText = " (" + e.getDate() + ")";
            if (e.getPriest() > 0) {
                hoverText = hoverText + " -> " + new FamilyPlayerImpl(e.getPriest()).getName();
            }

            Component heart = Component.text(" " + Adoption.getDefaultParentEmoji() + " ", TextColor.fromHexString(e.getEmojiColor())).hoverEvent(HoverEvent.showText(Component.text(hoverText)));

            TextReplacementConfig indexRpl = getTextReplacementConfig("%index%", String.valueOf(index));
            TextReplacementConfig player1Rpl = getTextReplacementConfig("%parent%", player1Fam.getName());
            TextReplacementConfig player2Rpl = getTextReplacementConfig("%child%", player2Fam.getName());
            TextReplacementConfig heartRpl = TextReplacementConfig.builder().match("%emoji%").replacement(heart).build();

            msg = msg.append(Component.newline())
                    .append(getMessage(pairsMK, false)
                            .replaceText(indexRpl)
                            .replaceText(player1Rpl)
                            .replaceText(player2Rpl)
                            .replaceText(heartRpl));


            index++;
        }

        return msg;
    }
}
