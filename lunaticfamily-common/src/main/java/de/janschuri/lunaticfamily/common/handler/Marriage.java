package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
//import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.sql.Timestamp;

@Entity
public class Marriage {

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
    @Column(name = "divorceDate")
    private final Timestamp divorceDate;

    public Marriage(int id) {
        Marriage marriage = DatabaseRepository.getDatabase().find(Marriage.class).where().eq("id", id).findOne();
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
        emoji = color;
        DatabaseRepository.getDatabase().save(this);
    }

    public String getColoredEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultEmoji();
    }

    public static String getDefaultEmoji() {
        return "❤";
    }

    public boolean isDivorced() {
        return divorceDate != null;
    }

    public int getPartnerID(int playerID) {
        if (playerID == player1ID) {
            return player2ID;
        }

        if (playerID == player2ID) {
            return player1ID;
        }

        return -1;
    }
}
