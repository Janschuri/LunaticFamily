package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Request {

    private Map<String, List<PlayerSender>> players = new HashMap<>();
    private Predicate<Request> onRequest = (request) -> true;
    private BiPredicate<Request, String> onAccept = (request, groupKey) -> true;
    private BiPredicate<Request, String> onDeny = (request, groupKey) -> true;
    private Predicate<Request> onTimeout = (request) -> true;
    private Predicate<Request> onCancel = (request) -> true;
    private Predicate<Request> onComplete = (request) -> true;

    private final PlayerSender requester;
    private Map<String, List<PlayerSender>> currentRequested = new HashMap<>();
    private Map<String, List<PlayerSender>> accepted = new HashMap<>();
    private Map<String, List<PlayerSender>> denied = new HashMap<>();

    private boolean completed = false;
    private boolean cancelled = false;
    private boolean timedOut = false;

    private long timeout = -1L;


    protected Map<String, List<PlayerSender>> playerToAccept = new HashMap<>();

    public Request(PlayerSender requester) {
        this.requester = requester;
    }

    public Request players(String groupKey, List<PlayerSender> players) {
        this.players.put(groupKey, players);
        this.playerToAccept.put(groupKey, players);
        return this;
    }

    public void accept(String groupKey, PlayerSender player) {
        if (!this.players.containsKey(groupKey)) {
            Logger.errorLog("Cannot accept player " + player.getName() + " for group " + groupKey + " because the group does not exist.");
            return;
        }

        if (!this.players.get(groupKey).contains(player)) {
            Logger.errorLog("Cannot accept player " + player.getName() + " for group " + groupKey + " because the player is not in the group.");
            return;
        }

        if (!this.playerToAccept.containsKey(groupKey) || !this.playerToAccept.get(groupKey).contains(player)) {
            Logger.errorLog("Cannot accept player " + player.getName() + " for group " + groupKey + " because the player already accepted or denied the request.");
            return;
        }

        this.playerToAccept.get(groupKey).remove(player);

        this.accepted.get(groupKey).add(player);
        onAccept.test(this, groupKey);

        if (this.playerToAccept.get(groupKey).isEmpty()) {
            this.playerToAccept.remove(groupKey);
        }

        if (this.playerToAccept.isEmpty()) {
            onComplete.test(this);
        }
    }

    public void deny(String groupKey, PlayerSender player) {
        if (!this.players.containsKey(groupKey)) {
            Logger.errorLog("Cannot deny player " + player.getName() + " for group " + groupKey + " because the group does not exist.");
            return;
        }

        if (!this.players.get(groupKey).contains(player)) {
            Logger.errorLog("Cannot deny player " + player.getName() + " for group " + groupKey + " because the player is not in the group.");
            return;
        }

        if (!this.playerToAccept.containsKey(groupKey) || !this.playerToAccept.get(groupKey).contains(player)) {
            Logger.errorLog("Cannot deny player " + player.getName() + " for group " + groupKey + " because the player already accepted or denied the request.");
            return;
        }

        this.playerToAccept.get(groupKey).remove(player);

        this.denied.get(groupKey).add(player);
        onDeny.test(this, groupKey);

        if (this.playerToAccept.isEmpty()) {
            this.cancel();
        }
    }

    public void send() {
        onRequest.test(this);
    }

    public Request setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public Request onRequest(Predicate<Request> onRequest) {
        this.onRequest = onRequest;
        return this;
    }

    public Request onAccept(BiPredicate<Request, String> onAccept) {
        this.onAccept = onAccept;
        return this;
    }

    public Request onDeny(BiPredicate<Request, String> onDeny) {
        this.onDeny = onDeny;
        return this;
    }

    public Request onTimeout(Predicate<Request> onTimeout) {
        this.onTimeout = onTimeout;
        return this;
    }

    public Request onCancel(Predicate<Request> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public Request onComplete(Predicate<Request> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public void cancel() {
        this.cancelled = true;
        onCancel.test(this);
    }

    public void timeout() {
        this.timedOut = true;
        onTimeout.test(this);
    }

    public PlayerSender getRequester() {
        return requester;
    }

    public List<PlayerSender> getCurrentRequested(String groupKey) {
        if (!currentRequested.containsKey(groupKey)) {
            Logger.errorLog("Cannot get current requested player for group " + groupKey + " because the group does not exist.");
            return null;
        }

        return currentRequested.get(groupKey);
    }

    public PlayerSender getLastAccepted(String groupKey) {
        if (!accepted.containsKey(groupKey)) {
            Logger.errorLog("Cannot get last accepted player for group " + groupKey + " because the group does not exist.");
            return null;
        }

        return accepted.get(groupKey).get(accepted.get(groupKey).size() - 1);
    }

    public PlayerSender getLastDenied(String groupKey) {
        if (!denied.containsKey(groupKey)) {
            Logger.errorLog("Cannot get last denied player for group " + groupKey + " because the group does not exist.");
            return null;
        }

        return denied.get(groupKey).get(denied.get(groupKey).size() - 1);
    }

    public List<PlayerSender> getPlayers(String groupKey) {
        if (!players.containsKey(groupKey)) {
            Logger.errorLog("Cannot get players for group " + groupKey + " because the group does not exist.");
            return null;
        }

        return players.get(groupKey);
    }

    public boolean isPlayerInvolved(PlayerSender player) {
        for (List<PlayerSender> group : players.values()) {
            if (group.contains(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerInvolved(String groupKey, PlayerSender player) {
        if (!players.containsKey(groupKey)) {
            Logger.errorLog("Cannot check if player " + player.getName() + " is involved in group " + groupKey + " because the group does not exist.");
            return false;
        }

        return players.get(groupKey).contains(player);
    }

    public boolean isCurrentlyRequested(String groupKey, PlayerSender player) {
        if (!currentRequested.containsKey(groupKey)) {
            Logger.errorLog("Cannot check if player " + player.getName() + " is currently requested in group " + groupKey + " because the group does not exist.");
            return false;
        }

        return currentRequested.get(groupKey).contains(player);
    }

    public long getTimeout() {
        return timeout;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}