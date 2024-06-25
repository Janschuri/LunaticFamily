package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.List;
import java.util.UUID;

public class MarryHeart extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noColorMK = new CommandMessageKey(this,"no_color");
    private final CommandMessageKey colorSetMK = new CommandMessageKey(this,"color_set");
    private final CommandMessageKey colorMK = new CommandMessageKey(this,"color");
    private final CommandMessageKey noMarriageMK = new CommandMessageKey(this,"no_marriage");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry.heart";
    }

    @Override
    public String getName() {
        return "heart";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
    }

    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }


        PlayerSender player = (PlayerSender) sender;
        UUID playerUUID = player.getUniqueId();
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

        if (!playerFam.isMarried()) {
            sender.sendMessage(getMessage(noMarriageMK));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("MarryHeartSubcommand: Wrong usage");
            return true;
        }

        if (!LunaticFamily.getLanguageConfig().isColorLang(args[0]) && !Utils.isValidHexCode(args[0])) {
            sender.sendMessage(getMessage(noColorMK));
            return true;
        }



        String hexColor = "";

        String color = args[0];

        String colorMsg = "";

        if (LunaticFamily.getLanguageConfig().isColorLang(args[0])) {
            String colorKey = LunaticFamily.getLanguageConfig().getColorKeyFromLang(color);
            if (player.hasPermission(getPermission() + ".color." + colorKey)){
                hexColor = LunaticFamily.getConfig().getColor(colorKey);
                colorMsg = LunaticFamily.getLanguageConfig().getColorLang(colorKey);
            } else {
                sender.sendMessage(getMessage(NO_PERMISSION_MK));
                return true;
            }
        } else {
            if (player.hasPermission(getPermission() + ".hex")) {
                hexColor = args[0];
                colorMsg = args[0];
            } else {
                sender.sendMessage(getMessage(NO_PERMISSION_MK));
                return true;
            }
        }

        TextReplacementConfig replacementConfig = TextReplacementConfig.builder().match("%color%").replacement(colorMsg).build();

        Component msg = getMessage(colorSetMK).replaceText(replacementConfig);

        player.sendMessage(msg);
        playerFam.getMarriage().setEmojiColor(hexColor);

        return true;
    }


    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(colorMK, false)
        );
    }
}
