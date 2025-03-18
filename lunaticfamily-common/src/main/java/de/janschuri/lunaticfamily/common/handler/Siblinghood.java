package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "lunaticfamily_siblinghoods")
public class Siblinghood {

    @Id
    @Identity
    @NotNull
    private long id;
    @ManyToOne
    @NotNull
    private FamilyPlayer player1;
    @ManyToOne
    @NotNull
    private FamilyPlayer player2;
    @ManyToOne
    private FamilyPlayer priest;
    private String emojiColor;
    @NotNull
    private Timestamp date;
    private Timestamp unsiblingDate;

    public Siblinghood(FamilyPlayer player1, FamilyPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.date = new Timestamp(System.currentTimeMillis());
    }

    public Siblinghood save() {
        DatabaseRepository.getDatabase().save(this);
        return this;
    }

    public FamilyPlayer getPlayer1() {
        return player1;
    }

    public FamilyPlayer getPlayer2() {
        return player2;
    }

    public FamilyPlayer getPriest() {
        return priest;
    }

    public Siblinghood setPriest(FamilyPlayer priest) {
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
        String color = emojiColor;
        if (color == null) {
            color = LunaticFamily.getConfig().getDefaultSiblingEmojiColor();
        }
        return color;
    }

    public Siblinghood setEmojiColor(String color) {
        emojiColor = color;
        return this;
    }

    public String getColoredEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultEmoji();
    }

    public static String getDefaultEmoji() {
        return "⭐";
    }

    public FamilyPlayer getSibling(FamilyPlayer playerFam) {

        if (player1.equals(playerFam)) {
            return player2;
        }

        if (player2.equals(playerFam)) {
            return player1;
        }


        return null;
    }
}
