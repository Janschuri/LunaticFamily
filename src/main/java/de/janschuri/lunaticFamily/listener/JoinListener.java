package de.janschuri.lunaticFamily.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        FamilyPlayer playerFam = new FamilyPlayer(uuid);

        new BukkitRunnable() {
            public void run() {
                if (PluginConfig.enabledCrazyAdvancementAPI) {
                    new FamilyTree(playerFam.getID());
                }
                if (playerFam.isMarried()) {
                    if (!LunaticFamily.isProxy) {
                        if (playerFam.getPartner().sendMessage(Language.prefix + Language.getMessage("marry_partner_online") + 8)) {
                            player.sendMessage(Language.prefix + Language.getMessage("marry_partner_online") + 1);
                        } else {
                            player.sendMessage(Language.prefix + Language.getMessage("marry_partner_offline") + 2);
                        }
                    }
                }
            }
        }.runTaskLater(LunaticFamily.getInstance(), 5L);
    }
}