package de.janschuri.lunaticfamily.common.listener;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.marry.MarryKiss;
import de.janschuri.lunaticfamily.common.futurerequests.PlayerInteractsWithPlayerRequest;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.Placeholder;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.command.LunaticPlaceholder;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import de.janschuri.lunaticlib.common.config.LunaticMessageKey;
import de.janschuri.lunaticlib.common.utils.Mode;
import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class PlayerInteractsWithPlayerExecuter {

    private static final MarryKiss MARRY_KISS_CMD = new MarryKiss();

    private static final CommandMessageKey KISS_MK = new LunaticCommandMessageKey(MARRY_KISS_CMD, "kiss")
            .defaultMessage("en", "You have kissed %player%.")
            .defaultMessage("de", "Du hast %player% geküsst.");
    private static final CommandMessageKey GOT_KISSED_MK = new LunaticCommandMessageKey(MARRY_KISS_CMD, "got_kissed")
            .defaultMessage("en", "%player% has kissed you.")
            .defaultMessage("de", "%player% hat dich geküsst.");

    protected static final MessageKey NO_PERMISSION_MK = new LunaticMessageKey("no_permission")
            .defaultMessage("en", "You do not have permission to do this.")
            .defaultMessage("de", "Du hast keine Berechtigung, das zu tun.");

    private static final List<UUID> kissCooldown = new ArrayList<>();

    private PlayerInteractsWithPlayerExecuter() {}

    public static boolean execute(PlayerSender clickingPlayer, PlayerSender clickedPlayer, boolean clickingPlayerIsSneaking) {
        if (LunaticFamily.getMode() == Mode.BACKEND) {
            return new PlayerInteractsWithPlayerRequest().get(clickingPlayer, clickedPlayer, clickingPlayerIsSneaking).thenApply(success -> success).join();
        }


        if (!clickingPlayer.isOnline()) {
            return false;
        }

        if (!clickedPlayer.isOnline()) {
            return false;
        }

        FamilyPlayer clickingPlayerFam = FamilyPlayer.find(clickingPlayer.getUniqueId());
        FamilyPlayer clickedPlayerFam = FamilyPlayer.find(clickedPlayer.getUniqueId());

        if (clickingPlayerFam == null || clickedPlayerFam == null) {
            return false;
        }

        if (clickingPlayerFam.isMarriedTo(clickedPlayerFam) && clickingPlayerIsSneaking) {
            return performKiss(clickingPlayer, clickedPlayer);
        }

        return true;
    }

    public static boolean performKiss(PlayerSender clickingPlayer, PlayerSender clickedPlayer) {
        if (kissCooldown.contains(clickingPlayer.getUniqueId())) {
            return true;
        }

        if (!clickingPlayer.hasPermission("lunaticfamily.marry.kiss")) {
            clickingPlayer.sendMessage(getMessage(NO_PERMISSION_MK));
            return false;
        }

        UUID playerUUID = clickingPlayer.getUniqueId();

        double[] playerPosition = clickingPlayer.getPosition();
        double[] partnerPosition = clickedPlayer.getPosition();
        double[] position = Utils.getPositionBetweenLocations(playerPosition, partnerPosition);
        position[1] += 2;
        for (int i = 0; i < 6; i++) {

            Runnable runnable = () -> {
                Utils.spawnParticleCloud(playerUUID, position, "HEART");
            };

            Utils.scheduleTask(runnable, i * 250L, TimeUnit.MILLISECONDS);
        }

        kissCooldown.add(playerUUID);

        Runnable runnable = () -> {
            kissCooldown.remove(playerUUID);
        };

        Utils.scheduleTask(runnable, 2000L, TimeUnit.MILLISECONDS);

        clickingPlayer.sendMessage(getMessage(KISS_MK,
                placeholder("%player%", clickingPlayer.getName())));
        clickedPlayer.sendMessage(getMessage(GOT_KISSED_MK,
                placeholder("%player%", clickedPlayer.getName())));

        return true;
    }


    private static Component getMessage(MessageKey key, Placeholder... placeholders) {
        return LunaticFamily.getLanguageConfig().getMessage(key, placeholders);
    }

    private static Placeholder placeholder(String key, String value) {
        return new LunaticPlaceholder(key, Component.text(value));
    }
}
