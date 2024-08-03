package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticfamily.common.futurerequests.GetPlaceholderRequest;
import de.janschuri.lunaticfamily.common.futurerequests.GetRelationalPlaceholderRequest;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.util.List;
import java.util.Map;
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

        if (placeholder.equalsIgnoreCase("gender")) {
            return LunaticFamily.getLanguageConfig().getGenderLang(player.getGender());
        }

        if (placeholder.equalsIgnoreCase("gender_emoji")) {
            return LunaticFamily.getLanguageConfig().getGenderEmoji(player.getGender());
        }

        Pattern marriagePattern = Pattern.compile("marriage_(emoji_status|emoji|partner|priest|date|status|color)");

        if (marriagePattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[1];

            if (Objects.equals(type, "emoji_status")) {
                if (player.isMarried()) {
                    return player.getMarriages().get(0).getColoredEmoji();
                }

                return "<" + LunaticFamily.getConfig().getUnmarriedEmojiColor() + ">" + Marriage.getDefaultEmoji();
            }

            if (Objects.equals(type, "status")) {
                return player.isMarried() + "";
            }


            List<Marriage> marriages = player.getMarriages();

            if (marriages.isEmpty()) {
                return "";
            }

            Marriage marriage = marriages.get(0);

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

            if (Objects.equals(type, "color")) {
                return marriage.getEmojiColor();
            }
        }

        Pattern siblinghoodPattern = Pattern.compile("siblinghood_(emoji_status|emoji|sibling|priest|date|status|color)");

        if (siblinghoodPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[1];

            if (Objects.equals(type, "emoji_status")) {
                if (player.hasSibling()) {
                    return player.getSiblinghoods().get(0).getColoredEmoji();
                }

                return "<" + LunaticFamily.getConfig().getUnsiblingedEmojiColor() + ">" + Siblinghood.getDefaultEmoji();
            }

            if (Objects.equals(type, "status")) {
                return player.hasSibling() + "";
            }


            List<Siblinghood> siblinghoods = player.getSiblinghoods();

            if (siblinghoods.isEmpty()) {
                return "";
            }

            Siblinghood siblinghood = siblinghoods.get(0);

            if (Objects.equals(type, "sibling")) {
                return player.getSibling().getName();
            }

            if (Objects.equals(type, "emoji")) {
                return siblinghood.getColoredEmoji();
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

            if (Objects.equals(type, "color")) {
                return siblinghood.getEmojiColor();
            }
        }

        Pattern adoptionFirstChildPattern = Pattern.compile("adoptionAsParent_firstChild_(emoji_status|emoji|child|priest|date|status|color)");

        if (adoptionFirstChildPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[1];

            if (Objects.equals(type, "emoji_status")) {
                if (player.hasChildren()) {
                    return player.getAdoptionsAsParent().get(0).getColoredParentEmoji();
                }

                return "<" + LunaticFamily.getConfig().getUnadoptedEmojiColor() + ">" + Adoption.getDefaultParentEmoji();
            }

            if (Objects.equals(type, "status")) {
                return player.hasChildren() + "";
            }

            List<Adoption> adoptions = player.getAdoptionsAsParent();

            if (adoptions.isEmpty()) {
                return "";
            }

            Adoption adoption = adoptions.get(0);

            if (Objects.equals(type, "child")) {
                return player.getChildren().get(0).getName();
            }

            if (Objects.equals(type, "emoji")) {
                return adoptions.get(0).getColoredParentEmoji();
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

            if (Objects.equals(type, "color")) {
                return adoption.getEmojiColor();
            }
        }

        Pattern adoptionSecondChildPattern = Pattern.compile("adoptionAsParent_secondChild_(emoji_status|emoji|child|priest|date|status|color)");

        if (adoptionSecondChildPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[1];

            if (Objects.equals(type, "emoji_status")) {
                if (player.getChildrenAmount() > 1) {
                    return player.getAdoptionsAsParent().get(1).getColoredParentEmoji();
                }

                return "<" + LunaticFamily.getConfig().getUnparentEmojiColor() + ">" + Adoption.getDefaultParentEmoji();
            }

            if (Objects.equals(type, "status")) {
                return (player.getChildrenAmount() > 1) + "";
            }

            List<Adoption> adoptions = player.getAdoptionsAsParent();

            if (adoptions.size() < 2) {
                return "";
            }

            Adoption adoption = adoptions.get(1);

            if (Objects.equals(type, "child")) {
                return player.getChildren().get(1).getName();
            }

            if (Objects.equals(type, "emoji")) {
                return adoptions.get(0).getColoredParentEmoji();
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

            if (Objects.equals(type, "color")) {
                return adoption.getEmojiColor();
            }
        }

        Pattern adoptionAsChildPattern = Pattern.compile("adoptionAsChild_(emoji_status|emoji|firstParent|secondParent|priest|date|status|color)");

        if (adoptionAsChildPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[1];

            if (Objects.equals(type, "emoji_status")) {
                if (player.isAdopted()) {
                    return player.getAdoptionsAsChild().get(0).getColoredChildEmoji();
                }

                return "<" + LunaticFamily.getConfig().getUnadoptedEmojiColor() + ">" + Adoption.getDefaultChildEmoji();
            }

            if (Objects.equals(type, "status")) {
                return player.isAdopted() + "";
            }


            List<Adoption> adoptions = player.getAdoptionsAsParent();

            if (adoptions.size() < 2) {
                return "";
            }

            Adoption adoption = adoptions.get(1);

            if (Objects.equals(type, "firstParent")) {
                return player.getParents().get(0).getName();
            }

            if (Objects.equals(type, "secondParent")) {
                return player.getParents().get(1).getName();
            }

            if (Objects.equals(type, "emoji")) {
                return adoptions.get(0).getColoredChildEmoji();
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

            if (Objects.equals(type, "color")) {
                return adoption.getEmojiColor();
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
                return adoption.getColoredParentEmoji();
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

    public static String getPlaceholder(UUID uuid1, UUID uuid2, String placeholder) {

        if (LunaticFamily.getMode() == Mode.BACKEND) {
            return new GetRelationalPlaceholderRequest().get(uuid1, uuid2, placeholder);
        }

        FamilyPlayerImpl player1 = new FamilyPlayerImpl(uuid1);
        FamilyPlayerImpl player2 = new FamilyPlayerImpl(uuid2);

        if (placeholder.equalsIgnoreCase("relation")) {


            if (player1.isFamilyMember(player2.getId())) {
                Map<Integer, String> familyMap = player1.getFamilyMap();
                String relation = familyMap.entrySet().stream()
                        .filter(e -> e.getKey().equals(player2.getId()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse("");
                return LunaticFamily.getLanguageConfig().getRelation(relation, player2.getGender());
            } else {
                return "";
            }
        }

        return null;
    }
}
