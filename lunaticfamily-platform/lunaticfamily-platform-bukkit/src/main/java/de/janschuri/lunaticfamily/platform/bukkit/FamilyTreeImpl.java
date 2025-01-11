package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.platform.FamilyRelation;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTree;
import de.janschuri.lunaticlib.platform.bukkit.util.ItemStackUtils;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import eu.endercentral.crazy_advancements.packet.AdvancementsPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class FamilyTreeImpl implements FamilyTree {

    private static final Map<Integer, Map<String, Integer>> filledSlots = new HashMap<>();

    @Override
    public boolean isFamilyTreeMapLoaded() {
        return true;
    }

    @Override
    public boolean loadFamilyTreeMap(String JSONContent) {
        return true;
    }

    @Override
    public boolean update(String server, UUID uuid, String background, FamilyRelation relation) {
        Logger.debugLog("Creating FamilyTree for " + uuid);
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            Logger.errorLog("Player with UUID " + uuid + " not found on the server.");
            return false;
        }

        AdvancementManager manager = new AdvancementManager(new NameKey("manager", uuid.toString()));

        List<NameKey> advancements = List.of(new NameKey("family_tree", "ego_holder"));

        AdvancementsPacket packet = new AdvancementsPacket(player, false, null, advancements);
        packet.send();

        addRelations(null, relation, manager, FamilyType.EGO);

        manager.getAdvancement(new NameKey("family_tree", "ego_holder")).getDisplay().setBackgroundTexture(background);

        manager.addPlayer(player);


        return true;
    }

    private void addRelations(@Nullable Advancement parent, FamilyRelation relation, AdvancementManager manager, FamilyType type) {
        ItemStack icon = ItemStackUtils.getSkullFromURL(relation.getSkinUrl());
        String title = relation.getName();
        String description = relation.getRelationLang();
        AdvancementDisplay.AdvancementFrame frame;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        if (type == FamilyType.EGO) {
            frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        } else {
            frame = AdvancementDisplay.AdvancementFrame.GOAL;
        }

        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);

        float parentY = 0;
        float parentX = 0;

        if (parent != null) {
            parentY = parent.getDisplay().getY();
            parentX = parent.getDisplay().getX();
        }
        float y = 0;
        float x = 0;

        if (type == FamilyType.EGO) {
            y = 7.5f;
            x = 13.5f;
        } else {
            if (type == FamilyType.CHILD) {
                y = parentY + 1;
                x = parentX + 0.5f;

            } else if (type == FamilyType.PARENT) {
                y = 2f + parent.getDisplay().getY();
                x = -1.5f + parent.getDisplay().getX();
            } else if (type == FamilyType.SIBLING) {
                y = 0 + parent.getDisplay().getY();
                x = 3 + parent.getDisplay().getX();
            } else if (type == FamilyType.PARTNER) {
                y = 0 + parent.getDisplay().getY();
                x = -3 + parent.getDisplay().getX();
            }
        }

        display.setY(y);
        display.setX(x);

        AdvancementDisplay holderDisplay = new AdvancementDisplay(icon, title, description+" holder", frame, AdvancementVisibility.ALWAYS);
        AdvancementFlag[] flags = {
                AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN
        };
        Advancement holder = new Advancement(parent, new NameKey("family_tree", relation.getRelation() + "_holder"), holderDisplay, flags);
        manager.addAdvancement(holder);

        holder.getDisplay().setY(y);
        holder.getDisplay().setX(x - 0.5f);

        Advancement advancement = new Advancement(holder, new NameKey("family_tree", relation.getRelation()), display);
        manager.addAdvancement(advancement);

        if (!relation.getChildren().isEmpty()) {
            AdvancementDisplay childrenHolderDisplay = new AdvancementDisplay(icon, title, description+" children holder", frame, AdvancementVisibility.ALWAYS);
            AdvancementFlag[] childrenHolderFlags = {
                    AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN
            };
            Advancement childrenHolder = new Advancement(holder, new NameKey("family_tree", relation.getRelation() + "_children_holder"), childrenHolderDisplay, childrenHolderFlags);
            childrenHolder.getDisplay().setY(y+1);
            childrenHolder.getDisplay().setX(x);
            manager.addAdvancement(childrenHolder);

            for (int i = 0; i < relation.getChildren().size(); i++) {
                FamilyRelation child = relation.getChildren().get(i);

                AdvancementDisplay childHolderDisplay = new AdvancementDisplay(icon, title, child.getRelationLang()+" holder", frame, AdvancementVisibility.ALWAYS);
                AdvancementFlag[] childHolderFlags = {
                        AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN
                };
                Advancement childHolder = new Advancement(childrenHolder, new NameKey("family_tree", child.getRelation() + "_holder"), childHolderDisplay, childHolderFlags);
                childHolder.getDisplay().setY(y+1);
                childHolder.getDisplay().setX(x+(i*2)-0.5f);

                manager.addAdvancement(childHolder);

                addRelations(childHolder, child, manager, FamilyType.CHILD);
            }
        }
    }

    private enum FamilyType {
        CHILD,
        PARENT,
        SIBLING,
        PARTNER,
        EGO
    }

    @Override
    public FamilyPlayerImpl getFamilyPlayer(int id) {
        return new FamilyPlayerImpl(id);
    }

    @Override
    public String getRelation(String relation, String key) {
        return LunaticFamily.getLanguageConfig().getRelation(relation, key);
    }
}
