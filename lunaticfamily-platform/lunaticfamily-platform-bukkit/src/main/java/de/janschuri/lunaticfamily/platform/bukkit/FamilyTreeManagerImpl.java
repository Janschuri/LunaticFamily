package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticlib.platform.bukkit.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class FamilyTreeManagerImpl implements FamilyTreeManager {

    private static final Map<String, String> relationLangs = new HashMap<>();

    @Override
    public boolean update(String server, int familyPlayerID) {
        FamilyTree familyTree = FamilyTree.getFamilyTree(familyPlayerID);

        if (familyTree == null) {
            return false;
        }

        return familyTree.update();
    }


    public static String getRelationLang(String key) {
        return key;
    }
}
