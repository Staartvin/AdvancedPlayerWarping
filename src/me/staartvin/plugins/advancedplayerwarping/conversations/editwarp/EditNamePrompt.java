package me.staartvin.plugins.advancedplayerwarping.conversations.editwarp;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import me.staartvin.plugins.advancedplayerwarping.permissions.PermissionManager;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpIdentifier;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpType;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class EditNamePrompt extends StringPrompt {

    private AdvancedPlayerWarping plugin;
    private Warp warpToEdit;

    public EditNamePrompt(Warp warpToEdit) {
        super();

        this.plugin = (AdvancedPlayerWarping) Bukkit.getPluginManager().getPlugin("AdvancedPlayerWarping");
        this.warpToEdit = warpToEdit;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Message.NAME_PROMPT_QUESTION.getTranslatedMessage();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        Player player = (Player) conversationContext.getForWhom();

        boolean warpExists;

        if (warpToEdit.getType() == WarpType.SERVER) {
            warpExists = plugin.getWarpManager().warpExists(new WarpIdentifier(s));
        } else {
            warpExists = plugin.getWarpManager().warpExists(new WarpIdentifier(player.getUniqueId(), s));
        }

        if (warpExists) {
            player.sendMessage(Message.COMMANDS_CONFLICTING_WARP_NAME.getTranslatedMessage());
            return this;
        }

        Pattern p = Pattern.compile("[&][abcdef0-9]");

        // Check if there are color codes in the name.
        if (p.matcher(s).find() && !PermissionManager.canEditWarpNameWithColors(player, warpToEdit)) {
            player.sendMessage(Message.NAME_NO_COLOR_CODES_ALLOWED.getTranslatedMessage());
            return this;
        }

        List<String> forbiddenWords = plugin.getConfigurationManager().getBlacklistedWarpNames();

        boolean usingForbiddenWord = false;

        // Check for words that are forbidden.
        for (String forbiddenWord : forbiddenWords) {
            if (s.toLowerCase().contains(forbiddenWord.toLowerCase())) {
                usingForbiddenWord = true;
                break;
            }
        }

        if (usingForbiddenWord) {
            player.sendMessage(Message.NAME_USING_FORBIDDEN_WORDS.getTranslatedMessage());
            return this;
        }

        if (plugin.getEconomyManager().isEconomySupported()) {

            double cost = plugin.getConfigurationManager().getEditWarpNameCost();

            if (!plugin.getEconomyManager().withdrawFunds(player.getUniqueId(), cost)) {
                player.sendMessage(Message.WARPS_INSUFFICIENT_FUNDS.getTranslatedMessage(
                        plugin.getEconomyManager().getMissingFunds(player.getUniqueId(), cost)));
                return END_OF_CONVERSATION;
            }
        }

        warpToEdit.setDisplayName(s);
        plugin.getWarpManager().getWarpStorageProvider().saveWarp(warpToEdit);

        conversationContext.setSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
        conversationContext.setSessionData(PluginConversation.EDITED_WARP_IDENTIFIER, warpToEdit);

        return END_OF_CONVERSATION;
    }
}
