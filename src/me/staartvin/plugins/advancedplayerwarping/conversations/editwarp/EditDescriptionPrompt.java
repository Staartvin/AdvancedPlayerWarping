package me.staartvin.plugins.advancedplayerwarping.conversations.editwarp;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import me.staartvin.plugins.advancedplayerwarping.permissions.PermissionManager;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class EditDescriptionPrompt extends StringPrompt {

    private AdvancedPlayerWarping plugin;
    private Warp warpToEdit;

    public EditDescriptionPrompt(Warp warpToEdit) {
        super();

        this.plugin = (AdvancedPlayerWarping) Bukkit.getPluginManager().getPlugin("AdvancedPlayerWarping");
        this.warpToEdit = warpToEdit;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Message.DESCRIPTION_PROMPT_QUESTION.getTranslatedMessage();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        Player player = (Player) conversationContext.getForWhom();

        if (s.trim().equals("")) {
            player.sendMessage(Message.DESCRIPTION_NO_DESCRIPTION_PROVIDED.getTranslatedMessage());
            return this;
        }

        List<String> description = Arrays.asList(s.split(","));

        List<String> forbiddenWords = plugin.getConfigurationManager().getBlacklistedDescriptions();

        boolean usingForbiddenWord = false;

      Pattern p = Pattern.compile("[&][abcdef0-9]");

        // Check for words that are forbidden.
        for (String string : description) {

            String lowerCase = string.toLowerCase();

            if (p.matcher(lowerCase).find() && !PermissionManager.canEditWarpDescriptionWithColors(player, warpToEdit)) {
                player.sendMessage(Message.DESCRIPTION_NO_COLOR_CODES_ALLOWED.getTranslatedMessage());
                return this;
            }

            for (String forbiddenWord : forbiddenWords) {
                if (lowerCase.contains(forbiddenWord.toLowerCase())) {
                    usingForbiddenWord = true;
                    break;
                }
            }
        }

        if (usingForbiddenWord) {
            player.sendMessage(Message.DESCRIPTION_USING_FORBIDDEN_WORDS.getTranslatedMessage());
            return this;
        }

        if (plugin.getEconomyManager().isEconomySupported()) {

            double cost = plugin.getConfigurationManager().getEditDescriptionCost();

            if (!plugin.getEconomyManager().withdrawFunds(player.getUniqueId(), cost)) {
                player.sendMessage(Message.WARPS_INSUFFICIENT_FUNDS.getTranslatedMessage(
                        plugin.getEconomyManager().getMissingFunds(player.getUniqueId(), cost)));
                return this;
            }
        }

        warpToEdit.setDescription(description);
        plugin.getWarpManager().getWarpStorageProvider().saveWarp(warpToEdit);

        conversationContext.setSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
        conversationContext.setSessionData(PluginConversation.EDITED_WARP_IDENTIFIER, warpToEdit);

        return END_OF_CONVERSATION;
    }
}
