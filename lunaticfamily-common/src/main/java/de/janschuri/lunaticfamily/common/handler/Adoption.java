package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "adoptions")
public class Adoption {

    @Id
    @Identity
    @NotNull
    private int id;
    private String emoji;
    @NotNull
    @WhenCreated
    private Timestamp date;
    @Column(name = "unadoptDate")
    private Timestamp unadoptDate;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "parentID")
    private FamilyPlayerImpl parent;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "childID")
    private FamilyPlayerImpl child;
    @ManyToOne
    @JoinColumn(name = "priest")
    private FamilyPlayerImpl priest;

    public Adoption(FamilyPlayerImpl parent, FamilyPlayerImpl child) {
        this.parent = parent;
        this.child = child;
    }

    public Adoption save() {
        DatabaseRepository.getDatabase().save(this);
        return this;
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

    public Adoption setPriest(FamilyPlayerImpl priest) {
        this.priest = priest;
        return this;
    }

    public Timestamp getDate() {
        return date;
    }

    public Timestamp getUnadoptDate() {
        return unadoptDate;
    }
    public Adoption setUnadoptDate() {
        this.unadoptDate = new Timestamp(System.currentTimeMillis());
        return this;
    }

    public String getEmojiColor() {

        String color = emoji;
        if (color == null) {
            color = LunaticFamily.getConfig().getDefaultAdoptEmojiColor();
        }
        return color;
    }

    public Adoption setEmojiColor(String color) {
        emoji = color;
        return this;
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