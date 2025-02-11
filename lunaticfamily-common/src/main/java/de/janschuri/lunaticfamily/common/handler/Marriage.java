package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
//import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "marriages")
public class Marriage {

    @Id
    @Identity
    @NotNull
    private int id;
    @ManyToOne
    @JoinColumn(name = "player1ID")
    @NotNull
    private FamilyPlayerImpl player1;
    @ManyToOne
    @JoinColumn(name = "player2ID")
    @NotNull
    private FamilyPlayerImpl player2;
    @ManyToOne
    @JoinColumn(name = "priest")
    private FamilyPlayerImpl priest;
    @Column(name = "heart")
    private String emoji;
    @WhenCreated
    @NotNull
    private Timestamp date;
    @Column(name = "divorceDate")
    private Timestamp divorceDate;

    public Marriage(FamilyPlayerImpl player1, FamilyPlayerImpl player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Marriage save() {
        DatabaseRepository.getDatabase().save(this);
        return this;
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

    public Marriage setDivorceDate() {
        divorceDate = new Timestamp(System.currentTimeMillis());
        return this;
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
