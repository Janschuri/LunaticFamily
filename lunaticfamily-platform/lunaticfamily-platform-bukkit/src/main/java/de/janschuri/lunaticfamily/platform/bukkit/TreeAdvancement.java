package de.janschuri.lunaticfamily.platform.bukkit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TreeAdvancement {
    private final String key;
    private final TreeAdvancement parent_key;
    private final float x;
    private final float y;
    private boolean hidden = true;

    public TreeAdvancement(@NotNull String key, TreeAdvancement parent_key, float x, float y) {
        this.key = key;
        this.parent_key = parent_key;
        this.x = x;
        this.y = -y;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return key;
    }

    public String getDescription() {
        return key;
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.STONE);
    }

    public float getX() {
        return x;
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

    protected void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public static class RelationAdvancement extends TreeAdvancement {

        private final String title;
        private final String description;
        private final ItemStack icon;

        public RelationAdvancement(String key, TreeAdvancement parent_key, String title, String description, ItemStack icon, float x, float y) {
            super(key, parent_key, x, y);
            this.title = title;
            this.description = description;
            this.icon = icon;
            setHidden(false);
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
        public ItemStack getIcon() {
            return icon;
        }
    }

    public static class RootTreeAdvancement extends RelationAdvancement {
        private final String background;

        public RootTreeAdvancement(String key, String background, TreeAdvancement parent_key, String title, String description, ItemStack icon, float x, float y) {
            super(key, parent_key, title, description, icon, x, y);
            this.background = background;
            setHidden(false);
        }

        public String getBackground() {
            return background;
        }
    }
}
