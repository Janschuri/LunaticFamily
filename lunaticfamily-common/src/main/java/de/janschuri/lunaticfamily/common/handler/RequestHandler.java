package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.utils.Utils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RequestHandler {

    private RequestHandler() {
        // Prevent instantiation
    }

    private static final Map<String, List<Request>> requests = new HashMap<>();

    public static void send(String requestGroup, Request request) {
        requests.putIfAbsent(requestGroup, new ArrayList<>());

        request.send();

        if (request.getTimeout() > 0) {
            Runnable runnable = () -> {
                if (!request.isCompleted() && !request.isCancelled()) {
                    request.timeout();
                }
            };

            Utils.scheduleTask(runnable, request.getTimeout(), TimeUnit.SECONDS);
        }

    }

    public static void cancelAllRequests(UUID uuid) {
        //TODO: Implement this method
    }
}