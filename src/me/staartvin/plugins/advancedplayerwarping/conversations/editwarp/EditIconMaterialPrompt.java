package me.staartvin.plugins.advancedplayerwarping.conversations.editwarp;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpIcon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class EditIconMaterialPrompt extends StringPrompt {

    private AdvancedPlayerWarping plugin;
    private Warp warpToEdit;

    public EditIconMaterialPrompt(Warp warpToEdit) {
        super();

        this.plugin = (AdvancedPlayerWarping) Bukkit.getPluginManager().getPlugin("AdvancedPlayerWarping");
        this.warpToEdit = warpToEdit;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Message.ICON_PROMPT_QUESTION.getTranslatedMessage();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        Player player = (Player) conversationContext.getForWhom();

        Material material;

        try {
            material = Material.valueOf(s.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            player.sendMessage(Message.ICON_INVALID_MATERIAL.getTranslatedMessage());
            return this;
        }

        if (plugin.getConfigurationManager().getBlacklistedIcons().contains(material)) {
            player.sendMessage(Message.ICON_USING_FORBIDDEN_MATERIAL.getTranslatedMessage());
            return this;
        }

        if (plugin.getEconomyManager().isEconomySupported()) {

            double cost = plugin.getConfigurationManager().getEditIconCost();

            if (!plugin.getEconomyManager().withdrawFunds(player.getUniqueId(), cost)) {
                player.sendMessage(Message.WARPS_INSUFFICIENT_FUNDS.getTranslatedMessage(
                        plugin.getEconomyManager().getMissingFunds(player.getUniqueId(), cost)));
                return END_OF_CONVERSATION;
            }
        }

        warpToEdit.setIcon(new WarpIcon(material, warpToEdit.getIcon().isEnchanted()));
        plugin.getWarpManager().getWarpStorageProvider().saveWarp(warpToEdit);

        conversationContext.setSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
        conversationContext.setSessionData(PluginConversation.EDITED_WARP_IDENTIFIER, warpToEdit);

        return END_OF_CONVERSATION;
    }
}
