package de.codemakers.bot.supreme.plugin;

import de.codemakers.plugin.PluginLoader;
import de.codemakers.plugin.impl.StandardPluginFilter;
import java.io.File;
import java.util.ArrayList;

/**
 * CommandPluginManager
 *
 * @author Panzer1119
 */
public class CommandPluginManager implements CommandPluggableManager {
    
    private final PluginLoader pluginLoader;
    private final ArrayList<CommandPluggable> commandPlugins = new ArrayList<>();
    
    public CommandPluginManager() {
        this(new PluginLoader().setPluginFilter(StandardPluginFilter.createInstance(CommandPluggable.class)));
    }
    
    public CommandPluginManager(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
        PluginLoader.DEBUG_MODE = true;
        PluginLoader.PRECISE_DEBUG_MODE = true;
    }
    
    protected final PluginLoader getPluginLoader() {
        return pluginLoader;
    }
    
    public final CommandPluginManager loadPlugins(File... files) {
        if (pluginLoader.loadPlugins(files)) {
            commandPlugins.clear();
            commandPlugins.addAll(pluginLoader.getPluggables());
            commandPlugins.stream().forEach((commandPlugin) -> {
                commandPlugin.setManager(this);
            });
        }
        return this;
    }

    @Override
    public boolean print(CommandPluggable commandPlugin, String print, Object... args) {
        if (args == null || args.length == 0) {
            System.out.print(String.format("[%s]: %s", commandPlugin.getName(), print));
        } else {
            System.out.printf(String.format("[%s]: %s", commandPlugin.getName(), print), args);
        }
        return true;
    }

}
