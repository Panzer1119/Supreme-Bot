package de.codemakers.bot.supreme.plugin;

/**
 * CommandPluggableManager
 *
 * @author Panzer1119
 */
public interface CommandPluggableManager {
    
    public boolean print(CommandPluggable commandPlugin, String print, Object... args);
    
}
