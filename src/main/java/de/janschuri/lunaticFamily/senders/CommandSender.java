package de.janschuri.lunaticFamily.senders;

import de.janschuri.lunaticFamily.utils.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.utils.ClickableMessage;

import java.util.List;
import java.util.UUID;

public abstract class CommandSender {
    public CommandSender() {
    }
    public abstract boolean hasPermission(String permission);
    public abstract boolean sendMessage(String message);
    public abstract boolean sendMessage(ClickableMessage message);
    public abstract boolean sendMessage(ClickableDecisionMessage message);
    public abstract boolean sendMessage(List<ClickableMessage> msg);
    public abstract PlayerCommandSender getPlayerCommandSender(UUID uuid);
    public abstract PlayerCommandSender getPlayerCommandSender(String name);
}
