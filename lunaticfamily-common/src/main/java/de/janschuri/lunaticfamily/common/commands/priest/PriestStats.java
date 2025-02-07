package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.FamilyPlayer;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.Adoption;
import de.janschuri.lunaticfamily.common.handler.Marriage;
import de.janschuri.lunaticfamily.common.handler.Siblinghood;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;

import javax.xml.crypto.Data;

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

        int totalAdoptions = DatabaseRepository.getDatabase().find(Adoption.class).where().eq("priest", familyPlayer.getId()).findCount();
        int activeAdoptions = DatabaseRepository.getDatabase().find(Adoption.class).where().eq("priest", familyPlayer.getId()).and().isNull("unadoptDate").findCount();
        String percentageAdoption = Utils.getPercentageAsString(activeAdoptions, totalAdoptions);

        msg.append(Component.newline());
        msg.append(getMessage(adoptionsMK, false)
                .replaceText(getTextReplacementConfig("%total%", String.valueOf(totalAdoptions)))
                .replaceText(getTextReplacementConfig("%active%", String.valueOf(activeAdoptions)))
                .replaceText(getTextReplacementConfig("%percentage%", percentageAdoption)));


        int totalMarriages = DatabaseRepository.getDatabase().find(Marriage.class).where().eq("priest", familyPlayer.getId()).findCount();
        int activeMarriages = DatabaseRepository.getDatabase().find(Marriage.class).where().eq("priest", familyPlayer.getId()).and().isNull("divorceDate").findCount();
        String percentageMarriage = Utils.getPercentageAsString(activeMarriages, totalMarriages);

        msg.append(Component.newline());
        msg.append(getMessage(marriagesMK, false)
                .replaceText(getTextReplacementConfig("%total%", String.valueOf(totalMarriages)))
                .replaceText(getTextReplacementConfig("%active%", String.valueOf(activeMarriages)))
                .replaceText(getTextReplacementConfig("%percentage%", percentageMarriage)));

        int totalSiblings = DatabaseRepository.getDatabase().find(Siblinghood.class).where().eq("priest", familyPlayer.getId()).findCount();
        int activeSiblings = DatabaseRepository.getDatabase().find(Siblinghood.class).where().eq("priest", familyPlayer.getId()).and().isNull("unadoptDate").findCount();
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
