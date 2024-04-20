package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.ClickableMessage;
import de.janschuri.lunaticFamily.commands.CommandSender;
import de.janschuri.lunaticFamily.commands.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.TextReplacementConfig;
//import net.kyori.adventure.text.format.TextColor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MarryHeartSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "heart";
    private static final String permission = "lunaticfamily.marry.heart";
    private static final List<String> colors = Language.getColorLangs();

    public MarryHeartSubcommand() {
        super(mainCommand, name, permission, colors);
    }
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (!Language.isColorLang(args[1]) && !Utils.isValidHexCode(args[1])) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_heart_no_color"));
            } else {

                String hexColor = "";

                String color = args[1];

                String colorMsg = "";

                if (Language.isColorLang(args[1])) {
                    String colorKey = Language.getColorKeyFromLang(color);
                    if (player.hasPermission(permission + ".color." + colorKey)){
                        hexColor = PluginConfig.colors.get(colorKey);
                        colorMsg = Language.getColorLang(colorKey);
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                        return true;
                    }
                } else {
                    if (player.hasPermission(permission + ".hex")) {
                        hexColor = args[1];
                        colorMsg = args[1];
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                        return true;
                    }
                }

                String[] msgArray = Language.getMessage("marry_heart_color").split("%color%");
                List<ClickableMessage> msg = new ArrayList<>();
                msg.add(new ClickableMessage(Language.prefix + msgArray[0]));
                msg.add(new ClickableMessage(colorMsg).setColor(hexColor));
                msg.add(new ClickableMessage(msgArray[1]));

                player.sendMessage(msg);
                playerFam.setHeartColor(hexColor);
            }


        }
        return true;
    }
}
