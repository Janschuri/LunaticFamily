package de.janschuri.lunaticfamily.common.handler.familytree;

import de.janschuri.lunaticfamily.TreeAdvancement;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RelationAdvancement implements TreeAdvancement {

    private final int id;
    private final String gender;
    private final UUID uuid;
    private final String key;
    private final TreeAdvancement parent;
    private final String title;
    private final String description;
    private final String skinUrl;
    private final float y;
    private final Side side;
    private float x;

    public static final String DEFAULT_SKIN = "https://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";

    public RelationAdvancement(@NotNull String key, TreeAdvancement parent, int id, UUID uuid, String gender, String title, String description, String skinUrl, float x, float y, Side side) {
        this.id = id;
        this.uuid = uuid;
        this.gender = gender;
        this.key = key;
        this.parent = parent;
        this.title = title;
        this.description = description;

        if (skinUrl != null || !skinUrl.isEmpty()) {
            this.skinUrl = skinUrl;
        } else {
            this.skinUrl = DEFAULT_SKIN;
        }

        this.x = x;
        this.y = y;
        this.side = side;
    }

    public int getId() {
        return id;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public TreeAdvancement getParent() {
        return parent;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getSkinUrl() {
        return skinUrl;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public RelationAdvancement setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public Side getSide() {
        return side;
    }
}