package de.janschuri.lunaticfamily.commands.subcommands.marry;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Logger;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableMessage;

import java.util.ArrayList;
import java.util.List;

public class MarryListSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "list";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryListSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            int page = 1;
            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_list_no_number").replace("%input%", args[0]));
                }
            }

            List<Integer> marryList = MarriagesTable.getMarryList(page, 10);
            List<ClickableMessage> msg = new ArrayList<>();
            msg.add(new ClickableMessage(language.getPrefix() + language.getMessage("marry_list") + "\n"));
            int index = 1 + (10*(page-1));
            Logger.debugLog("MarryList: " + marryList.toString());
            for (Integer e : marryList) {
                FamilyPlayer player1Fam = new FamilyPlayer(e);
                FamilyPlayer player2Fam = new FamilyPlayer(player1Fam.getPartner().getID());


                String hoverText = " (" + player1Fam.getMarriageDate() + ")";
                if (player1Fam.getPriest() != null) {
                    hoverText = hoverText + " -> " + player1Fam.getPriest().getName();
                }

                msg.add(new ClickableMessage((language.getPrefix() + " " + index + ": " + player1Fam.getName())));
                msg.add(new ClickableMessage(" ❤ ", hoverText).setColor(player1Fam.getHeartColor()));
                msg.add(new ClickableMessage(player2Fam.getName() + "\n"));


                index++;
            }
            sender.sendMessage(msg);
        }
        return true;
    }
}
