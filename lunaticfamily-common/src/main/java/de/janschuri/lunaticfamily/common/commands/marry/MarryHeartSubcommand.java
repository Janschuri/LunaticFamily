package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.UUID;

public class MarryHeartSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "heart";
    private static final String PERMISSION = "lunaticfamily.marry.heart";

    public MarryHeartSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, LunaticFamily.getLanguageConfig().getColorLangs());
    }
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (args.length < 1) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("wrong_usage"));
                Logger.debugLog("MarryHeartSubcommand: Wrong usage");
            } else if (!LunaticFamily.getLanguageConfig().isColorLang(args[0]) && !Utils.isValidHexCode(args[0])) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_heart_no_color"));
            } else {

                String hexColor = "";

                String color = args[0];

                String colorMsg = "";

                if (LunaticFamily.getLanguageConfig().isColorLang(args[0])) {
                    String colorKey = LunaticFamily.getLanguageConfig().getColorKeyFromLang(color);
                    if (player.hasPermission(PERMISSION + ".color." + colorKey)){
                        hexColor = LunaticFamily.getConfig().getColor(colorKey);
                        colorMsg = LunaticFamily.getLanguageConfig().getColorLang(colorKey);
                    } else {
                        sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
                        return true;
                    }
                } else {
                    if (player.hasPermission(PERMISSION + ".hex")) {
                        hexColor = args[0];
                        colorMsg = args[0];
                    } else {
                        sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
                        return true;
                    }
                }

                String[] msgArray = LunaticFamily.getLanguageConfig().getMessage("marry_heart_color").split("%color%");

                Component msg =
                        LegacyComponentSerializer.legacyAmpersand().deserialize(LunaticFamily.getLanguageConfig().getPrefix() + msgArray[0])
                        .append(Component.text(colorMsg).color(TextColor.fromHexString(hexColor)))
                        .append(LegacyComponentSerializer.legacyAmpersand().deserialize(msgArray[1]));

                player.sendMessage(msg);
                playerFam.setHeartColor(hexColor);
            }


        }
        return true;
    }
}
