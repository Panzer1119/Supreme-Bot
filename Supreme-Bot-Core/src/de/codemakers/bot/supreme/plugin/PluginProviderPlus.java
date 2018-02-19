package de.codemakers.bot.supreme.plugin;

import de.codemakers.bot.supreme.entities.AdvancedGuild;
import java.io.Serializable;

/**
 * PluginProviderPlus
 *
 * @author Panzer1119
 */
public class PluginProviderPlus implements Serializable {

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

    public final boolean println(String print, Object... args) {
        return provider.println(plugin, print, args);
    }

    public final boolean register(Object id, Object object, RegisterType type) {
        return provider.register(plugin, id, object, type);
    }

    public final Object get(Object id, Object... options) {
        return provider.get(plugin, id, options);
    }

    public final AdvancedGuild getAdvancedGuild(long guild_id) {
        return provider.getAdvancedGuild(plugin, guild_id);
    }

}
