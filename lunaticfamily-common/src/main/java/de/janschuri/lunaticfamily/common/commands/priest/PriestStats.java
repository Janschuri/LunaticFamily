package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.FamilyPlayer;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;

public class PriestStats extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey headerMK = new CommandMessageKey(this,"header");
    private final CommandMessageKey adoptionsMK = new CommandMessageKey(this,"adoptions");
    private final CommandMessageKey marriagesMK = new CommandMessageKey(this,"marriages");
    private final CommandMessageKey siblingsMK = new CommandMessageKey(this,"siblings");


    @Override
    public String getPermission() {
        return "lunaticfamily.priest.stats";
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public boolean execute(Sender sender, String[] strings) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        ComponentBuilder msg = Component.text();

        msg.append(getMessage(headerMK, false));

        PlayerSender player = (PlayerSender) sender;
        FamilyPlayer familyPlayer = getFamilyPlayer(player.getUniqueId());

        int totalAdoptions = AdoptionsTable.getPriestsTotalAdoptionsCount(familyPlayer.getId());
        int activeAdoptions = AdoptionsTable.getPriestsAdoptionsCount(familyPlayer.getId());
        String percentageAdoption = Utils.getPercentageAsString(activeAdoptions, totalAdoptions);

        msg.append(Component.newline());
        msg.append(getMessage(adoptionsMK, false)
                .replaceText(getTextReplacementConfig("%total%", String.valueOf(totalAdoptions)))
                .replaceText(getTextReplacementConfig("%active%", String.valueOf(activeAdoptions)))
                .replaceText(getTextReplacementConfig("%percentage%", percentageAdoption)));


        int totalMarriages = MarriagesTable.getPriestsTotalMarriagesCount(familyPlayer.getId());
        int activeMarriages = MarriagesTable.getPriestsMarriagesCount(familyPlayer.getId());
        String percentageMarriage = Utils.getPercentageAsString(activeMarriages, totalMarriages);

        msg.append(Component.newline());
        msg.append(getMessage(marriagesMK, false)
                .replaceText(getTextReplacementConfig("%total%", String.valueOf(totalMarriages)))
                .replaceText(getTextReplacementConfig("%active%", String.valueOf(activeMarriages)))
                .replaceText(getTextReplacementConfig("%percentage%", percentageMarriage)));

        int totalSiblings = SiblinghoodsTable.getPriestsTotalSiblinghoodsCount(familyPlayer.getId());
        int activeSiblings = SiblinghoodsTable.getPriestsSiblinghoodsCount(familyPlayer.getId());
        String percentageSiblings = Utils.getPercentageAsString(activeSiblings, totalSiblings);

        msg.append(Component.newline());
        msg.append(getMessage(siblingsMK, false)
                .replaceText(getTextReplacementConfig("%total%", String.valueOf(totalSiblings)))
                .replaceText(getTextReplacementConfig("%active%", String.valueOf(activeSiblings)))
                .replaceText(getTextReplacementConfig("%percentage%", percentageSiblings)));


        player.sendMessage(msg.build());
        return true;



    }

    @Override
    public Priest getParentCommand() {
        return new Priest();
    }


}
