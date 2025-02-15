package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;

public class PriestStats extends FamilyCommand implements HasParentCommand {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this, "help");
    private final CommandMessageKey headerMK = new LunaticCommandMessageKey(this, "header");
    private final CommandMessageKey adoptionsMK = new LunaticCommandMessageKey(this, "adoptions");
    private final CommandMessageKey marriagesMK = new LunaticCommandMessageKey(this, "marriages");
    private final CommandMessageKey siblingsMK = new LunaticCommandMessageKey(this, "siblings");


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
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        ComponentBuilder msg = Component.text();

        msg.append(getMessage(headerMK.noPrefix()));

        FamilyPlayer playerFam = getFamilyPlayer(player.getUniqueId());

        int totalAdoptions = playerFam.getAdoptionsAsPriest().size();
        int activeAdoptions = playerFam.getAdoptionsAsPriest().stream()
                .filter(adoption -> adoption.getUnadoptDate() != null)
                .toList().size();
        String percentageAdoption = Utils.getPercentageAsString(activeAdoptions, totalAdoptions);

        msg.append(Component.newline());
        msg.append(getMessage(adoptionsMK.noPrefix(),
                placeholder("%total%", String.valueOf(totalAdoptions)),
                placeholder("%active%", String.valueOf(activeAdoptions)),
                placeholder("%percentage%", percentageAdoption)));


        int totalMarriages = playerFam.getMarriagesAsPriest().size();
        int activeMarriages = playerFam.getMarriagesAsPriest().stream()
                .filter(marriage -> marriage.getDivorceDate() != null)
                .toList().size();
        String percentageMarriage = Utils.getPercentageAsString(activeMarriages, totalMarriages);

        msg.append(Component.newline());
        msg.append(getMessage(marriagesMK.noPrefix(),
                placeholder("%total%", String.valueOf(totalMarriages)),
                placeholder("%active%", String.valueOf(activeMarriages)),
                placeholder("%percentage%", percentageMarriage)));

        int totalSiblings = playerFam.getSiblinghoodsAsPriest().size();
        int activeSiblings = playerFam.getSiblinghoodsAsPriest().stream()
                .filter(siblinghood -> siblinghood.getUnsiblingDate() != null)
                .toList().size();
        String percentageSiblings = Utils.getPercentageAsString(activeSiblings, totalSiblings);

        msg.append(Component.newline());
        msg.append(getMessage(siblingsMK.noPrefix(),
                placeholder("%total%", String.valueOf(totalSiblings)),
                placeholder("%active%", String.valueOf(activeSiblings)),
                placeholder("%percentage%", percentageSiblings)));


        player.sendMessage(msg.build());
        return true;


    }

    @Override
    public Priest getParentCommand() {
        return new Priest();
    }


}
