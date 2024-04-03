package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MarryDenySubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.marry";
    private static final List<String> aliases = Language.getAliases("marry", "deny");

    public MarryDenySubcommand() {
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

            if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_deny_no_request"));
            } else {
                if (LunaticFamily.marryRequests.containsKey(playerUUID)) {
                    String partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                    FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
                    if (!LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                        partnerFam.sendMessage(Language.prefix + Language.getMessage("marry_deny_denied").replace("%player%", playerFam.getName()));
                    } else {
                        String priestUUID = LunaticFamily.marryPriest.get(partnerUUID);
                        FamilyPlayer priestFam = new FamilyPlayer(priestUUID);
                        player.chat(Language.getMessage("marry_deny_no"));
                        priestFam.chat(Language.getMessage("marry_deny_cancel"));
                        LunaticFamily.marryPriest.remove(partnerUUID);
                    }
                    LunaticFamily.marryRequests.remove(playerUUID);

                } else if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                    player.chat(Language.getMessage("marry_deny_no"));
                    String priestUUID = LunaticFamily.marryPriest.get(playerUUID);
                    FamilyPlayer priestFam = new FamilyPlayer(priestUUID);
                    priestFam.chat(Language.getMessage("marry_deny_cancel"));
                    LunaticFamily.marryPriestRequests.remove(playerUUID);
                    LunaticFamily.marryPriest.remove(playerUUID);
                }
            }
        }
    }
}
