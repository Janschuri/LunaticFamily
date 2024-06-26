package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;

import java.sql.Timestamp;

public class Adoption {

    private final int id;
    private final int parentID;
    private final int childID;
    private final int priest;
    private final String emoji;
    private final Timestamp date;
    private final Timestamp unadoptDate;

    public Adoption(int id) {
        Adoption adoption = AdoptionsTable.getAdoption(id);
        assert adoption != null;
        this.id = adoption.id;
        this.parentID = adoption.parentID;
        this.childID = adoption.childID;
        this.priest = adoption.priest;
        this.emoji = adoption.emoji;
        this.date = adoption.date;
        this.unadoptDate = adoption.unadoptDate;
    }

    public Adoption(int id, int parentID, int childID, int priest, String emoji, Timestamp date, Timestamp unadoptDate) {
        this.id = id;
        this.parentID = parentID;
        this.childID = childID;
        this.priest = priest;
        this.emoji = emoji;
        this.date = date;
        this.unadoptDate = unadoptDate;
    }

    public int getParentID() {
        return parentID;
    }

    public int getChildID() {
        return childID;
    }

    public int getPriest() {
        return priest;
    }

    public Timestamp getDate() {
        return date;
    }

    public Timestamp getUnadoptDate() {
        return unadoptDate;
    }

    public String getEmojiColor() {

        String color = emoji;
        if (color == null) {
            color = LunaticFamily.getConfig().getDefaultAdoptEmojiColor();
        }
        return color;
    }

    public void setEmojiColor(String color) {
        AdoptionsTable.saveEmojiColor(this.id, color);
    }

    public String getColoredEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultEmoji();
    }

    public static String getDefaultEmoji() {
        return "⌂";
    }
}