package de.codemakers.bot.supreme.plugin;

import java.io.Serializable;

/**
 * Plugin
 *
 * @author Panzer1119
 */
public interface Plugin extends Serializable {

    public PluginProviderPlus provider = null;

    public boolean setProvider(PluginProviderPlus provider);

    public boolean preInit();

    public boolean init();

    public boolean postInit();

    public boolean reload();

    public String getID();

    public String getPermissionID();

}
