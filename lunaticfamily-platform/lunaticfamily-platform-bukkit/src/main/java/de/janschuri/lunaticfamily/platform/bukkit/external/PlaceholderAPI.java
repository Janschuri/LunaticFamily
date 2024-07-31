package de.janschuri.lunaticfamily.platform.bukkit.external;

import de.janschuri.lunaticfamily.FamilyPlayer;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.handler.Placeholder;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.bukkit.FamilyTreeImpl;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.utils.Mode;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion implements Relational {

    @Override
    public String getAuthor() {
        return "Janschuri";
    }

    @Override
    public String getIdentifier() {
        return "lunaticfamily";
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        return Placeholder.getPlaceholder(player.getUniqueId(), params);
    }

    @Override
    public String onPlaceholderRequest(Player player, Player player1, String s) {
        String ph = Placeholder.getPlaceholder(player.getUniqueId(), player1.getUniqueId(), s);
        return ph;
    }

}
