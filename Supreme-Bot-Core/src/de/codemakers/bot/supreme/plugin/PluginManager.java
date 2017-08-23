package de.codemakers.bot.supreme.plugin;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.listeners.Listener;
import de.codemakers.bot.supreme.listeners.ListenerManager;
import de.codemakers.bot.supreme.settings.DefaultSettings;
import de.codemakers.bot.supreme.settings.Settings;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.plugin.PluginLoader;
import de.codemakers.plugin.impl.StandardPluginFilter;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * PluginManager
 *
 * @author Panzer1119
 */
public class PluginManager implements PluginProvider {

    private final PluginLoader pluginLoader;
    private final ArrayList<Plugin> plugins = new ArrayList<>();
    private final HashMap<Object, Map.Entry<RegisterType, Object>> registeredObjects = new HashMap<>();

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
            final StringBuilder sb = new StringBuilder();
            sb.append("Loaded Plugins: ");
            plugins.stream().forEach((plugin) -> {
                if (plugin == null) {
                    return;
                }
                if (plugin.setProvider(this)) {
                    sb.append("\"");
                    sb.append(plugin.getID());
                    sb.append("\", ");
                }
            });
            sb.delete(sb.length() - ", ".length(), sb.length());
            if (!plugins.isEmpty()) {
                System.out.println(sb.toString());
            } else {
                System.out.println("Loaded no Plugins!");
            }
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

    /**
     * 
     * @param plugin
     * @param id
     * @param object If null then this is counted as an unregistration
     * @param type
     * @return 
     */
    @Override
    public boolean register(Plugin plugin, Object id, Object object, RegisterType type) {
        if (plugin == null || id == null || (registeredObjects.containsKey(id) && object != null)) {
            return false;
        }
        final boolean unregister = (registeredObjects.containsKey(id) && object == null && registeredObjects.get(id).getKey() == type);
        switch (type) {
            case COMMAND:
                if (object != null && object instanceof Command) {
                    CommandHandler.registerCommand((Command) object);
                    registeredObjects.put(id, new AbstractMap.SimpleEntry<>(type, object));
                    System.out.println(format(plugin, "Registered Command: \"%s\"", object));
                    return true;
                } else if (unregister) {
                    final Object o = registeredObjects.get(id).getValue();
                    if (o != null && (o instanceof Command)) {
                        System.out.println(format(plugin, "Unregistered Command: \"%s\"", o));
                        return CommandHandler.unregisterCommand((Command) o);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            case LISTENER:
                if (object instanceof Listener) {
                    System.out.println(format(plugin, "Registered Listener: \"%s\"", object));
                    return ListenerManager.registerListener(id, (Listener) object);
                }
                System.out.println(format(plugin, "Not registered Listener: \"%s\"", object));
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
