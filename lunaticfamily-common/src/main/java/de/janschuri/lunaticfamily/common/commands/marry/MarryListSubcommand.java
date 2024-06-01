package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;

public class MarryListSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey headerMK = new CommandMessageKey(this,"header");
    private final CommandMessageKey pairsMK = new CommandMessageKey(this,"pairs");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry.list";
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public MarrySubcommand getParentCommand() {
        return new MarrySubcommand();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            int page = 1;
            if (args.length > 0) {
                try {
                    page = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(getMessage(NO_NUMBER_MK)
                            .replaceText(getTextReplacementConfig("%input%", args[0])));
                }
            }

            List<Integer> marryList = MarriagesTable.getMarryList(page, 10);

            Component msg = getMessage(headerMK);

            int index = 1 + (10*(page-1));
            Logger.debugLog("MarryList: " + marryList.toString());
            for (Integer e : marryList) {
                FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(e);
                FamilyPlayerImpl player2Fam = new FamilyPlayerImpl(player1Fam.getPartner().getId());


                String hoverText = " (" + player1Fam.getMarriageDate() + ")";
                if (player1Fam.getPriest() != null) {
                    hoverText = hoverText + " -> " + player1Fam.getPriest().getName();
                }

                Component heart = Component.text(" ❤ ", TextColor.fromHexString(player1Fam.getHeartColor())).hoverEvent(HoverEvent.showText(Component.text(hoverText)));

                TextReplacementConfig indexRpl = getTextReplacementConfig("%index%", String.valueOf(index));
                TextReplacementConfig player1Rpl = getTextReplacementConfig("%player1%", player1Fam.getName());
                TextReplacementConfig player2Rpl = getTextReplacementConfig("%player2%", player2Fam.getName());
                TextReplacementConfig heartRpl = TextReplacementConfig.builder().match("%heart%").replacement(heart).build();

                msg = msg.append(Component.newline())
                        .append(getMessage(pairsMK)
                        .replaceText(indexRpl)
                        .replaceText(player1Rpl)
                        .replaceText(player2Rpl)
                        .replaceText(heartRpl));



                index++;
            }
            sender.sendMessage(msg);
        }
        return true;
    }
}
