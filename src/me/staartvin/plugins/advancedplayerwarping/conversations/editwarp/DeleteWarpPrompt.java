package me.staartvin.plugins.advancedplayerwarping.conversations.editwarp;

import me.staartvin.plugins.advancedplayerwarping.AdvancedPlayerWarping;
import me.staartvin.plugins.advancedplayerwarping.conversations.PluginConversation;
import me.staartvin.plugins.advancedplayerwarping.warps.Warp;
import me.staartvin.plugins.advancedplayerwarping.warps.WarpIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class DeleteWarpPrompt extends StringPrompt {

    private AdvancedPlayerWarping plugin;
    private Warp warpToEdit;

    public DeleteWarpPrompt(Warp warpToEdit) {
        super();

        this.plugin = (AdvancedPlayerWarping) Bukkit.getPluginManager().getPlugin("AdvancedPlayerWarping");
        this.warpToEdit = warpToEdit;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return ChatColor.GOLD + "Are you sure you want to delete '" + ChatColor.LIGHT_PURPLE + warpToEdit.getDisplayName() + ChatColor.GOLD +"'?";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {

        Player player = (Player) conversationContext.getForWhom();

        boolean sure = false;

        if (s.equalsIgnoreCase("yes")) {
            sure = true;
        }

        if (!sure) {
            return END_OF_CONVERSATION;
        }

        plugin.getWarpManager().getWarpStorageProvider().deleteWarp(new WarpIdentifier(warpToEdit.getOwner(),
                warpToEdit.getDisplayName(false)));

        conversationContext.setSessionData(PluginConversation.CONVERSATION_SUCCESSFUL_IDENTIFIER, true);
        conversationContext.setSessionData(PluginConversation.EDITED_WARP_IDENTIFIER, warpToEdit);

        return END_OF_CONVERSATION;
    }
}
