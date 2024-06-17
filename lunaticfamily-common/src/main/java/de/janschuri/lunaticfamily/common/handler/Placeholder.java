package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.FamilyPlayer;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticfamily.common.futurerequests.GetPlaceholderRequest;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.util.Objects;
import java.util.UUID;

public class Placeholder {

    public static String getPlaceholder(UUID uuid, String placeholder) {

        if (LunaticFamily.getMode() == Mode.BACKEND) {
            return new GetPlaceholderRequest().get(uuid, placeholder);
        }

        FamilyPlayer player = new FamilyPlayerImpl(uuid);

        if (placeholder.equalsIgnoreCase("status_heart")) {
            return "<" + player.getHeartColor() + ">❤";
        }

        if (placeholder.equalsIgnoreCase("heart")) {
            if (player.isMarried()) {
                return "<" + player.getHeartColor() + ">❤";
            }

            return "";
        }

        if (placeholder.equalsIgnoreCase("marriages_count")) {
            return MarriagesTable.getMarriagesCount() + "";
        }

        if (placeholder.equalsIgnoreCase("adoptions_count")) {
            return AdoptionsTable.getAdoptionsCount() + "";
        }

        if (placeholder.equalsIgnoreCase("siblinghoods_count")) {
            return SiblinghoodsTable.getSiblinghoodsCount() + "";
        }

        return null;
    }
}
