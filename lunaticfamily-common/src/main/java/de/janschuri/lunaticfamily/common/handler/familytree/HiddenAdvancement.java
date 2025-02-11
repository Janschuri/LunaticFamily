package de.janschuri.lunaticfamily.common.handler.familytree;

import de.janschuri.lunaticfamily.TreeAdvancement;

public class HiddenAdvancement implements TreeAdvancement {

    private final String key;
    private final TreeAdvancement parent;
    private final float y;
    private final Side side;
    private float x;

    public HiddenAdvancement(String key, TreeAdvancement parentKey, float x, float y, Side side) {
        this.key = key;
        this.parent = parentKey;
        this.x = x;
        this.y = y;
        this.side = side;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getTitle() {
        return key;
    }

    @Override
    public String getDescription() {
        return key;
    }

    @Override
    public String getSkinUrl() {
        return null;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public HiddenAdvancement setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public TreeAdvancement getParent() {
        return parent;
    }

    @Override
    public Side getSide() {
        return side;
    }
}
