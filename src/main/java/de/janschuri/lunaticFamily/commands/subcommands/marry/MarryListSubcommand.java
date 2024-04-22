package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.utils.ClickableMessage;
import de.janschuri.lunaticFamily.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;

import java.util.ArrayList;
import java.util.List;

public class MarryListSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "list";
    private static final String permission = "lunaticfamily.marry";

    public MarryListSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Language.prefix + Language.getMessage("marry_list_no_number").replace("%input%", args[1]));
                }
            }

            List<Integer> marryList = Database.getDatabase().getMarryList(page, 10);
            List<ClickableMessage> msg = new ArrayList<>();
            msg.add(new ClickableMessage(Language.prefix + Language.getMessage("marry_list") + "\n"));
            int index = 1 + (10*(page-1));
            for (Integer e : marryList) {
                FamilyPlayer player1Fam = new FamilyPlayer(e);
                FamilyPlayer player2Fam = new FamilyPlayer(player1Fam.getPartner().getID());


                String hoverText = " (" + player1Fam.getMarriageDate() + ")";
                if (player1Fam.getPriest() != null) {
                    hoverText = hoverText + " -> " + player1Fam.getPriest().getName();
                }

                msg.add(new ClickableMessage((Language.prefix + " " + index + ": " + player1Fam.getName())));
                msg.add(new ClickableMessage(" ❤ ", hoverText).setColor(player1Fam.getHeartColor()));
                msg.add(new ClickableMessage(player2Fam.getName() + "\n"));


                index++;
            }
            sender.sendMessage(msg);
        }
        return true;
    }
}
