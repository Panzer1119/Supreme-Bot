package de.codemakers.bot.supreme.plugin;

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
        PluginLoader.PRECISE_DEBUG_MODE = true;
    }

    protected final PluginLoader getPluginLoader() {
        return pluginLoader;
    }

    public final PluginManager loadPlugins(File... files) {
        if (pluginLoader.loadPlugins(files)) {
            plugins.clear();
            plugins.addAll(pluginLoader.getPluggables());
            plugins.stream().forEach((commandPlugin) -> {
                commandPlugin.setProvider(this);
            });
        }
        return this;
    }

    @Override
    public boolean print(Plugin commandPlugin, String print, Object... args) {
        if (args == null || args.length == 0) {
            System.out.print(String.format("[%s]: %s", commandPlugin.getName(), print));
        } else {
            System.out.printf(String.format("[%s]: %s", commandPlugin.getName(), print), args);
        }
        return true;
    }

}
