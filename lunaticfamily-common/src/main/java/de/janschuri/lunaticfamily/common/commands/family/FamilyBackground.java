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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyBackground extends FamilyCommand implements HasParams, HasParentCommand {

    private static final FamilyBackground INSTANCE = new FamilyBackground();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", INSTANCE.getDefaultHelpMessage("Change the background of your family tree."))
            .defaultMessage("de", INSTANCE.getDefaultHelpMessage("Ändere den Hintergrund deines Familienstammbaums."));
    private static final CommandMessageKey SET_MK = new LunaticCommandMessageKey(INSTANCE, "set")
            .defaultMessage("en", "Background has been set.")
            .defaultMessage("de", "Hintergrund wurde gesetzt.");


    @Override
    public String getPermission() {
        return "lunaticfamily.family.background";
    }

    @Override
    public String getName() {
        return "background";
    }

    @Override
    public List<MessageKey> getParamsNames() {
        return List.of(
                BACKGROUND_MK
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
            sender.sendMessage(getReplacedHelpMessage(HELP_MK, sender, this));
            return true;
        }

        String backgroundArg = args[0];
        String background = "textures/block/" + backgroundArg + ".png";

        FamilyPlayer playerFam = FamilyPlayer.find(player.getUniqueId());


        playerFam.setBackground(background);
        playerFam.save();
        sender.sendMessage(getMessage(SET_MK));
        playerFam.updateFamilyTree();

        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
