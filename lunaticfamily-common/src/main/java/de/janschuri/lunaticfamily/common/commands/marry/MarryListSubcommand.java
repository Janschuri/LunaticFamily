package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;

public class MarryListSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "list";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryListSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            int page = 1;
            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(getPrefix() + getMessage("marry_list_no_number").replace("%input%", args[0]));
                }
            }

            List<Integer> marryList = MarriagesTable.getMarryList(page, 10);

            Component msg = Component.text(getPrefix() + getMessage("marry_list") + "\n");

            int index = 1 + (10*(page-1));
            Logger.debugLog("MarryList: " + marryList.toString());
            for (Integer e : marryList) {
                FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(e);
                FamilyPlayerImpl player2Fam = new FamilyPlayerImpl(player1Fam.getPartner().getId());


                String hoverText = " (" + player1Fam.getMarriageDate() + ")";
                if (player1Fam.getPriest() != null) {
                    hoverText = hoverText + " -> " + player1Fam.getPriest().getName();
                }

                msg = msg.append(Component.text(getPrefix() + " " + index + ": " + player1Fam.getName()))
                            .append(Component.text(" ❤ ", TextColor.fromHexString(player1Fam.getHeartColor())).hoverEvent(HoverEvent.showText(Component.text(hoverText))))
                            .append(Component.text(player2Fam.getName() + "\n"));

                index++;
            }
            sender.sendMessage(msg);
        }
        return true;
    }
}
