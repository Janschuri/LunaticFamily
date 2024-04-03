package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class MarryGiftSubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.marry.gift";
    private static final List<String> aliases = Language.getAliases("marry", "gift");

    public MarryGiftSubcommand() {
        super(permission, aliases);
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



                    String[] msgPlayer = Language.getMessage("marry_gift_sent").split("%item%");
                    String[] msgPartner = Language.getMessage("marry_gift_got").split("%item%");

                    TextComponent componentAmount = new TextComponent(amount + "x ");

                    TextComponent componentPlayer1 = new TextComponent(msgPlayer[0]);
                    TextComponent componentPlayer2 = new TextComponent(msgPlayer[1]);

                    TextComponent componentPartner1 = new TextComponent(msgPartner[0]);
                    TextComponent componentPartner2 = new TextComponent(msgPartner[1]);

                    if (itemMeta.hasDisplayName()) {
                        TextComponent componentItem = new TextComponent(itemMeta.getDisplayName());

                        Bukkit.getLogger().info(itemMeta.getDisplayName());

                        player.sendMessage(componentPlayer1, componentAmount, componentItem, componentPlayer2);
                        partnerPlayer.sendMessage(componentPartner1, componentAmount, componentItem, componentPartner2);
                    } else {
                        TranslatableComponent componentItem = new TranslatableComponent(Utils.getItemKey(material));

                        player.sendMessage(componentPlayer1, componentAmount, componentItem, componentPlayer2);
                        partnerPlayer.sendMessage(componentPartner1, componentAmount, componentItem, componentPartner2);
                    }



                }

            }
        }
    }
}
