package de.codemakers.bot.supreme.plugin;

import de.codemakers.bot.supreme.settings.Settings;

/**
 * PluginProvider
 *
 * @author Panzer1119
 */
public interface PluginProvider {
    
    public boolean print(Plugin plugin, String print, Object... args);
    
    public boolean register(Plugin plugin, Object object, RegisterType type);
    
    public Settings getSettings(Plugin plugin, String guild_id);
    
    public boolean setSettings(Plugin plugin, String guild_id, Settings settings);
    
}
