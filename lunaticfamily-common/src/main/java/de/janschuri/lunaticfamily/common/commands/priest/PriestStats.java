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

import java.util.Map;

public class PriestStats extends FamilyCommand implements HasParentCommand {

    private static final PriestStats INSTANCE = new PriestStats();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", INSTANCE.getDefaultHelpMessage("Show your statistics as a priest."))
            .defaultMessage("de", INSTANCE.getDefaultHelpMessage("Zeige deine Statistiken als Priester."));
    private static final CommandMessageKey HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "header")
            .defaultMessage("en", "Your statistics:")
            .defaultMessage("de", "Deine Statistiken:");
    private static final CommandMessageKey MARRIAGES_MK = new LunaticCommandMessageKey(INSTANCE, "marriages")
            .defaultMessage("en", "&6Marriages: &b%active%/%total% &7(%percentage%%)")
            .defaultMessage("de", "&6Ehen: &b%active%/%total% &7(%percentage%%)");
    private static final CommandMessageKey ADOPTIONS_MK = new LunaticCommandMessageKey(INSTANCE, "adoptions")
            .defaultMessage("en", "&6Adoptions: &b%active%/%total% &7(%percentage%%)")
            .defaultMessage("de", "&6Adoptionen: &b%active%/%total% &7(%percentage%%)");
    private static final CommandMessageKey SIBLINGS_MK = new LunaticCommandMessageKey(INSTANCE, "siblings")
            .defaultMessage("en", "&6Siblinghoods: &b%active%/%total% &7(%percentage%%)")
            .defaultMessage("de", "&6Geschwisterschaften: &b%active%/%total% &7(%percentage%%)");



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

        msg.append(getMessage(HEADER_MK.noPrefix()));

        FamilyPlayer playerFam = getFamilyPlayer(player.getUniqueId());

        int totalAdoptions = playerFam.getAdoptionsAsPriest().size();
        int activeAdoptions = playerFam.getAdoptionsAsPriest().stream()
                .filter(adoption -> adoption.getUnadoptDate() != null)
                .toList().size();
        String percentageAdoption = Utils.getPercentageAsString(activeAdoptions, totalAdoptions);

        msg.append(Component.newline());
        msg.append(getMessage(ADOPTIONS_MK.noPrefix(),
                placeholder("%total%", String.valueOf(totalAdoptions)),
                placeholder("%active%", String.valueOf(activeAdoptions)),
                placeholder("%percentage%", percentageAdoption)));


        int totalMarriages = playerFam.getMarriagesAsPriest().size();
        int activeMarriages = playerFam.getMarriagesAsPriest().stream()
                .filter(marriage -> marriage.getDivorceDate() != null)
                .toList().size();
        String percentageMarriage = Utils.getPercentageAsString(activeMarriages, totalMarriages);

        msg.append(Component.newline());
        msg.append(getMessage(MARRIAGES_MK.noPrefix(),
                placeholder("%total%", String.valueOf(totalMarriages)),
                placeholder("%active%", String.valueOf(activeMarriages)),
                placeholder("%percentage%", percentageMarriage)));

        int totalSiblings = playerFam.getSiblinghoodsAsPriest().size();
        int activeSiblings = playerFam.getSiblinghoodsAsPriest().stream()
                .filter(siblinghood -> siblinghood.getUnsiblingDate() != null)
                .toList().size();
        String percentageSiblings = Utils.getPercentageAsString(activeSiblings, totalSiblings);

        msg.append(Component.newline());
        msg.append(getMessage(SIBLINGS_MK.noPrefix(),
                placeholder("%total%", String.valueOf(totalSiblings)),
                placeholder("%active%", String.valueOf(activeSiblings)),
                placeholder("%percentage%", percentageSiblings)));


        player.sendMessage(msg.build());
        return true;


    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }

    @Override
    public Priest getParentCommand() {
        return new Priest();
    }


}
