package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import de.janschuri.lunaticlib.common.config.LunaticMessageKey;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyBackground extends FamilyCommand implements HasParams, HasParentCommand {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey setMK = new LunaticCommandMessageKey(this,"set");
    private final MessageKey backgroundMK = new LunaticMessageKey("background");


    @Override
    public String getPermission() {
        return "lunaticfamily.family.background";
    }

    @Override
    public String getName() {
        return "background";
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(backgroundMK.noPrefix())
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        List<String> backgrounds = LunaticFamily.getConfig().getBackgrounds();

        Map<String, String> backgroundParams = new HashMap<>();

        for (String background : backgrounds) {
            backgroundParams.put(background, getPermission());
        }

        return List.of(backgroundParams);
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {

        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(getMessage(helpMK));
            return true;
        }

        String backgroundArg = args[0];
        String background = "textures/block/" + backgroundArg + ".png";

        FamilyPlayer playerFam = FamilyPlayer.findOrCreate(player.getUniqueId());


        playerFam.setBackground(background);
        playerFam.save();
        sender.sendMessage(getMessage(setMK));
        playerFam.updateFamilyTree();

        return true;
    }
}
