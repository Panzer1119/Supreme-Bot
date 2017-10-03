package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.plugin.Plugin;

/**
 * Listener
 *
 * @author Panzer1119
 */
public interface Listener {

    public Object fired(Object... data);

    default Plugin getPlugin() {
        return null;
    }

}
