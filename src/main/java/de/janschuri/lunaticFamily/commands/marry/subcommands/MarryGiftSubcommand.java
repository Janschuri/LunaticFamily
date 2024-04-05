package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
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
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!playerFam.isMarried()) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_gift_no_partner"));
            } else if (!player.hasPermission("lunaticFamily.marry.gift")) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())).getName()));
            } else if (player.getInventory().getItemInMainHand().isEmpty()) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_gift_hand_empty"));
            } else {
                Player partnerPlayer = playerFam.getPartner().getPlayer();
                if (partnerPlayer.getInventory().firstEmpty() == -1) {
                    sender.sendMessage(Language.prefix + Language.getMessage("marry_gift_partner_full_inv"));
                } else {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    player.getInventory().remove(item);
                    partnerPlayer.getInventory().addItem(item);
                    Material material = item.getType();
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

                    Component msgPlayer = Component.text(Language.getMessage("marry_gift_sent")).replaceText(replacementConfig);
                    Component msgPartner = Component.text(Language.getMessage("marry_gift_got")).replaceText(replacementConfig);

                    player.sendMessage(msgPlayer);
                    partnerPlayer.sendMessage(msgPartner);
                }

            }
        }
    }
}
