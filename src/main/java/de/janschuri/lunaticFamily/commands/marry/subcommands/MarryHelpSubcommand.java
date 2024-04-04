package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class MarryHelpSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.marry";
    private static final Subcommand[] subcommands = {
            new MarryAcceptSubcommand(),
            new MarryBackpackSubcommand(),
            new MarryDenySubcommand(),
            new MarryDivorceSubcommand(),
            new MarryDivorceSubcommand(),
            new MarryGiftSubcommand(),
            new MarryKissSubcommand(),
            new MarryListSubcommand(),
            new MarryPriestSubcommand(),
            new MarryProposeSubcommand(),
            new MarrySetSubcommand(),
            new MarryUnsetSubcommand()
    };

    public MarryHelpSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            TextComponent msg = new TextComponent(Language.getMessage(mainCommand + "_help") + "\n");

            for (Subcommand subcommand : subcommands) {
                msg.addExtra(subcommand.getHelp(sender));
            }

            sender.sendMessage(msg);
        }
    }
}
