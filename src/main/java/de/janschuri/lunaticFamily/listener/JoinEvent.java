package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.commands.CommandSender;
import de.janschuri.lunaticFamily.commands.PlayerCommandSender;

public class JoinEvent {

    public boolean execute(CommandSender sender) {
        PlayerCommandSender playerCommandSender = (PlayerCommandSender) sender;
        playerCommandSender.onJoinEvent();
        return true;
    }
}
