package de.janschuri.lunaticfamily.platform.bukkit;

import org.bukkit.inventory.ItemStack;

public class TreeAdvancement {
    private String key;
    private String title;
    private String description;
    private ItemStack icon;

    public TreeAdvancement(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public TreeAdvancement title(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TreeAdvancement description(String description) {
        this.description = description;
        return this;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public TreeAdvancement icon(ItemStack icon) {
        this.icon = icon;
        return this;
    }

    public static class RootTreeAdvancement extends TreeAdvancement {
        private String background;

        public RootTreeAdvancement(String key, String background) {
            super(key);
            this.background = background;
        }

        public String getBackground() {
            return background;
        }
    }
}
