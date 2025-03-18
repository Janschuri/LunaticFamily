package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.TreeAdvancement;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticfamily.platform.bukkit.external.CrazyAdvancementsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class FamilyTreeManagerImpl implements FamilyTreeManager {

    private static final Map<String, String> relationLangs = new HashMap<>();

    @Override
    public CompletableFuture<Boolean> update(String server, UUID uuid, List<TreeAdvancement> treeAdvancements) {

        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return CompletableFuture.completedFuture(false);
        }

        CrazyAdvancementsAPI.send(player, treeAdvancements);

        return CompletableFuture.completedFuture(true);
    }
}
