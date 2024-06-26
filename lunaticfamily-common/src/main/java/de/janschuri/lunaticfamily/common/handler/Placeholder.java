package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticfamily.common.futurerequests.GetPlaceholderRequest;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Placeholder {

    public static String getPlaceholder(UUID uuid, String placeholder) {

        if (LunaticFamily.getMode() == Mode.BACKEND) {
            return new GetPlaceholderRequest().get(uuid, placeholder);
        }

        FamilyPlayerImpl player = new FamilyPlayerImpl(uuid);

        if (player == null) {
            return null;
        }

        if (placeholder.equalsIgnoreCase("player")) {
            return player.getName();
        }

        Pattern marriagePattern = Pattern.compile("marriage_(emoji_status|partner|emoji|priest|date)");

        if (marriagePattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[1];

            if (Objects.equals(type, "emoji_status")) {
                if (player.isMarried()) {
                    return player.getMarriages().get(0).getColoredEmoji();
                }

                return "<" + LunaticFamily.getConfig().getUnadoptedEmojiColor() + ">" + Marriage.getDefaultEmoji();
            }


            Marriage marriage = player.getMarriages().get(0);

            if (marriage == null) {
                return null;
            }

            if (Objects.equals(type, "partner")) {
                return player.getPartner().getName();
            }

            if (Objects.equals(type, "emoji")) {
                return marriage.getColoredEmoji();
            }

            if (Objects.equals(type, "priest")) {
                if (marriage.getPriest() < 1) {
                    return "";
                }
                return new FamilyPlayerImpl(marriage.getPriest()).getName();
            }

            if (Objects.equals(type, "date")) {
                return marriage.getDate().toString();
            }
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

        Pattern marriagesPattern = Pattern.compile("marriages_<(\\d+)>_(player1|player2|emoji|priest|date)");

        if (marriagesPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.replaceAll("[<>]", "").split("_");
            int index = Integer.parseInt(split[1]);
            String type = split[2];

            Marriage marriage = MarriagesTable.getMarriageList(index, 1).get(0);

            if (marriage == null) {
                return null;
            }

            if (Objects.equals(type, "player1")) {
                return new FamilyPlayerImpl(marriage.getPlayer1ID()).getName();
            }

            if (Objects.equals(type, "player2")) {
                return new FamilyPlayerImpl(marriage.getPlayer2ID()).getName();
            }

            if (Objects.equals(type, "emoji")) {
                return marriage.getColoredEmoji();
            }

            if (Objects.equals(type, "priest")) {
                if (marriage.getPriest() < 1) {
                    return "";
                }
                return new FamilyPlayerImpl(marriage.getPriest()).getName();
            }

            if (Objects.equals(type, "date")) {
                return marriage.getDate().toString();
            }
        }

        Pattern adoptionsPattern = Pattern.compile("adoptions_<(\\d+)>_(parent|child|emoji|priest|date)");

        if (adoptionsPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            int index = Integer.parseInt(split[1]);
            String type = split[2];

            Adoption adoption = AdoptionsTable.getAdoptionList(index, 1).get(0);

            if (adoption == null) {
                return null;
            }

            if (Objects.equals(type, "parent")) {
                return new FamilyPlayerImpl(adoption.getParentID()).getName();
            }

            if (Objects.equals(type, "child")) {
                return new FamilyPlayerImpl(adoption.getChildID()).getName();
            }

            if (Objects.equals(type, "emoji")) {
                return adoption.getColoredEmoji();
            }

            if (Objects.equals(type, "priest")) {
                if (adoption.getPriest() < 1) {
                    return "";
                }
                return new FamilyPlayerImpl(adoption.getPriest()).getName();
            }

            if (Objects.equals(type, "date")) {
                return adoption.getDate().toString();
            }
        }

        Pattern siblinghoodsPattern = Pattern.compile("siblinghoods_<(\\d+)>_(player1|player2|emoji|priest|date)");

        if (siblinghoodsPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            int index = Integer.parseInt(split[1]);
            String type = split[2];

            Siblinghood siblinghood = SiblinghoodsTable.getSiblinghoodList(index, 1).get(0);

            if (siblinghood == null) {
                return null;
            }

            if (Objects.equals(type, "parent")) {
                return new FamilyPlayerImpl(siblinghood.getPlayer1ID()).getName();
            }

            if (Objects.equals(type, "child")) {
                return new FamilyPlayerImpl(siblinghood.getPlayer2ID()).getName();
            }

            if (Objects.equals(type, "emoji")) {
                return siblinghood.getColoredEmoji( );
            }

            if (Objects.equals(type, "priest")) {
                if (siblinghood.getPriest() < 1) {
                    return "";
                }
                return new FamilyPlayerImpl(siblinghood.getPriest()).getName();
            }

            if (Objects.equals(type, "date")) {
                return siblinghood.getDate().toString();
            }
        }

        return null;
    }
}
