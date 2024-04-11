package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MarryHeartSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "heart";
    private static final String permission = "lunaticfamily.marry.heart";
    private static final List<String> colors = Language.getColorLangs();

    public MarryHeartSubcommand() {
        super(mainCommand, name, permission, colors);
    }
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (!Language.isColorLang(args[1]) && !Utils.isValidHexCode(args[1])) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_heart_no_color"));
            } else {

                String hexColor = "";

                String color = args[1];

                if (Language.isColorLang(args[1])) {
                    String colorKey = Language.getColorKeyFromLang(color);
                    if (player.hasPermission(permission + ".color." + colorKey)){
                        hexColor = Config.colors.get(colorKey);
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                        return;
                    }
                } else {
                    if (player.hasPermission(permission + ".hex")) {
                        hexColor = args[1];
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                        return;
                    }
                }


                Component colorMsg = Component.text(color)
                        .color(TextColor.fromHexString(hexColor));

                TextReplacementConfig replacement = TextReplacementConfig.builder()
                        .match("%color%")
                        .replacement(colorMsg)
                        .build();
                Component msg = Component.text(Language.prefix + Language.getMessage("marry_heart_color")).replaceText(replacement);

                sender.sendMessage(msg);
                playerFam.setHeartColor(hexColor);
            }


        }
    }
}
