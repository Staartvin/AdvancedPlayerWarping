package me.staartvin.plugins.advancedplayerwarping.conversations.filterwarps;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RequestPlayerNamePrompt extends StringPrompt {

    private AdvancedPlayerWarping plugin;

    public RequestPlayerNamePrompt() {
        super();

        this.plugin = (AdvancedPlayerWarping) Bukkit.getPluginManager().getPlugin("AdvancedPlayerWarping");
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Message.FILTER_SEARCH_WARPS_BY_PLAYER_NAME_PROMPT.getTranslatedMessage();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        Player player = (Player) conversationContext.getForWhom();

        String targetPlayerName = s.trim();

        if (targetPlayerName.equals("")) {
            player.sendMessage(Message.FILTER_SEARCH_WARPS_BY_PLAYER_NAME.getTranslatedMessage());
            return this;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        if (!targetPlayer.hasPlayedBefore()) {
            player.sendMessage(Message.WHITELIST_UNKNOWN_PLAYER_PROVIDED.getTranslatedMessage());
            return this;
        }

        UUID targetUUID = targetPlayer.getUniqueId();

        conversationContext.setSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
        conversationContext.setSessionData(PluginConversation.FILTER_WARP_BY_USER_IDENTIFIER, targetUUID);

        return END_OF_CONVERSATION;
    }
}
