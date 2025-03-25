package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.Adoption;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.Placeholder;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;
import java.util.Map;

public class AdoptList extends FamilyCommand implements HasParams, HasParentCommand {

    private static final AdoptList INSTANCE = new AdoptList();

    private static final CommandMessageKey helpMK = new LunaticCommandMessageKey(INSTANCE,"help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Show the list of all adoptions.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &7- Zeige die Liste aller Adoptionen.");
    private static final CommandMessageKey headerMK = new LunaticCommandMessageKey(INSTANCE,"header")
            .defaultMessage("en", "All adoptions: ")
            .defaultMessage("de", "Alle Adoptionen: ");
    private static final CommandMessageKey pairsMK = new LunaticCommandMessageKey(INSTANCE,"pairs")
            .defaultMessage("en", "&6%index%: &b%parent% %emoji% &b%child%")
            .defaultMessage("de", "&6%index%: &b%parent% %emoji% &b%child%");


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
                sender.sendMessage(getMessage(NO_NUMBER_MK,
                placeholder("%input%", args[0])));
            }
        }

        Component msg = getAdoptList(page);

        sender.sendMessage(msg);

        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                helpMK, getPermission()
        );
    }

    @Override
    public List<MessageKey> getParamsNames() {
        return List.of(
                PAGE_MK
        );
    }


    @Override
    public List<Map<String, String>> getParams() {
        Map<String, String> numbers = Map.of(
                "1", getPermission(),
                "2", getPermission(),
                "3", getPermission()
        );

        return List.of(numbers);
    }

    private Component getAdoptList(int page) {
        List<Adoption> adoptList = DatabaseRepository.getDatabase().find(Adoption.class).where().isNull("unadoptDate").setFirstRow(10*(page-1)).setMaxRows(10).findList();

        Component msg = getMessage(headerMK.noPrefix());

        int index = 1 + (10*(page-1));
        for (Adoption e : adoptList) {
            FamilyPlayer player1Fam = e.getParent();
            FamilyPlayer player2Fam = e.getChild();


            String hoverText = " (" + e.getDate() + ")";
            if (e.getPriest() == null) {
                hoverText = hoverText + " -> " + e.getPriest().getName();
            }

            Component heart = Component.text(" " + Adoption.getDefaultParentEmoji() + " ", TextColor.fromHexString(e.getEmojiColor())).hoverEvent(HoverEvent.showText(Component.text(hoverText)));

            Placeholder heartRpl = placeholder("%emoji%", heart);

            Placeholder indexRpl = placeholder("%index%", String.valueOf(index));
            Placeholder player1Rpl = placeholder("%parent%", player1Fam.getName());
            Placeholder player2Rpl = placeholder("%child%", player2Fam.getName());

            msg = msg.append(Component.newline())
                    .append(getMessage(pairsMK.noPrefix(), heartRpl, indexRpl, player1Rpl, player2Rpl));


            index++;
        }

        return msg;
    }
}
