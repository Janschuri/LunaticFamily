package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.database.Database;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

import static java.lang.Math.sqrt;

public class Utils {

    public static TextComponent createClickableMessage(String message, String confirmHoverText, String confirmCommand, String cancelHoverText, String cancelCommand) {
        TextComponent confirm = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + " ✓");
        confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, confirmCommand));
        confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(confirmHoverText).create()));


        TextComponent cancel = new TextComponent(ChatColor.RED + " ❌");
        cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cancelCommand));
        cancel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(cancelHoverText).create()));

        TextComponent prefix = new TextComponent(Language.prefix);
        TextComponent msg = new TextComponent(message);

        return new TextComponent(prefix, msg, confirm, cancel);
    }

    public static String getName(String name) {
        if (Bukkit.getOfflinePlayer(name).getName() != null) {
            return Bukkit.getOfflinePlayer(name).getName();
        } else {
            return name;
        }
    }

    public static boolean playerExists(String name) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().equals(name)) return true;
        }

        String uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();

        if (LunaticFamily.getDatabase().getID(uuid) != 0) {
            return true;
        }
        return false;
    }

    public static void addMissingProperties(File file, String filePath, LunaticFamily plugin) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), filePath));

        Logger.log("yml " + filePath , LoggingSeverity.DEBUG);

        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Location getPositionBetweenLocations(Location loc1, Location loc2) {
        Vector vec1 = new Vector(loc1.getX(), loc1.getY(), loc1.getZ());
        Vector vec2 = new Vector(loc2.getX(), loc2.getY(), loc2.getZ());

        Vector midpoint = vec1.clone().add(vec2).multiply(0.5);

        Location position = new Location(loc1.getWorld(), midpoint.getX(), midpoint.getY(), midpoint.getZ());

        return position;
    }

    public static boolean isInRange(Location firstLocation, Location secondLocation, double range) {

        double distance = firstLocation.distance(secondLocation);

        return distance <= range;
    }

    public static void spawnParticles(Location location, Particle particle) {
        World world = location.getWorld();

        Random random = new Random();

        double range = 2.0;

        for (int i = 0; i < 10; i++) {
            double offsetX = (random.nextDouble() - 0.5) * range * 2;
            double offsetY = (random.nextDouble() - 0.5) * range * 2;
            double offsetZ = (random.nextDouble() - 0.5) * range * 2;

            Location particleLocation = location.clone().add(offsetX, offsetY, offsetZ);

            world.spawnParticle(particle, particleLocation, 1);
        }
    }

    public static String getItemKey(Material material) {
        if (material.isBlock()) {
            String id = material.getKey().getKey();

            return "block.minecraft." + id;
        } else if (material.isItem()) {
            String id = material.getKey().getKey();

            return "item.minecraft." + id;
        }
        return "block.minecraft.dirt";
    }

    public static ItemStack getSkull(String url) {

        PlayerProfile pProfile = getProfile(url);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwnerProfile(pProfile);
        head.setItemMeta(meta);

        return head;
    }

    public static String getUUID(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId().toString();
    }

    private static PlayerProfile getProfile(String url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }
}
