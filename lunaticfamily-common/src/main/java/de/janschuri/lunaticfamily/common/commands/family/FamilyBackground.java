package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyBackground extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey setMK = new CommandMessageKey(this,"set");
    private final MessageKey backgroundMK = new MessageKey("background");


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
                getMessage(backgroundMK, false)
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

        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(player.getUniqueId());

        playerFam.setBackground(args[0]);
        sender.sendMessage(getMessage(setMK));
        playerFam.updateFamilyTree();

        return true;
    }
}
