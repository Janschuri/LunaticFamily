package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "adoptions")
public class Adoption {

    @Id
    @Identity
    private final int id;
    @ManyToOne
    @JoinColumn(name = "parentID")
    private FamilyPlayerImpl parent;
    @ManyToOne
    @JoinColumn(name = "childID")
    private FamilyPlayerImpl child;
    @ManyToOne
    @JoinColumn(name = "priest")
    private FamilyPlayerImpl priest;
    private String emoji;
    private final Timestamp date;
    @Column(name = "unadoptDate")
    private final Timestamp unadoptDate;

    public Adoption(int id) {
        Adoption adoption = DatabaseRepository.getDatabase().find(Adoption.class).where().eq("id", id).findOne();
        assert adoption != null;
        this.id = adoption.id;
        this.parent = adoption.parent;
        this.child = adoption.child;
        this.priest = adoption.priest;
        this.emoji = adoption.emoji;
        this.date = adoption.date;
        this.unadoptDate = adoption.unadoptDate;
    }

    public Adoption(int id, FamilyPlayerImpl parent, FamilyPlayerImpl child, FamilyPlayerImpl priest, String emoji, Timestamp date, Timestamp unadoptDate) {
        this.id = id;
        this.parent = parent;
        this.child = child;
        this.priest = priest;
        this.emoji = emoji;
        this.date = date;
        this.unadoptDate = unadoptDate;
    }

    public FamilyPlayerImpl getParent() {
        return parent;
    }

    public FamilyPlayerImpl getChild() {
        return child;
    }

    public FamilyPlayerImpl getPriest() {
        return priest;
    }

    public Timestamp getDate() {
        return date;
    }

    public Timestamp getUnadoptDate() {
        return unadoptDate;
    }

    public String getEmojiColor() {

        String color = emoji;
        if (color == null) {
            color = LunaticFamily.getConfig().getDefaultAdoptEmojiColor();
        }
        return color;
    }

    public void setEmojiColor(String color) {
        emoji = color;
        DatabaseRepository.getDatabase().save(this);
    }

    public boolean hasPriest() {
        return priest != null;
    }

    public String getColoredParentEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultParentEmoji();
    }

    public String getColoredChildEmoji() {
        return "<" + getEmojiColor() + ">" + getDefaultChildEmoji();
    }

    public static String getDefaultParentEmoji() {
        return "⌂";
    }

    public static String getDefaultChildEmoji() {
        return "☀";
    }
}