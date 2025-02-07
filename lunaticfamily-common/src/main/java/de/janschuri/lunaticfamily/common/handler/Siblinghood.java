package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
//import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "siblinghoods")
public class Siblinghood {

    @Id
    @Identity
    private final int id;
    @ManyToOne
    @JoinColumn(name = "player1ID")
    private FamilyPlayerImpl player1;
    @ManyToOne
    @JoinColumn(name = "player2ID")
    private FamilyPlayerImpl player2;
    @ManyToOne
    @JoinColumn(name = "priest")
    private FamilyPlayerImpl priest;
    private String emoji;
    private final Timestamp date;
    @Column(name = "unsiblingDate")
    private final Timestamp unsiblingDate;

    public Siblinghood(int id) {
        Siblinghood siblinghood = DatabaseRepository.getDatabase().find(Siblinghood.class).where().eq("id", id).findOne();
        assert siblinghood != null;
        this.id = siblinghood.id;
        this.player1 = siblinghood.player1;
        this.player2 = siblinghood.player2;
        this.priest = siblinghood.priest;
        this.emoji = siblinghood.emoji;
        this.date = siblinghood.date;
        this.unsiblingDate = siblinghood.unsiblingDate;
    }

    public Siblinghood(int id, FamilyPlayerImpl player1, FamilyPlayerImpl player2, FamilyPlayerImpl priest, String emoji, Timestamp date, Timestamp unsiblingDate) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.priest = priest;
        this.emoji = emoji;
        this.date = date;
        this.unsiblingDate = unsiblingDate;
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
