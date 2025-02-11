package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.Adoption;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;

import java.util.*;

public class AdoptEmoji extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noColorMK = new CommandMessageKey(this,"no_color");
    private final CommandMessageKey colorSetMK = new CommandMessageKey(this,"color_set");
    private final CommandMessageKey noAdoptionMK = new CommandMessageKey(this,"no_adoption");
    private final CommandMessageKey headerMK = new CommandMessageKey(this,"header");


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
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

        if (!playerFam.isAdopted()) {
            sender.sendMessage(getMessage(noAdoptionMK));
            return true;
        }

        if (args.length < 1) {
            ComponentBuilder builder = Component.text();
            builder.append(getMessage(headerMK));
            builder.append(Component.newline());

            for (String color : getParams().get(0).keySet()) {
                if (color.startsWith("#")) {
                    continue;
                }
                String colorLang = LunaticFamily.getLanguageConfig().getColorLang(color);

                ComponentBuilder component = Component.text()
                        .append(Component.text(colorLang)
                                .color(TextColor.fromHexString(LunaticFamily.getConfig().getColor(color)))
                                .clickEvent(ClickEvent.runCommand("/family adopt emoji " + color))
                        );

                builder.append(component);
                builder.append(Component.space());
            }

            player.sendMessage(builder.build());
            return true;
        }

        String color = args[0];

        if (!LunaticFamily.getConfig().getColors().containsKey(color) && !Utils.isValidHexCode(args[0])) {
            sender.sendMessage(getMessage(noColorMK));
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



        for (Adoption adoption : playerFam.getAdoptionsAsChild()) {
            adoption.setEmojiColor(hexColor);
            adoption.save();
        }

        TextReplacementConfig replacementConfig = TextReplacementConfig.builder().match("%color%").replacement(colorComponent).build();

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
