package de.codemakers.bot.supreme.plugin;

/**
 * Plugin
 *
 * @author Panzer1119
 */
public interface Plugin {
    
    public boolean setProvider(PluginProvider provider);
    
    public boolean preInit();
    
    public boolean init();
    
    public boolean pastInit();
    
    public boolean reload();
    
    public String getID();
    
    public String getPermissionID();
    
}
