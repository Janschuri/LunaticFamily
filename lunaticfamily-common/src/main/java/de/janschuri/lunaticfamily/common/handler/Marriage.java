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
    private FamilyPlayer player1;
    @ManyToOne
    @JoinColumn(name = "player2ID")
    @NotNull
    private FamilyPlayer player2;
    @ManyToOne
    @JoinColumn(name = "priest")
    private FamilyPlayer priest;
    @Column(name = "heart")
    private String emoji;
    @WhenCreated
    @NotNull
    private Timestamp date;
    @Column(name = "divorceDate")
    private Timestamp divorceDate;

    public Marriage(FamilyPlayer player1, FamilyPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Marriage save() {
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

    public Marriage setPriest(FamilyPlayer priest) {
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

    public FamilyPlayer getPartner(FamilyPlayer playerFam) {
        if (playerFam.equals(player1)) {
            return player2;
        }

        if (playerFam.equals(player2)) {
            return player1;
        }

        return null;
    }
}
