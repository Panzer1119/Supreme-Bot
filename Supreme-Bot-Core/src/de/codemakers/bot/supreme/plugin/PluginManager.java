package de.codemakers.bot.supreme.plugin;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.listeners.Listener;
import de.codemakers.bot.supreme.listeners.ListenerManager;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.io.file.AdvancedFile;
import de.codemakers.plugin.PluginLoader;
import de.codemakers.plugin.impl.StandardPluginFilter;
import de.codemakers.util.MultiFunction;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PluginManager
 *
 * @author Panzer1119
 */
public class PluginManager implements PluginProvider {

    private final PluginLoader pluginLoader;
    private final List<Plugin> plugins = new ArrayList<>();
    private final Map<Object, Map.Entry<RegisterType, Object>> registeredObjects = new HashMap<>();
    private final Map<Object, MultiFunction<Object, Object>> functions = new HashMap<>();

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

    public final PluginManager loadPlugins(AdvancedFile... files) {
        if (pluginLoader.loadPlugins(AdvancedFile.toFiles(files))) {
            plugins.clear();
            plugins.addAll(pluginLoader.getPluggables(Plugin.class));
            final StringBuilder sb = new StringBuilder();
            sb.append("Loaded Plugins: ");
            plugins.stream().forEach((plugin) -> {
                if (plugin == null) {
                    return;
                }
                if (plugin.setProvider(new PluginProviderPlus(this, plugin))) {
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
            sb.delete(0, sb.length());
            sb.append("Pre-Initialized Plugins: ");
            plugins.stream().forEach((plugin) -> {
                if (plugin == null) {
                    return;
                }
                if (plugin.preInit()) {
                    sb.append("\"");
                    sb.append(plugin.getID());
                    sb.append("\", ");
                }
            });
            sb.delete(sb.length() - ", ".length(), sb.length());
            if (!plugins.isEmpty()) {
                System.out.println(sb.toString());
            } else {
                System.out.println("Pre-Initialized no Plugins!");
            }
            sb.delete(0, sb.length());
            sb.append("Initialized Plugins: ");
            plugins.stream().forEach((plugin) -> {
                if (plugin == null) {
                    return;
                }
                if (plugin.init()) {
                    sb.append("\"");
                    sb.append(plugin.getID());
                    sb.append("\", ");
                }
            });
            sb.delete(sb.length() - ", ".length(), sb.length());
            if (!plugins.isEmpty()) {
                System.out.println(sb.toString());
            } else {
                System.out.println("Initialized no Plugins!");
            }
            sb.delete(0, sb.length());
            sb.append("Post-Initialized Plugins: ");
            plugins.stream().forEach((plugin) -> {
                if (plugin == null) {
                    return;
                }
                if (plugin.postInit()) {
                    sb.append("\"");
                    sb.append(plugin.getID());
                    sb.append("\", ");
                }
            });
            sb.delete(sb.length() - ", ".length(), sb.length());
            if (!plugins.isEmpty()) {
                System.out.println(sb.toString());
            } else {
                System.out.println("Post-Initialized no Plugins!");
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
    public final boolean print(Plugin plugin, String print, Object... args) {
        System.out.print(format(plugin, print, args));
        return true;
    }

    @Override
    public final boolean println(Plugin plugin, String print, Object... args) {
        System.out.println(format(plugin, print, args));
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
    public final boolean register(Plugin plugin, Object id, Object object, RegisterType type) {
        if (plugin == null || id == null || (registeredObjects.containsKey(id) && object != null)) {
            return false;
        }
        final boolean unregister = (registeredObjects.containsKey(id) && object == null && registeredObjects.get(id).getKey() == type);
        switch (type) {
            case COMMAND:
                if (object != null && object instanceof Command) {
                    CommandHandler.registerCommand((Command) object);
                    registeredObjects.put(id, new AbstractMap.SimpleEntry<>(type, object));
                    System.out.println(format(plugin, "Registered Command: \"%s\" (ID: \"%s\")", object, id));
                    return true;
                } else if (unregister) {
                    final Object o = registeredObjects.get(id).getValue();
                    if (o != null && (o instanceof Command)) {
                        System.out.println(format(plugin, "Unregistered Command: \"%s\" (ID: \"%s\")", o, id));
                        return CommandHandler.unregisterCommand((Command) o);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            case LISTENER:
                if (object instanceof Listener) {
                    System.out.println(format(plugin, "Registered Listener: \"%s\" (ID: \"%s\"", object, id));
                    return ListenerManager.registerListener(id, (Listener) object);
                }
                System.out.println(format(plugin, "Not registered Listener: \"%s\"", object));
                return false;
            default:
                return false;
        }
    }

    @Override
    public final Object get(Plugin plugin, Object id, Object... options) {
        return functions.get(id).apply(options);
    }

    @Override
    public final AdvancedGuild getAdvancedGuild(Plugin plugin, long guild_id) {
        if (plugin == null || guild_id == 0) {
            return null;
        }
        return Standard.getAdvancedGuild(guild_id);
    }

    final List<Plugin> getPlugins() {
        return plugins;
    }

    public final Map<Object, Map.Entry<RegisterType, Object>> getRegisteredObjects() {
        return registeredObjects;
    }

    public final Map<Object, MultiFunction<Object, Object>> getFunctions() {
        return functions;
    }

}
