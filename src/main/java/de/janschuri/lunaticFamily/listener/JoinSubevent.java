package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;

public class JoinSubevent {

    public boolean execute(CommandSender sender) {
        PlayerCommandSender playerCommandSender = (PlayerCommandSender) sender;
        playerCommandSender.onJoinEvent();
        return true;
    }
}
