package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableMessage;

import java.util.ArrayList;
import java.util.List;

public class MarryHeartSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "heart";
    private static final String PERMISSION = "lunaticfamily.marry.heart";

    public MarryHeartSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, Language.getColorLangs());
    }
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (args.length < 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
            } else if (!Language.isColorLang(args[0]) && !Utils.isValidHexCode(args[0])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_heart_no_color"));
            } else {

                String hexColor = "";

                String color = args[0];

                String colorMsg = "";

                if (Language.isColorLang(args[0])) {
                    String colorKey = Language.getColorKeyFromLang(color);
                    if (player.hasPermission(PERMISSION + ".color." + colorKey)){
                        hexColor = PluginConfig.getColor(colorKey);
                        colorMsg = Language.getColorLang(colorKey);
                    } else {
                        sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                        return true;
                    }
                } else {
                    if (player.hasPermission(PERMISSION + ".hex")) {
                        hexColor = args[0];
                        colorMsg = args[0];
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
