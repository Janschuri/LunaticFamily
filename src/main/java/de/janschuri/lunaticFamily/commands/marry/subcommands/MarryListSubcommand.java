package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MarryListSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "list";
    private static final String permission = "lunaticfamily.marry";

    public MarryListSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
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

            List<Integer> marryList = LunaticFamily.getDatabase().getMarryList(page, 10);
            TextComponent msg = new TextComponent(Language.prefix + Language.getMessage("marry_list") + "\n");
            int index = 1 + (10*(page-1));
            for (Integer e : marryList) {
                FamilyPlayer player1Fam = new FamilyPlayer(e);
                FamilyPlayer player2Fam = new FamilyPlayer(player1Fam.getPartner().getID());

                TextComponent text = new TextComponent(Language.prefix + " " + index + ": " + player1Fam.getName() + " ❤ " + player2Fam.getName() + "\n");

                String hoverText = " (" + player1Fam.getMarriageDate() + ")";
                if (player1Fam.getPriest() != null) {
                    hoverText = hoverText + " -> " + player1Fam.getPriest().getName();
                }

                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
                msg.addExtra(text);

                index++;
            }
            sender.sendMessage(msg);
        }
    }
}
