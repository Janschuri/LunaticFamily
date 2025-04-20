package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.futurerequests.GetPlaceholderRequest;
import de.janschuri.lunaticfamily.common.futurerequests.GetRelationalPlaceholderRequest;
import de.janschuri.lunaticfamily.common.handler.familytree.RelationAdvancement;
import de.janschuri.lunaticlib.common.utils.Mode;
import org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import static de.janschuri.lunaticfamily.common.handler.FamilyPlayer.find;

public class Placeholder {

    public static String getPlaceholder(UUID uuid, String placeholder) {

        if (LunaticFamily.getMode() == Mode.BACKEND) {
            return new GetPlaceholderRequest().get(uuid, placeholder).thenApply(s -> s).join();
        }

        FamilyPlayer player = find(uuid);

        if (player == null) {
            return null;
        }

        if (placeholder.equalsIgnoreCase("player")) {
            return player.getName();
        }

        if (placeholder.equalsIgnoreCase("gender")) {
            return LunaticFamily.getLanguageConfig().getGenderLang(player.getGender());
        }

        if (placeholder.equalsIgnoreCase("gender_emoji")) {
            return LunaticFamily.getLanguageConfig().getGenderEmoji(player.getGender());
        }


        String coloredEmojiPattern = LunaticFamily.getConfig().getColoredEmojiPattern();

        Pattern marriagePattern = Pattern.compile("marriage_(emojiStatus|emoji|partner|priest|date|status|color)");

        if (marriagePattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[1];

            if (Objects.equals(type, "emojiStatus")) {

                if (player.isMarried()) {
                    Marriage marriage = player.getMarriage();
                        String color = marriage.getEmojiColor();

                        return coloredEmojiPattern
                                .replaceAll("%hexcolor%", color)
                                .replaceAll("%emoji%", Marriage.getDefaultEmoji());
                } else {
                    String color = LunaticFamily.getConfig().getUnmarriedEmojiColor();

                    return coloredEmojiPattern
                            .replaceAll("%hexcolor%", color)
                            .replaceAll("%emoji%", Marriage.getDefaultEmoji());
                }
            }

            if (Objects.equals(type, "status")) {
                return player.isMarried() + "";
            }


            if (!player.isMarried()) {
                return "";
            }

            List<Marriage> marriages = player.getMarriages();
            Marriage marriage = marriages.get(0);

            if (Objects.equals(type, "partner")) {
                return player.getPartner().getName();
            }

            if (Objects.equals(type, "emoji")) {
                String color = marriage.getEmojiColor();

                return coloredEmojiPattern
                        .replaceAll("%hexcolor%", color)
                        .replaceAll("%emoji%", Marriage.getDefaultEmoji());
            }

            if (Objects.equals(type, "priest")) {
                if (marriage.getPriest() != null) {
                    return "";
                }
                return marriage.getPriest().getName();
            }

            if (Objects.equals(type, "date")) {
                return marriage.getDate().toString();
            }

            if (Objects.equals(type, "color")) {
                return marriage.getEmojiColor();
            }
        }

        Pattern siblinghoodPattern = Pattern.compile("siblinghood_(?:<\\d+>_)?(emojiStatus|emoji|sibling|priest|date|status|color)");

        if (siblinghoodPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");

            int index = getIndex(split[1]);

            int amount = index + 1;

            String type = split[1];

            if (Objects.equals(type, "emojiStatus")) {

                if (player.hasSiblings(amount)) {
                     Siblinghood siblinghood = player.getSiblinghoods().get(index);
                        String color = siblinghood.getEmojiColor();

                        return coloredEmojiPattern
                                .replaceAll("%hexcolor%", color)
                                .replaceAll("%emoji%", Siblinghood.getDefaultEmoji());
                } else {
                    String color = LunaticFamily.getConfig().getUnmarriedEmojiColor();

                    return coloredEmojiPattern
                            .replaceAll("%hexcolor%", color)
                            .replaceAll("%emoji%", Siblinghood.getDefaultEmoji());
                }
            }

            if (Objects.equals(type, "status")) {
                return player.hasSiblings() + "";
            }

            if (!player.hasSiblings(amount)) {
                return "";
            }

            List<Siblinghood> siblinghoods = player.getSiblinghoods();
            Siblinghood siblinghood = siblinghoods.get(index);

            if (Objects.equals(type, "sibling")) {
                return player.getSibling().getName();
            }

            if (Objects.equals(type, "emoji")) {
                String color = siblinghood.getEmojiColor();

                return coloredEmojiPattern
                        .replaceAll("%hexcolor%", color)
                        .replaceAll("%emoji%", Siblinghood.getDefaultEmoji());
            }

            if (Objects.equals(type, "priest")) {
                if (siblinghood.getPriest() == null) {
                    return "";
                }
                return siblinghood.getPriest().getName();
            }

            if (Objects.equals(type, "date")) {
                return siblinghood.getDate().toString();
            }

            if (Objects.equals(type, "color")) {
                return siblinghood.getEmojiColor();
            }
        }

        Pattern adoptionAsParentPattern = Pattern.compile("adoptionAsParent_(?:<\\d+>_)?(emojiStatus|emoji|child|priest|date|status|color)");

        if (adoptionAsParentPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");

            int index = getIndex(split[1]);

            int amount = index + 1;

            String type = split[2];

            if (Objects.equals(type, "emojiStatus")) {
                if (player.hasChildren(amount)) {
                    Adoption adoption = player.getAdoptionsAsParent().get(index);
                        String color = adoption.getEmojiColor();

                        return coloredEmojiPattern
                                .replaceAll("%hexcolor%", color)
                                .replaceAll("%emoji%", Adoption.getDefaultParentEmoji());
                } else {
                    String color = LunaticFamily.getConfig().getUnparentEmojiColor();

                    return coloredEmojiPattern
                            .replaceAll("%hexcolor%", color)
                                .replaceAll("%emoji%", Adoption.getDefaultChildEmoji());
                }
            }

            if (Objects.equals(type, "status")) {
                return player.hasChildren(amount) + "";
            }

            if (!player.hasChildren(amount)) {
                return "";
            }

            List<Adoption> adoptions = player.getAdoptionsAsParent();
            Adoption adoption = adoptions.get(index);

            if (Objects.equals(type, "child")) {
                return player.getChildren().get(0).getName();
            }

            if (Objects.equals(type, "emoji")) {
                String color = adoption.getEmojiColor();

                return coloredEmojiPattern
                        .replaceAll("%hexcolor%", color)
                        .replaceAll("%emoji%", Adoption.getDefaultParentEmoji());
            }

            if (Objects.equals(type, "priest")) {
                if (adoption.hasPriest()) {
                    return "";
                }
                return adoption.getPriest().getName();
            }

            if (Objects.equals(type, "date")) {
                return adoption.getDate().toString();
            }

            if (Objects.equals(type, "color")) {
                return adoption.getEmojiColor();
            }
        }

        Pattern adoptionAsChildPattern = Pattern.compile("adoptionAsChild_(?:<\\d+>_)?(emojiStatus|emoji|firstParent|secondParent|priest|date|status|color)");

        if (adoptionAsChildPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");

            int index = getIndex(split[1]);

            String type = split[2];

            if (Objects.equals(type, "firstParent")) {
                index = 0;
            }

            if (Objects.equals(type, "secondParent")) {
                index = 1;
            }

            if (Objects.equals(type, "emojiStatus")) {
                if (player.isAdopted()) {
                    Adoption adoption = player.getAdoptionsAsChild().get(index);
                    String color = adoption.getEmojiColor();

                    return coloredEmojiPattern
                            .replaceAll("%hexcolor%", color)
                            .replaceAll("%emoji%", Adoption.getDefaultChildEmoji());
                } else {
                    String color = LunaticFamily.getConfig().getUnparentEmojiColor();

                    return coloredEmojiPattern
                            .replaceAll("%hexcolor%", color)
                            .replaceAll("%emoji%", Adoption.getDefaultChildEmoji());
                }
            }

            if (Objects.equals(type, "status")) {
                return player.isAdopted() + "";
            }

            if (!player.isAdopted()) {
                return "";
            }

            List<Adoption> adoptions = player.getAdoptionsAsParent();
            Adoption adoption = adoptions.get(index);

            if (Objects.equals(type, "firstParent")) {
                return adoption.getParent().getName();
            }

            if (Objects.equals(type, "secondParent")) {
                return adoption.getParent().getName();
            }

            if (Objects.equals(type, "emoji")) {
                String color = adoption.getEmojiColor();

                return coloredEmojiPattern
                        .replaceAll("%hexcolor%", color)
                        .replaceAll("%emoji%", Adoption.getDefaultChildEmoji());
            }

            if (Objects.equals(type, "priest")) {
                if (adoption.hasPriest()) {
                    return "";
                }
                return adoption.getPriest().getName();
            }

            if (Objects.equals(type, "date")) {
                return adoption.getDate().toString();
            }

            if (Objects.equals(type, "color")) {
                return adoption.getEmojiColor();
            }
        }

        if (placeholder.equalsIgnoreCase("marriages_count")) {
            return DatabaseRepository.getDatabase().find(Marriage.class).where().isNull("divorceDate").findCount() + "";
        }

        if (placeholder.equalsIgnoreCase("adoptions_count")) {
            return DatabaseRepository.getDatabase().find(Adoption.class).where().isNull("unadoptDate").findCount() + "";
        }

        if (placeholder.equalsIgnoreCase("siblinghoods_count")) {
            return DatabaseRepository.getDatabase().find(Siblinghood.class).where().isNull("unsiblingDate").findCount() + "";
        }

        Pattern marriagesPattern = Pattern.compile("marriages_<(\\d+)>_(player1|player2|emoji|priest|date)");

        if (marriagesPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.replaceAll("[<>]", "").split("_");
            int index = getIndex(split[1]);
            String type = split[2];

            Marriage marriage = DatabaseRepository.getDatabase().find(Marriage.class).setFirstRow(index).setMaxRows(1).findOne();

            if (marriage == null) {
                return null;
            }

            if (Objects.equals(type, "player1")) {
                return marriage.getPlayer1().getName();
            }

            if (Objects.equals(type, "player2")) {
                return marriage.getPlayer2().getName();
            }

            if (Objects.equals(type, "emoji")) {
                String color = marriage.getEmojiColor();

                return coloredEmojiPattern
                        .replaceAll("%hexcolor%", color)
                        .replaceAll("%emoji%", Marriage.getDefaultEmoji());
            }

            if (Objects.equals(type, "priest")) {
                if (marriage.getPriest() == null) {
                    return "";
                }
                return marriage.getPriest().getName();
            }

            if (Objects.equals(type, "date")) {
                return marriage.getDate().toString();
            }
        }

        Pattern adoptionsPattern = Pattern.compile("adoptions_<(\\d+)>_(parent|child|emoji|priest|date)");

        if (adoptionsPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            int index = getIndex(split[1]);
            String type = split[2];

            Adoption adoption = DatabaseRepository.getDatabase().find(Adoption.class).setFirstRow(index).setMaxRows(1).findOne();

            if (adoption == null) {
                return null;
            }

            if (Objects.equals(type, "parent")) {
                return adoption.getParent().getName();
            }

            if (Objects.equals(type, "child")) {
                return adoption.getChild().getName();
            }

            if (Objects.equals(type, "emoji")) {
                String color = adoption.getEmojiColor();

                return coloredEmojiPattern
                        .replaceAll("%hexcolor%", color)
                        .replaceAll("%emoji%", Adoption.getDefaultChildEmoji());
            }

            if (Objects.equals(type, "priest")) {
                if (adoption.hasPriest()) {
                    return "";
                }
                return adoption.getPriest().getName();
            }

            if (Objects.equals(type, "date")) {
                return adoption.getDate().toString();
            }
        }

        Pattern siblinghoodsPattern = Pattern.compile("siblinghoods_<(\\d+)>_(player1|player2|emoji|priest|date)");

        if (siblinghoodsPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");

            int index = getIndex(split[1]);
            String type = split[2];

            Siblinghood siblinghood = DatabaseRepository.getDatabase().find(Siblinghood.class).setFirstRow(index).setMaxRows(1).findOne();

            if (siblinghood == null) {
                return null;
            }

            if (Objects.equals(type, "parent")) {
                return siblinghood.getPlayer1().getName();
            }

            if (Objects.equals(type, "child")) {
                return siblinghood.getPlayer2().getName();
            }

            if (Objects.equals(type, "emoji")) {
                String color = siblinghood.getEmojiColor();

                return coloredEmojiPattern
                        .replaceAll("%hexcolor%", color)
                        .replaceAll("%emoji%", Siblinghood.getDefaultEmoji());
            }

            if (Objects.equals(type, "priest")) {
                if (siblinghood.getPriest() == null) {
                    return "";
                }
                return siblinghood.getPriest().getName();
            }

            if (Objects.equals(type, "date")) {
                return siblinghood.getDate().toString();
            }
        }

        return null;
    }

