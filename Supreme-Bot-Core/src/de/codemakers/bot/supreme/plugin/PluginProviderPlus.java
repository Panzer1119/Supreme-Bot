package de.codemakers.bot.supreme.plugin;

import de.codemakers.bot.supreme.entities.AdvancedGuild;

/**
 * PluginProviderPlus
 *
 * @author Panzer1119
 */
public class PluginProviderPlus {

    private final PluginProvider provider;
    private final Plugin plugin;

    public PluginProviderPlus(PluginProvider provider, Plugin plugin) {
        this.provider = provider;
        this.plugin = plugin;
    }

    public final Plugin getPlugin() {
        return plugin;
    }

    public final boolean print(String print, Object... args) {
        return provider.print(plugin, print, args);
    }

    public final boolean register(Object id, Object object, RegisterType type) {
        return provider.register(plugin, id, object, type);
    }

    public final AdvancedGuild getAdvancedGuild(String guild_id) {
        return provider.getAdvancedGuild(plugin, guild_id);
    }

}
