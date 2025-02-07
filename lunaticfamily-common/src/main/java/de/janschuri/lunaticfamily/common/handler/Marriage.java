package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
//import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "marriages")
public class Marriage {

    @Id
    @Identity
    private int id;
    @ManyToOne
    @JoinColumn(name = "player1ID")
    private FamilyPlayerImpl player1;
    @ManyToOne
    @JoinColumn(name = "player2ID")
    private FamilyPlayerImpl player2;
    @ManyToOne
    @JoinColumn(name = "priest")
    private FamilyPlayerImpl priest;
    @Column(name = "heart")
    private String emoji;
    @WhenCreated
    private Timestamp date;
    @Column(name = "divorceDate")
    private Timestamp divorceDate;

    public Marriage(int id) {
        Marriage marriage = DatabaseRepository.getDatabase().find(Marriage.class).where().eq("id", id).findOne();
        this.id = marriage.id;
        this.player1 = marriage.player1;
        this.player2 = marriage.player2;
        this.emoji = marriage.emoji;
        this.priest = marriage.priest;
        this.date = marriage.date;
        this.divorceDate = marriage.divorceDate;
    }

    public Marriage(FamilyPlayerImpl player1, FamilyPlayerImpl player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public FamilyPlayerImpl getPlayer1() {
        return player1;
    }

    public FamilyPlayerImpl getPlayer2() {
        return player2;
    }

    public FamilyPlayerImpl getPriest() {
        return priest;
    }

    public Marriage setPriest(FamilyPlayerImpl priest) {
        this.priest = priest;
        return this;
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

    public FamilyPlayerImpl getPartner(int playerID) {
        if (playerID == player1.getId()) {
            return player2;
        }

        if (playerID == player2.getId()) {
            return player1;
        }

        return null;
    }
}
