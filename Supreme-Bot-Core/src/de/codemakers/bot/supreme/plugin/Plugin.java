package de.codemakers.bot.supreme.plugin;

/**
 * Plugin
 *
 * @author Panzer1119
 */
public abstract class Plugin {

    protected PluginProviderPlus provider = null;

    public final boolean setProvider(PluginProvider provider) {
        this.provider = new PluginProviderPlus(provider, this);
        return this.provider != null;
    }
    
    public final PluginProviderPlus getProvider() {
        return provider;
    }

    public abstract boolean preInit();

    public abstract boolean init();

    public abstract boolean pastInit();

    public abstract boolean reload();

    public abstract String getID();

    public abstract String getPermissionID();

}
