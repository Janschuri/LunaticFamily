package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
//import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.sql.Timestamp;

@Entity
public class Siblinghood {

    @Id
    @Identity
    private final int id;
    @ManyToMany(mappedBy = "playerData")
    private final int player1ID;
    @ManyToMany(mappedBy = "playerData")
    private final int player2ID;
    @ManyToMany(mappedBy = "playerData")
    private final int priest;
    private String emoji;
    private final Timestamp date;
    @Column(name = "unsiblingDate")
    private final Timestamp unsiblingDate;

    public Siblinghood(int id) {
        Siblinghood siblinghood = DatabaseRepository.getDatabase().find(Siblinghood.class).where().eq("id", id).findOne();
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
        emoji = color;
        DatabaseRepository.getDatabase().save(this);
    }

    public String getColoredEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultEmoji();
    }

    public static String getDefaultEmoji() {
        return "⭐";
    }

    public int getSiblingID(int playerID) {
        if (playerID == player1ID) {
            return player2ID;
        }

        if (playerID == player2ID) {
            return player1ID;
        }

        return -1;
    }
}
