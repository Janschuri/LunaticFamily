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
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {

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
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        return Placeholder.getPlaceholder(player.getUniqueId(), params);
    }
}
