package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.common.LunaticFamily;
//import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import io.ebean.Database;
import io.ebean.annotation.Identity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.sql.Timestamp;

@Entity
public class Adoption {

    @Id
    @Identity
    private final int id;
    @ManyToMany
    private final int parentID;
    @ManyToMany
    private final int childID;
    @ManyToMany
    private final int priest;
    private String emoji;
    private final Timestamp date;
    @Column(name = "unadoptDate")
    private final Timestamp unadoptDate;

    public Adoption(int id) {
        Adoption adoption = DatabaseRepository.getDatabase().find(Adoption.class).where().eq("id", id).findOne();
        assert adoption != null;
        this.id = adoption.id;
        this.parentID = adoption.parentID;
        this.childID = adoption.childID;
        this.priest = adoption.priest;
        this.emoji = adoption.emoji;
        this.date = adoption.date;
        this.unadoptDate = adoption.unadoptDate;
    }

    public Adoption(int id, int parentID, int childID, int priest, String emoji, Timestamp date, Timestamp unadoptDate) {
        this.id = id;
        this.parentID = parentID;
        this.childID = childID;
        this.priest = priest;
        this.emoji = emoji;
        this.date = date;
        this.unadoptDate = unadoptDate;
    }

    public int getParentID() {
        return parentID;
    }

    public int getChildID() {
        return childID;
    }

    public int getPriest() {
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