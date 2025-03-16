package de.janschuri.lunaticfamily.common.handler.familytree;

import de.janschuri.lunaticfamily.TreeAdvancement;

import java.util.UUID;

public class RootAdvancement extends RelationAdvancement {
    private final String background;

    public RootAdvancement(String key, String background, TreeAdvancement parentKey, int id, UUID uuid, String gender, String title, String description, String skinUrl, Side side) {
        super(key, parentKey, id, uuid, gender, title, description, skinUrl, 0, 0, side);
        this.background = background;
    }

    public String getBackground() { return background; }
}