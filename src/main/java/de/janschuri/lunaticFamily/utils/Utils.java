package de.janschuri.lunaticFamily.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean isValidHexCode(String hexCode) {
        Pattern pattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
        Matcher matcher = pattern.matcher(hexCode);
        return matcher.matches();
    }
    public static Component createClickableMessage(String message, String confirmHoverText, String confirmCommand, String cancelHoverText, String cancelCommand) {

        return Component.text(Language.prefix + message)
                .append(Component.text(" ✓", NamedTextColor.GREEN, TextDecoration.BOLD).clickEvent(ClickEvent.runCommand(
                        confirmCommand
                )))
                .hoverEvent(HoverEvent.showText(Component.text(
                        confirmHoverText
                )))
                .append(Component.text(" ❌", NamedTextColor.RED, TextDecoration.BOLD).clickEvent(ClickEvent.runCommand(
                        cancelCommand
                )))
                .hoverEvent(HoverEvent.showText(Component.text(
                        cancelHoverText
                )))
                .toBuilder().build();
    }

    public static String getName(String name) {
        if (Bukkit.getOfflinePlayer(name).getName() != null) {
            return Bukkit.getOfflinePlayer(name).getName();
        } else {
            return name;
        }
    }

    public static boolean playerExists(String name) {
        String uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();

        return LunaticFamily.getDatabase().getID(uuid) != 0;
    }

    public static void addMissingProperties(File file, File defaultFile) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultFile);

        YamlConfiguration newConfig = new YamlConfiguration();

        Set<String> keys = config.getKeys(true);


        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key)) {
                newConfig.set(key, defaultConfig.get(key));

                List<String> comments = defaultConfig.getComments(key);
                if (!comments.isEmpty()) {
                    newConfig.setComments(key, comments);
                }
            } else {
                newConfig.set(key, config.get(key));

                List<String> defaultComments = defaultConfig.getComments(key);
                List<String> configComments = config.getComments(key);
                List<String> comments = new ArrayList<>();

                if (!new HashSet<>(configComments).containsAll(defaultComments)) {
                    comments.addAll(defaultComments);
                }

                comments.addAll(configComments);

                if (!comments.isEmpty()) {
                    newConfig.setComments(key, comments);
                }

                keys.remove(key);
            }
        }

        // Transfer remaining properties without comments
        for (String key : keys) {
            newConfig.set(key, config.get(key));
        }

        try {
            // Save the merged configuration with comments
            newConfig.save(file);
        } catch (IOException e) {
            Logger.errorLog("Could not save file: " + file.getName());
            e.printStackTrace();
        }
    }
    public static Location getPositionBetweenLocations(Location loc1, Location loc2) {
        Vector vec1 = new Vector(loc1.getX(), loc1.getY(), loc1.getZ());
        Vector vec2 = new Vector(loc2.getX(), loc2.getY(), loc2.getZ());

        Vector midpoint = vec1.clone().add(vec2).multiply(0.5);

        return new Location(loc1.getWorld(), midpoint.getX(), midpoint.getY(), midpoint.getZ());
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

    public static void sendConsoleCommand(String command) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
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
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);

        return head;
    }

    public static String getUUID(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId().toString();
    }

    public static Player getPlayer(String arg) {
        return Bukkit.getPlayer(arg);
    }
}
