package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;

import java.sql.Time;
import java.sql.Timestamp;

public class Siblinghood {

    private final int id;
    private final int player1ID;
    private final int player2ID;
    private final int priest;
    private final String emoji;
    private final Timestamp date;
    private final Timestamp unsiblingDate;

    public Siblinghood(int id) {
        Siblinghood siblinghood = SiblinghoodsTable.getSiblinghood(id);
        assert siblinghood != null;
        this.id = siblinghood.id;
        this.player1ID = siblinghood.player1ID;
        this.player2ID = siblinghood.player2ID;
        this.priest = siblinghood.priest;
        this.emoji = siblinghood.emoji;
        this.date = siblinghood.date;
        this.unsiblingDate = siblinghood.unsiblingDate;
    }

    public Siblinghood(int id, int player1ID, int player2ID, int priest, String emoji, Timestamp date, Timestamp unsiblingDate) {
        this.id = id;
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        this.priest = priest;
        this.emoji = emoji;
        this.date = date;
        this.unsiblingDate = unsiblingDate;
    }

    public int getId() {
        return id;
    }

    public int getPlayer1ID() {
        return player1ID;
    }

    public int getPlayer2ID() {
        return player2ID;
    }

    public int getPriest() {
        return priest;
    }

    public Timestamp getDate() {
        return date;
    }

    public Timestamp getUnsiblingDate() {
        return unsiblingDate;
    }

    public String getEmojiColor() {
        String color = emoji;
        if (color == null) {
            color = LunaticFamily.getConfig().getDefaultSiblingEmojiColor();
        }
        return color;
    }

    public void setEmojiColor(String color) {
        SiblinghoodsTable.saveEmojiColor(this.id, color);
    }

    public String getColoredEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultEmoji();
    }

    public static String getDefaultEmoji() {
        return "⭐";
    }
}
