package me.staartvin.plugins.advancedplayerwarping.conversations;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;

/**
 * This class is used to determine when a conversation is ended. When it's ended, we must trace back to the proper
 * {@link PluginConversation} object so that we can call signal that it has ended and we can call the callback.
 * Furthermore, we'll let the conversable know that the conversation has ended (and why).
 */
public class ConversationAbandonedEvent implements ConversationAbandonedListener {
    @Override
    public void conversationAbandoned(org.bukkit.conversations.ConversationAbandonedEvent conversationAbandonedEvent) {
        Object conversationObject =
                conversationAbandonedEvent.getContext().getSessionData(PluginConversation.CONVERSATION_IDENTIFIER);

        // There was not AutorankConversation responsible for this conversation
        if (conversationObject == null) {
            return;
        }

        PluginConversation conversation = (PluginConversation) conversationObject;

        ConversationResult result;

        // Check if we have an object to indicate that the conversation was successful.
        Object endedSuccesfully =
                conversationAbandonedEvent.getContext().getSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER);

        Conversable conversable = conversationAbandonedEvent.getContext().getForWhom();

        // Build result object.
        if (endedSuccesfully == null) {
            result = new ConversationResult(false, conversable);
        } else {
            result = new ConversationResult((Boolean) endedSuccesfully, conversable);
        }

        // Set the conversation storage so it can be used later.
        result.setConversationStorage(conversationAbandonedEvent.getContext().getAllSessionData());

        ConversationCanceller canceller = conversationAbandonedEvent.getCanceller();

        // Inform the user that the conversation has ended and that the player may talk freely again.
        if (canceller instanceof InactivityConversationCanceller) {
            conversable.sendRawMessage(ChatColor.GRAY + "This conversation has ended because you didn't reply in time" +
                    ".");
        } else if (canceller instanceof ExactMatchConversationCanceller) {
            conversable.sendRawMessage(ChatColor.GRAY + "This conversation has been abandoned by you.");
            result.setEndedByKeyword(true);
        } else {
            conversable.sendRawMessage(ChatColor.GRAY + "This conversation has ended.");
        }

        // Indicate to conversation that it has ended.
        conversation.conversationEnded(result);
    }
}
