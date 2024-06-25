package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;

import java.sql.Timestamp;

public class Marriage {

    private final int id;
    private final int player1ID;
    private final int player2ID;
    private final int priest;
    private final String emoji;
    private final Timestamp date;
    private final Timestamp divorceDate;

    public Marriage(int id) {
        Marriage marriage = MarriagesTable.getMarriage(id);
        this.id = marriage.id;
        this.player1ID = marriage.player1ID;
        this.player2ID = marriage.player2ID;
        this.emoji = marriage.emoji;
        this.priest = marriage.priest;
        this.date = marriage.date;
        this.divorceDate = marriage.divorceDate;
    }

    public Marriage(int id, int player1ID, int player2ID, int priest, String emoji, Timestamp date, Timestamp divorceDate) {
        this.id = id;
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        this.priest = priest;
        this.emoji = emoji;
        this.date = date;
        this.divorceDate = divorceDate;
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

    public Timestamp getDivorceDate() {
        return divorceDate;
    }

    public String getEmojiColor() {

        String color = emoji;
        if (color == null) {
            color = LunaticFamily.getConfig().getDefaultMarryEmojiColor();
        }
        return color;
    }

    public void setEmojiColor(String color) {
        MarriagesTable.saveEmojiColor(this.id, color);
    }

    public String getColoredEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultEmoji();
    }

    public static String getDefaultEmoji() {
        return "❤";
    }
}
