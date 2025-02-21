package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.handler.familytree.RelationAdvancement;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.*;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyList extends FamilyCommand implements HasParams, HasParentCommand {

    private static final FamilyList INSTANCE = new FamilyList();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Show a list of all your family members.");
    private static final CommandMessageKey HELP_OTHERS_MK = new LunaticCommandMessageKey(INSTANCE, "help_others")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Show a list of all the family members of a player.");
    private static final CommandMessageKey HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "header")
            .defaultMessage("en", "Your family:");
    private static final CommandMessageKey OTHERS_HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "others_header")
            .defaultMessage("en", "%player%'s family:");
    private static final CommandMessageKey RELATIONS_MK = new LunaticCommandMessageKey(INSTANCE, "relations")
            .defaultMessage("en", "&6%relation%: &b%player%");


    @Override
    public String getPermission() {
        return "lunaticfamily.family.list";
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission(),
                HELP_OTHERS_MK, getPermission() + ".others"
        );
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (!(sender instanceof PlayerSender) && args.length < 1) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (args.length == 0) {
            PlayerSender player = (PlayerSender) sender;
            UUID uuid = player.getUniqueId();
            FamilyPlayer playerFam = getFamilyPlayer(uuid);
            playerFam.update();

            List<RelationAdvancement> relationAdvancements = playerFam.getFamilyTree().getRelationAdvancements();
            ComponentBuilder msg = Component.text().append(getMessage(HEADER_MK.noPrefix()));

            sender.sendMessage(getFamilyListMessage(relationAdvancements, msg));
            playerFam.updateFamilyTree();
            return true;
        }

        String playerName = args[0];
        UUID player1UUID = Utils.getUUIDFromArg(playerName);

        if (player1UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", playerName)));
            return true;
        }


        if (!sender.hasPermission(getPermission() + ".others")) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);
        List<RelationAdvancement> relationAdvancements = player1Fam.getFamilyTree().getRelationAdvancements();
        ComponentBuilder msg = Component.text();
        msg.append(getMessage(OTHERS_HEADER_MK.noPrefix(),
                placeholder("%player%", player1Fam.getName())));

        sender.sendMessage(getFamilyListMessage(relationAdvancements, msg));
        player1Fam.updateFamilyTree();

        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK.noPrefix())
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }

    private Component getFamilyListMessage(List<RelationAdvancement> relationAdvancements, ComponentBuilder msg) {

        for (RelationAdvancement relationAdvancement : relationAdvancements) {

                Component relation = Component.text(relationAdvancement.getDescription());
                Component name = Component.text(relationAdvancement.getTitle());

                TextReplacementConfig relationRpl = TextReplacementConfig.builder()
                        .match("%relation%")
                        .replacement(relation).build();

                TextReplacementConfig nameRpl = TextReplacementConfig.builder()
                        .match("%player%")
                        .replacement(name).build();

                Component component = getMessage(RELATIONS_MK.noPrefix())
                        .replaceText(relationRpl)
                        .replaceText(nameRpl);

                msg.append(Component.newline())
                        .append(component);

        }

        return msg.build();
    }
}
