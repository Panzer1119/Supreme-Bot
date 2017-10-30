package de.codemakers.bot.supreme.entities;

import de.codemakers.bot.supreme.listeners.ReactionListener;
import de.codemakers.bot.supreme.permission.ReactionPermissionFilter;
import de.codemakers.bot.supreme.util.Timeout;

/**
 * ReactionContainer
 *
 * @author Panzer1119
 */
public class ReactionContainer {

    public final Timeout timeout;
    public final ReactionListener listener;
    public final ReactionPermissionFilter filter;
    public final boolean removeReaction;

    public ReactionContainer(Timeout timeout, ReactionListener listener, ReactionPermissionFilter filter, boolean removeReaction) {
        this.timeout = timeout;
        this.listener = listener;
        this.filter = filter;
        this.removeReaction = removeReaction;
    }

}
