package de.codemakers.bot.supreme.plugin;

/**
 * CommandPluggable
 *
 * @author Panzer1119
 */
public interface CommandPluggable {
    
    public boolean setManager(CommandPluggableManager manager);
    
    public String getName();
    
}
