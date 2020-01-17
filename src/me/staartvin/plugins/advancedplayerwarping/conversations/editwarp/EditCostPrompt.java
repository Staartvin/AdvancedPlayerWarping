package me.staartvin.plugins.advancedplayerwarping.conversations.editwarp;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class EditCostPrompt extends StringPrompt {

    private AdvancedPlayerWarping plugin;
    private Warp warpToEdit;

    public EditCostPrompt(Warp warpToEdit) {
        super();

        this.plugin = (AdvancedPlayerWarping) Bukkit.getPluginManager().getPlugin("AdvancedPlayerWarping");
        this.warpToEdit = warpToEdit;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Message.COST_PROMPT_QUESTION.getTranslatedMessage();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        Player player = (Player) conversationContext.getForWhom();

        double cost = 0.0f;

        try {
            cost = Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            player.sendMessage(Message.COST_INPUT_NOT_A_NUMBER.getTranslatedMessage());
            return this;
        }

        double minCost = plugin.getConfigurationManager().getMinimumUsageCost();
        double maxCost = plugin.getConfigurationManager().getMaximumUsageCost();

        if (cost < minCost) {
            player.sendMessage(Message.COST_MINIMUM_COST.getTranslatedMessage(minCost));
            return this;
        }

        if (cost > maxCost) {
            player.sendMessage(Message.COST_MAXIMUM_COST.getTranslatedMessage(maxCost));
            return this;
        }

        if (plugin.getEconomyManager().isEconomySupported()) {

            double pay = plugin.getConfigurationManager().getEditCostCost();

            if (!plugin.getEconomyManager().withdrawFunds(player.getUniqueId(), pay)) {
                player.sendMessage(Message.WARPS_INSUFFICIENT_FUNDS.getTranslatedMessage(
                        plugin.getEconomyManager().getMissingFunds(player.getUniqueId(), pay)));
                return END_OF_CONVERSATION;
            }
        }

        warpToEdit.setCost(cost);
        plugin.getWarpManager().getWarpStorageProvider().saveWarp(warpToEdit);

        conversationContext.setSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
        conversationContext.setSessionData(PluginConversation.EDITED_WARP_IDENTIFIER, warpToEdit);

        return END_OF_CONVERSATION;
    }
}
