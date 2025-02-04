package de.janschuri.lunaticfamily.platform.bukkit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TreeAdvancement {
    private final String key;
    private final TreeAdvancement parent_key;
    private float x;
    private final float y;
    private boolean hidden = false;
    private final String title;
    private final String description;
    private final ItemStack icon;
    private final FamilyTree.Side side;

    protected TreeAdvancement(@NotNull String key, TreeAdvancement parent_key, String title, String description, ItemStack icon, float x, float y, FamilyTree.Side side) {
        this.key = Objects.requireNonNull(key);
        this.parent_key = parent_key;
        this.y = y;
        this.x = x;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.side = side;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title == null ? "no title" : title;
    }

    public String getDescription() {
        return description == null ? "no description" : description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public TreeAdvancement getParent() {
        return parent_key;
    }

    public boolean isHidden() {
        return hidden;
    }

    public FamilyTree.Side getSide() {
        return side;
    }

    protected void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public static class RelationAdvancement extends TreeAdvancement {

        public RelationAdvancement(String key, TreeAdvancement parent_key, String title, String description, ItemStack icon, float x, float y, FamilyTree.Side side) {
            super(key, parent_key, title, description, icon, x, y, side);
        }
    }

    public static class RootTreeAdvancement extends RelationAdvancement {
        private final String background;

        public RootTreeAdvancement(String key, String background, TreeAdvancement parent_key, String title, String description, ItemStack icon, float x, float y, FamilyTree.Side side) {
            super(key, parent_key, title, description, icon, x, y, side);
            this.background = background;
        }

        public String getBackground() {
            return background;
        }
    }

    public static class HiddenAdvancement extends TreeAdvancement {

        public HiddenAdvancement(String key, TreeAdvancement parent_key, float x, float y, FamilyTree.Side side) {
            super(key, parent_key, key, key, new ItemStack(Material.STONE), x, y, side);
            setHidden(true);
        }
    }
}
