package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.futurerequests.GetPlaceholderRequest;
import de.janschuri.lunaticfamily.common.futurerequests.GetRelationalPlaceholderRequest;
import de.janschuri.lunaticfamily.common.handler.familytree.RelationAdvancement;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import static de.janschuri.lunaticfamily.common.handler.FamilyPlayer.findOrCreate;

public class Placeholder {

    public static String getPlaceholder(UUID uuid, String placeholder) {

        if (LunaticFamily.getMode() == Mode.BACKEND) {
            return new GetPlaceholderRequest().get(uuid, placeholder);
        }

        FamilyPlayer player = findOrCreate(uuid);

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

        Pattern marriagePattern = Pattern.compile("marriage_(emojiStatus|emoji|partner|priest|date|status|color)");

        if (marriagePattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[1];

            if (Objects.equals(type, "emojiStatus")) {
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

        Pattern siblinghoodPattern = Pattern.compile("siblinghood_(emojiStatus|emoji|sibling|priest|date|status|color)");

        if (siblinghoodPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[1];

            if (Objects.equals(type, "emojiStatus")) {
                if (player.hasSiblings()) {
                    return player.getSiblinghoods().get(0).getColoredEmoji();
                }

                return "<" + LunaticFamily.getConfig().getUnsiblingedEmojiColor() + ">" + Siblinghood.getDefaultEmoji();
            }

            if (Objects.equals(type, "status")) {
                return player.hasSiblings() + "";
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

        Pattern adoptionFirstChildPattern = Pattern.compile("adoptionAsParent_firstChild_(emojiStatus|emoji|child|priest|date|status|color)");

        if (adoptionFirstChildPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[2];

            if (Objects.equals(type, "emojiStatus")) {
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

        Pattern adoptionSecondChildPattern = Pattern.compile("adoptionAsParent_secondChild_(emojiStatus|emoji|child|priest|date|status|color)");

        if (adoptionSecondChildPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[2];

            if (Objects.equals(type, "emojiStatus")) {
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

        Pattern adoptionAsChildPattern = Pattern.compile("adoptionAsChild_(emojiStatus|emoji|firstParent|secondParent|priest|date|status|color)");

        if (adoptionAsChildPattern.matcher(placeholder).matches()) {
            String[] split = placeholder.split("_");
            String type = split[2];

            if (Objects.equals(type, "emojiStatus")) {
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
            int index = Integer.parseInt(split[1]);
            String type = split[2];

            Marriage marriage = DatabaseRepository.getDatabase().find(Marriage.class).setFirstRow(index - 1).setMaxRows(1).findOne();

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
                return marriage.getColoredEmoji();
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
            int index = Integer.parseInt(split[1]);
            String type = split[2];

            Adoption adoption = DatabaseRepository.getDatabase().find(Adoption.class).setFirstRow(index - 1).setMaxRows(1).findOne();

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
                return adoption.getColoredParentEmoji();
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
            int index = Integer.parseInt(split[1]);
            String type = split[2];

            Siblinghood siblinghood = DatabaseRepository.getDatabase().find(Siblinghood.class).setFirstRow(index - 1).setMaxRows(1).findOne();

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
                return siblinghood.getColoredEmoji( );
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
            return new GetRelationalPlaceholderRequest().get(uuid1, uuid2, placeholder);
        }

        FamilyPlayer player1 = findOrCreate(uuid1);
        FamilyPlayer player2 = findOrCreate(uuid2);

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
}
