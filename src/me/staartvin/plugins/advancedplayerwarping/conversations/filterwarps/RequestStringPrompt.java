package me.staartvin.plugins.advancedplayerwarping.conversations.filterwarps;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class RequestStringPrompt extends StringPrompt {

    private AdvancedPlayerWarping plugin;

    public RequestStringPrompt() {
        super();

        this.plugin = (AdvancedPlayerWarping) Bukkit.getPluginManager().getPlugin("AdvancedPlayerWarping");
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Message.FILTER_SEARCH_WARPS_BY_STRING_PROMPT.getTranslatedMessage();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        conversationContext.setSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
        conversationContext.setSessionData(PluginConversation.FILTER_WARP_BY_STRING_IDENTIFIER, s);

        return END_OF_CONVERSATION;
    }
}
