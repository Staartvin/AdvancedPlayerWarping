package me.staartvin.plugins.advancedplayerwarping.conversations.editwarp;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class EditWhitelistPrompt extends StringPrompt {

    private AdvancedPlayerWarping plugin;
    private Warp warpToEdit;

    public EditWhitelistPrompt(Warp warpToEdit) {
        super();

        this.plugin = (AdvancedPlayerWarping) Bukkit.getPluginManager().getPlugin("AdvancedPlayerWarping");
        this.warpToEdit = warpToEdit;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Message.WHITELIST_PROMPT_QUESTION.getTranslatedMessage();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        Player player = (Player) conversationContext.getForWhom();

        String targetPlayerName = s.trim();

        boolean addPlayer = false;

        if (targetPlayerName.contains("add ")) {
            addPlayer = true;
            targetPlayerName = targetPlayerName.replace("add", "").trim();
        } else if (targetPlayerName.contains("remove ")) {
            addPlayer = false;
            targetPlayerName = targetPlayerName.replace("remove", "").trim();
        } else {
            player.sendMessage(Message.WHITELIST_NO_ACTION_PROVIDED.getTranslatedMessage());
            return this;
        }

        if (targetPlayerName.trim().equals("")) {
            player.sendMessage(Message.WHITELIST_NO_PLAYER_PROVIDED.getTranslatedMessage());
            return this;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        if (!targetPlayer.hasPlayedBefore()) {
            player.sendMessage(Message.WHITELIST_UNKNOWN_PLAYER_PROVIDED.getTranslatedMessage());
            return this;
        }

        UUID targetUUID = targetPlayer.getUniqueId();

        List<UUID> whitelist = warpToEdit.getWhitelist();

        if (addPlayer) {
            if (whitelist.contains(targetUUID)) {
                player.sendMessage(Message.WHITELIST_PLAYER_ALREADY_WHITELISTED.getTranslatedMessage(targetPlayerName));
            } else {

                whitelist.add(targetUUID);

                player.sendMessage(Message.WHITELIST_PLAYER_ADDED_TO_WHITELIST.getTranslatedMessage(targetPlayerName));
            }
        } else {
            if (whitelist.contains(targetUUID)) {
                whitelist.remove(targetUUID);
                player.sendMessage(Message.WHITELIST_PLAYER_REMOVED_FROM_WHITELIST.getTranslatedMessage(targetPlayerName));
            } else {
                player.sendMessage(Message.WHITELIST_PLAYER_NOT_WHITELISTED.getTranslatedMessage(targetPlayerName));
            }
        }

        if (plugin.getEconomyManager().isEconomySupported()) {

            double cost = plugin.getConfigurationManager().getEditWhitelistCost();

            if (!plugin.getEconomyManager().withdrawFunds(player.getUniqueId(), cost)) {
                player.sendMessage(Message.WARPS_INSUFFICIENT_FUNDS.getTranslatedMessage(
                        plugin.getEconomyManager().getMissingFunds(player.getUniqueId(), cost)));
                return this;
            }
        }

        warpToEdit.setWhitelist(whitelist);


        // Save warp
        plugin.getWarpManager().getWarpStorageProvider().saveWarp(warpToEdit);

        conversationContext.setSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
        conversationContext.setSessionData(PluginConversation.EDITED_WARP_IDENTIFIER, warpToEdit);

        return END_OF_CONVERSATION;
    }
}
