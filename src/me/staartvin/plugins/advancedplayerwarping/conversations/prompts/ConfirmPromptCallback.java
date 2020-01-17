package me.staartvin.plugins.advancedplayerwarping.conversations.prompts;

/**
 * Interface represents a callback from the {@link ConfirmPrompt}.
 */
public interface ConfirmPromptCallback {

    /**
     * The prompt has been confirmed.
     */
    void promptConfirmed();

    /**
     * The prompt has been denied.
     */
    void promptDenied();
}
