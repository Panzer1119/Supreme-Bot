package de.codemakers.bot.supreme.plugin;

import de.codemakers.bot.supreme.settings.DefaultSettings;
import de.codemakers.bot.supreme.settings.Settings;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.plugin.PluginLoader;
import de.codemakers.plugin.impl.StandardPluginFilter;
import java.io.File;
import java.util.ArrayList;

/**
 * PluginManager
 *
 * @author Panzer1119
 */
public class PluginManager implements PluginProvider {

    private final PluginLoader pluginLoader;
    private final ArrayList<Plugin> plugins = new ArrayList<>();

    public PluginManager() {
        this(new PluginLoader().setPluginFilter(StandardPluginFilter.createInstance(Plugin.class)));
    }

    public PluginManager(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
        PluginLoader.DEBUG_MODE = true;
    }

    protected final PluginLoader getPluginLoader() {
        return pluginLoader;
    }

    public final PluginManager loadPlugins(File... files) {
        if (pluginLoader.loadPlugins(files)) {
            plugins.clear();
            plugins.addAll(pluginLoader.getPluggables(Plugin.class));
            plugins.stream().forEach((plugin) -> {
                plugin.setProvider(this);
            });
        }
        return this;
    }
    
    protected final String format(Plugin plugin, String print, Object... args) {
        if (args == null || args.length == 0) {
            return String.format("[%s]: %s", plugin.getID(), print);
        } else {
            return String.format(String.format("[%s]: %s", plugin.getID(), print), args);
        }
    }

    @Override
    public boolean print(Plugin plugin, String print, Object... args) {
        System.out.print(format(plugin, print, args));
        return true;
    }

    @Override
    public boolean register(Plugin plugin, Object object, RegisterType type) {
        if (plugin == null || object == null || type == null) {
            return false;
        }
        switch (type) {
            case COMMAND:
                System.out.println(format(plugin, "Registered Command: \"%s\"", object));
                return true;
            case LISTENER:
                return false;
            default:
                return false;
        }
    }

    @Override
    public Settings getSettings(Plugin plugin, String guild_id) {
        if (plugin == null || guild_id == null || guild_id.isEmpty()) {
            return ((DefaultSettings) Standard.STANDARD_NULL_SETTINGS).toSimpleSettings(this, plugin, guild_id);
        }
        final DefaultSettings defaultSettings = (DefaultSettings) Standard.getGuildSettings(guild_id);
        if (defaultSettings == null) {
            return ((DefaultSettings) Standard.STANDARD_NULL_SETTINGS).toSimpleSettings(this, plugin, guild_id);
        } else {
            return defaultSettings.toSimpleSettings(this, plugin, guild_id);
        }
    }

    @Override
    public boolean setSettings(Plugin plugin, String guild_id, Settings settings) {
        if (plugin == null || guild_id == null || guild_id.isEmpty() || settings == null) {
            return false;
        }
        final DefaultSettings defaultSettings = (DefaultSettings) Standard.getGuildSettings(guild_id);
        if (defaultSettings == null) {
            return false;
        } else {
            defaultSettings.setSettings(settings.getSettings());
            return true;
        }
    }

}
