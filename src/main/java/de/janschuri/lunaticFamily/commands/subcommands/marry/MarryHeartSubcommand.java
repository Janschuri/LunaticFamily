package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableMessage;

import java.util.ArrayList;
import java.util.List;

public class MarryHeartSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "heart";
    private static final String permission = "lunaticfamily.marry.heart";
    private static List<String> colors;
    private static Language language;

    public MarryHeartSubcommand() {
        super(mainCommand, name, permission, colors);
        language = Language.getInstance();
        colors = language.getColorLangs();
    }
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
            } else if (!language.isColorLang(args[1]) && !Utils.isValidHexCode(args[1])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_heart_no_color"));
            } else {

                String hexColor = "";

                String color = args[1];

                String colorMsg = "";

                if (language.isColorLang(args[1])) {
                    String colorKey = language.getColorKeyFromLang(color);
                    if (player.hasPermission(permission + ".color." + colorKey)){
                        hexColor = PluginConfig.colors.get(colorKey);
                        colorMsg = language.getColorLang(colorKey);
                    } else {
                        sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                        return true;
                    }
                } else {
                    if (player.hasPermission(permission + ".hex")) {
                        hexColor = args[1];
                        colorMsg = args[1];
                    } else {
                        sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                        return true;
                    }
                }

                String[] msgArray = language.getMessage("marry_heart_color").split("%color%");
                List<ClickableMessage> msg = new ArrayList<>();
                msg.add(new ClickableMessage(language.getPrefix() + msgArray[0]));
                msg.add(new ClickableMessage(colorMsg).setColor(hexColor));
                msg.add(new ClickableMessage(msgArray[1]));

                player.sendMessage(msg);
                playerFam.setHeartColor(hexColor);
            }


        }
        return true;
    }
}
