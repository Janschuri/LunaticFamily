package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;

import java.util.*;

public class MarryEmoji extends FamilyCommand implements HasParentCommand, HasParams {

    private static final MarryEmoji INSTANCE = new MarryEmoji();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% <%param%> &7- Change the color of your heart in the marriage list.");
    private static final CommandMessageKey NO_COLOR_MK = new LunaticCommandMessageKey(INSTANCE, "no_color")
            .defaultMessage("en", "You must specify a color.");
    private static final CommandMessageKey COLOR_SET_MK = new LunaticCommandMessageKey(INSTANCE, "color_set")
            .defaultMessage("en", "You have chosen the color %color%.");
    private static final CommandMessageKey NO_MARRIAGE_MK = new LunaticCommandMessageKey(INSTANCE, "no_marriage")
            .defaultMessage("en", "You are not married!");
    private static final CommandMessageKey HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "header")
            .defaultMessage("en", "Available colors:");



    @Override
    public String getPermission() {
        return "lunaticfamily.marry.emoji";
    }

    @Override
    public String getName() {
        return "emoji";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
    }

    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }


        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

        if (!playerFam.isMarried()) {
            sender.sendMessage(getMessage(NO_MARRIAGE_MK));
            return true;
        }

        if (args.length < 1) {
            ComponentBuilder builder = Component.text();
            builder.append(getMessage(HEADER_MK));
            builder.append(Component.newline());

            for (String color : getParams().get(0).keySet()) {
                if (color.startsWith("#")) {
                    continue;
                }
                String colorLang = LunaticFamily.getLanguageConfig().getColorLang(color);

                ComponentBuilder component = Component.text()
                        .append(Component.text(colorLang)
                                .color(TextColor.fromHexString(LunaticFamily.getConfig().getColor(color)))
                                .clickEvent(ClickEvent.runCommand("/family marry emoji " + color))
                        );

                builder.append(component);
                builder.append(Component.space());
            }

            player.sendMessage(builder.build());
            return true;
        }

        String color = args[0];

        if (!LunaticFamily.getConfig().getColors().containsKey(color) && !Utils.isValidHexCode(color)) {
            sender.sendMessage(getMessage(NO_COLOR_MK));
            return true;
        }



        String hexColor = "";

        String colorMsg = "";

        if (LunaticFamily.getConfig().getColors().containsKey(color)) {
            if (player.hasPermission(getPermission() + ".color." + color)){
                hexColor = LunaticFamily.getConfig().getColor(color);
                colorMsg = LunaticFamily.getLanguageConfig().getColorLang(color);
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

        Component colorComponent = Component.text().content(colorMsg).color(TextColor.fromHexString(hexColor)).build();

        TextReplacementConfig replacementConfig = TextReplacementConfig.builder().match("%color%").replacement(colorComponent).build();

        Component msg = getMessage(COLOR_SET_MK).replaceText(replacementConfig);

        player.sendMessage(msg);
        playerFam.getMarriages().get(0).setEmojiColor(hexColor);

        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }


    @Override
    public List<MessageKey> getParamsNames() {
        return List.of(
                COLOR_MK
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        for (String color : LunaticFamily.getConfig().getColors().keySet()) {
            map.put(
                    color , getPermission()+".color."+color
            );
        }

        map.put("#RRGGBB",getPermission()+".hex");

        list.add(map);
        return list;
    }
}
