package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class MarryGiftSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "gift";
    private static final String permission = "lunaticfamily.marry.gift";

    public MarryGiftSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!playerFam.isMarried()) {
                playerFam.sendMessage(Language.prefix + Language.getMessage("marry_gift_no_partner"));
            } else if (!player.hasPermission("lunaticFamily.marry.gift")) {
                playerFam.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else if (!LunaticFamily.isPlayerOnline(playerFam.getPartner().getUUID())) {
                playerFam.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())).getName()));
            } else if (player.getInventory().getItemInMainHand().isEmpty()) {
                playerFam.sendMessage(Language.prefix + Language.getMessage("marry_gift_empty_hand"));
            } else {
                FamilyPlayer partnerFam = playerFam.getPartner();
//                if (partnerPlayer.getInventory().firstEmpty() == -1) {
//                    playerFam.sendMessage(Language.prefix + Language.getMessage("marry_gift_partner_full_inv"));
//                } else {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    player.getInventory().removeItem(item);
//                    partnerPlayer.getInventory().addItem(item);
                LunaticFamily.dropItemToPlayer(partnerFam.getUUID(), item);
                    int amount = item.getAmount();

                    ItemMeta itemMeta = item.getItemMeta();





                    Component componentAmount = Component.text(amount + "x ");
                    Component componentItem = Component.empty();


                    if (itemMeta.hasDisplayName()) {
                        componentItem = componentAmount.append(item.displayName());

                    } else {
                        componentItem = componentAmount.append(Component.translatable(item.translationKey()));
                    }

                    TextReplacementConfig replacementConfig = TextReplacementConfig.builder().match("%item%").replacement(componentItem).build();

                    Component msgPlayer = Component.text(Language.prefix + Language.getMessage("marry_gift_sent")).replaceText(replacementConfig);
                    Component msgPartner = Component.text(Language.prefix + Language.getMessage("marry_gift_got")).replaceText(replacementConfig);

                    playerFam.sendMessage(msgPlayer);
                    partnerFam.sendMessage(msgPartner);
//                }

            }
        }
    }
}