    public static String getPlaceholder(UUID uuid1, UUID uuid2, String placeholder) {

        if (LunaticFamily.getMode() == Mode.BACKEND) {
            return new GetRelationalPlaceholderRequest().get(uuid1, uuid2, placeholder).thenApply(s -> s).join();
        }

        FamilyPlayer player1 = find(uuid1);
        FamilyPlayer player2 = find(uuid2);

        if (placeholder.equalsIgnoreCase("relation")) {


            if (player1.isFamilyMember(player2)) {
                List<RelationAdvancement> relationAdvancements = player1.getFamilyTree().getRelationAdvancements();
                String relation = relationAdvancements.stream()
                        .filter(relationAdvancement -> relationAdvancement.getId() == player2.getId())
                        .findFirst()
                        .map(RelationAdvancement::getDescription)
                        .orElse("");
                return LunaticFamily.getLanguageConfig().getRelation(relation, player2.getGender());
            } else {
                return "";
            }
        }

        return null;
    }

    private static int getIndex(String indexString) {
        indexString = indexString.replaceAll("[<>]", "");
        int index;
        try {
            index = Integer.parseInt(indexString);
            index--;
        } catch (NumberFormatException e) {
            return 0;
        }

        if (index < 0) {
            index = 0;
        }
        return index;
    }
}
