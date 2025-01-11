package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.Database;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Adoption implements Model {

    private static final Map<Integer, Adoption> adoptions = new HashMap<>();

    private final int id;
    private final int parentID;
    private final int childID;
    private final int priest;
    private final Timestamp date;
    private final Timestamp unadoptDate;
    private String emojiColor;

    public Adoption(int id, int parentID, int childID, int priest, String emoji, Timestamp date, Timestamp unadoptDate) {
        this.id = id;
        this.parentID = parentID;
        this.childID = childID;
        this.priest = priest;
        this.emojiColor = emoji;
        this.date = date;
        this.unadoptDate = unadoptDate;
    }

    public Adoption getAdoption(int id) {
        if (adoptions.containsKey(id)) {
            return adoptions.get(id);
        }

        return AdoptionsTable.getAdoption(id);
    }

    public int getId() {
        return id;
    }

    public final int getParentID() {
        return parentID;
    }

    public final int getChildID() {
        return childID;
    }

    public final int getPriest() {
        return priest;
    }

    public final Timestamp getDate() {
        return date;
    }

    public final Timestamp getUnadoptDate() {
        return unadoptDate;
    }

    public final String getEmojiColor() {
        String color = emojiColor;
        if (color == null) {
            color = LunaticFamily.getConfig().getDefaultAdoptEmojiColor();
        }
        return color;
    }

    public final void setEmojiColor(String emojiColor) {
        this.emojiColor = emojiColor;
    }

    public final String getColoredParentEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultParentEmoji();
    }

    public final String getColoredChildEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultChildEmoji();
    }

    public static String getDefaultParentEmoji() {
        return "⌂";
    }

    public static String getDefaultChildEmoji() {
        return "☀";
    }
}