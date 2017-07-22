package de.codemakers.bot.supreme.plugin;

/**
 * Plugin
 *
 * @author Panzer1119
 */
public interface Plugin {
    
    public boolean setProvider(PluginProvider provider);
    
    public String getID();
    
    public String getPermissionID();
    
}
