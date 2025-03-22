package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestAdopt;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdoptDeny extends FamilyCommand implements HasParentCommand {

    private static final AdoptDeny INSTANCE = new AdoptDeny();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE,"help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Deny an adoption request.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Lehne eine Adoptionsanfrage ab.");
    private static final CommandMessageKey DENIED_MK = new LunaticCommandMessageKey(INSTANCE,"denied")
            .defaultMessage("en", "Sorry, %child% denied your adoption request.")
            .defaultMessage("de", "Entschuldigung, %child% hat deine Adoptionsanfrage abgelehnt.");
    private static final CommandMessageKey DENY_MK = new LunaticCommandMessageKey(INSTANCE,"deny")
            .defaultMessage("en", "You denied the adoption request from %parent%.")
            .defaultMessage("de", "Du hast die Adoptionsanfrage von %parent% abgelehnt.");
    private static final CommandMessageKey NO_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE,"no_request")
            .defaultMessage("en", "You have no pending adoption requests.")
            .defaultMessage("de", "Du hast keine ausstehenden Adoptionsanfragen.");


    private static final PriestAdopt PRIEST_ADOPT_INSTANCE = new PriestAdopt();

    private static final CommandMessageKey PRIEST_NO_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"no")
            .defaultMessage("en", "No, I don't want to.")
            .defaultMessage("de", "Nein, ich will nicht.");
    private static final CommandMessageKey PRIEST_CANCEL_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"cancel")
            .defaultMessage("en", "The adoption has been canceled.")
            .defaultMessage("de", "Die Adoption wurde abgebrochen.");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public Adopt getParentCommand() {
        return new Adopt();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = FamilyPlayer.find(playerUUID);

        if (LunaticFamily.adoptRequests.containsKey(playerUUID)) {
            UUID partnerUUID = LunaticFamily.adoptRequests.get(playerUUID);
            PlayerSender parent = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
            if (!LunaticFamily.adoptPriests.containsKey(partnerUUID)) {
                player.sendMessage(getMessage(DENY_MK,
                placeholder("%parent%", parent.getName())));
                parent.sendMessage(getMessage(DENIED_MK,
                placeholder("%child%", playerFam.getName())));
            } else {
                UUID priestUUID = LunaticFamily.adoptPriests.get(partnerUUID);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                player.chat(getLanguageConfig().getMessageAsString(PRIEST_NO_MK.noPrefix()));

                Runnable runnable = () -> {
                    priest.chat(getLanguageConfig().getMessageAsString(PRIEST_CANCEL_MK.noPrefix()));
                };

                Utils.scheduleTask(runnable, 250, TimeUnit.MILLISECONDS);


                LunaticFamily.adoptPriests.remove(partnerUUID);
            }
            LunaticFamily.adoptRequests.remove(playerUUID);
            return true;
        }

        if (LunaticFamily.adoptPriestRequests.containsKey(playerUUID)) {
            player.chat(getLanguageConfig().getMessageAsString(PRIEST_NO_MK.noPrefix()));
            UUID priestUUID = LunaticFamily.adoptPriests.get(playerUUID);
            PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
            priest.chat(getLanguageConfig().getMessageAsString(PRIEST_CANCEL_MK.noPrefix()));
            LunaticFamily.adoptPriestRequests.remove(playerUUID);
            LunaticFamily.adoptPriests.remove(playerUUID);
            return true;
        }

        sender.sendMessage(getMessage(NO_REQUEST_MK));

        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
