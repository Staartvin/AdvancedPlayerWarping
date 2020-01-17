package me.staartvin.plugins.advancedplayerwarping.conversations.editwarp;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.language.Message;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class RelocateWarpPrompt extends StringPrompt {

    private AdvancedPlayerWarping plugin;
    private Warp warpToEdit;

    public RelocateWarpPrompt(Warp warpToEdit) {
        super();

        this.plugin = (AdvancedPlayerWarping) Bukkit.getPluginManager().getPlugin("AdvancedPlayerWarping");
        this.warpToEdit = warpToEdit;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return ChatColor.GOLD + "Go to the new location and type " + ChatColor.LIGHT_PURPLE + "here";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        Player player = (Player) conversationContext.getForWhom();

        Location location = player.getLocation();

        if (plugin.getEconomyManager().isEconomySupported()) {

            double cost = plugin.getConfigurationManager().getEditRelocateCost();

            if (!plugin.getEconomyManager().withdrawFunds(player.getUniqueId(), cost)) {
                player.sendMessage(Message.WARPS_INSUFFICIENT_FUNDS.getTranslatedMessage(
                        plugin.getEconomyManager().getMissingFunds(player.getUniqueId(), cost)));
                return END_OF_CONVERSATION;
            }
        }

        warpToEdit.setDestination(location);
        plugin.getWarpManager().getWarpStorageProvider().saveWarp(warpToEdit);

        conversationContext.setSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
        conversationContext.setSessionData(PluginConversation.EDITED_WARP_IDENTIFIER, warpToEdit);

        return END_OF_CONVERSATION;
    }
}
