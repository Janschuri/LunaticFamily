package de.janschuri.lunaticfamily.common.commands.adopt;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdoptEmoji extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noColorMK = new CommandMessageKey(this,"no_color");
    private final CommandMessageKey colorSetMK = new CommandMessageKey(this,"color_set");
    private final CommandMessageKey noAdoptionMK = new CommandMessageKey(this,"no_adoption");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt.emoji";
    }

    @Override
    public String getName() {
        return "emoji";
    }

    @Override
    public Adopt getParentCommand() {
        return new Adopt();
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

        if (!playerFam.isAdopted()) {
            sender.sendMessage(getMessage(noAdoptionMK));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("AdoptEmoji: Wrong usage");
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


        playerFam.getAdoptionsAsChild().get(0).setEmojiColor(hexColor);
        TextReplacementConfig replacementConfig = TextReplacementConfig.builder().match("%color%").replacement(colorMsg).build();

        Component msg = getMessage(colorSetMK).replaceText(replacementConfig);

        player.sendMessage(msg);

        return true;
    }


    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(COLOR_MK, false)
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        List<Map<String, String>> list = new ArrayList<>();

        for (String color : LunaticFamily.getConfig().getColors().keySet()) {
            list.add(Map.of(
                    getPermission()+".color."+color, LunaticFamily.getLanguageConfig().getColorLang(color)
            ));
        }

        list.add(Map.of(getPermission()+".hex", "#RRGGBB"));

        return list;
    }
}
