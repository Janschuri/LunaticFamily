package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.annotation.Identity;
import io.ebean.annotation.NotNull;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "lunaticfamily_adoptions")
public class Adoption {

    @Id
    @Identity
    @NotNull
    private long id;
    private String emojiColor;
    @NotNull
    private Timestamp date;
    private Timestamp unadoptDate;

    @ManyToOne
    @NotNull
    private FamilyPlayer parent;
    @ManyToOne
    @NotNull
    private FamilyPlayer child;
    @ManyToOne
    private FamilyPlayer priest;

    public Adoption(FamilyPlayer parent, FamilyPlayer child) {
        this.parent = parent;
        this.child = child;
        this.date = new Timestamp(System.currentTimeMillis());
    }

    public Adoption save() {
        DatabaseRepository.getDatabase().save(this);
        return this;
    }

    public FamilyPlayer getParent() {
        return parent;
    }

    public FamilyPlayer getChild() {
        return child;
    }

    public FamilyPlayer getPriest() {
        return priest;
    }

    public Adoption setPriest(FamilyPlayer priest) {
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

        String color = emojiColor;
        if (color == null) {
            color = LunaticFamily.getConfig().getDefaultAdoptEmojiColor();
        }
        return color;
    }

    public Adoption setEmojiColor(String color) {
        emojiColor = color;
        return this;
    }

    public boolean hasPriest() {
        return priest != null;
    }

    public static String getDefaultParentEmoji() {
        return "⌂";
    }

    public static String getDefaultChildEmoji() {
        return "☀";
    }

    public void delete() {
        DatabaseRepository.getDatabase().delete(this);
    }
}