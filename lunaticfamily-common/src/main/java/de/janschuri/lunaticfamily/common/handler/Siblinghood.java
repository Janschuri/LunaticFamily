package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import io.ebean.typequery.Generated;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "siblinghoods")
public class Siblinghood {

    @Id
    @Identity
    @NotNull
    private int id;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "player1ID")
    private FamilyPlayerImpl player1;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "player2ID")
    private FamilyPlayerImpl player2;
    @ManyToOne
    @JoinColumn(name = "priest")
    private FamilyPlayerImpl priest;
    private String emoji;
    @NotNull
    @WhenCreated
    private Timestamp date;
    @Column(name = "unsiblingDate")
    private Timestamp unsiblingDate;

    public Siblinghood(FamilyPlayerImpl player1, FamilyPlayerImpl player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Siblinghood save() {
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

    public Siblinghood setPriest(FamilyPlayerImpl priest) {
        this.priest = priest;
        return this;
    }

    public Timestamp getDate() {
        return date;
    }

    public Timestamp getUnsiblingDate() {
        return unsiblingDate;
    }

    public Siblinghood setUnsiblingDate() {
        this.unsiblingDate = new Timestamp(System.currentTimeMillis());
        return this;
    }

    public String getEmojiColor() {
        String color = emoji;
        if (color == null) {
            color = LunaticFamily.getConfig().getDefaultSiblingEmojiColor();
        }
        return color;
    }

    public Siblinghood setEmojiColor(String color) {
        emoji = color;
        return this;
    }

    public String getColoredEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultEmoji();
    }

    public static String getDefaultEmoji() {
        return "⭐";
    }

    public FamilyPlayerImpl getSibling(int playerID) {
        if (player1.getId() == playerID) {
            return player2;
        }

        if (player1.getId() == playerID) {
            return player1;
        }

        return null;
    }
}
