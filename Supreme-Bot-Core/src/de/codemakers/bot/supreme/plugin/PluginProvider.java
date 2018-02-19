package de.codemakers.bot.supreme.plugin;

import de.codemakers.bot.supreme.entities.AdvancedGuild;
import java.io.Serializable;

/**
 * PluginProvider
 *
 * @author Panzer1119
 */
public interface PluginProvider extends Serializable {

    public boolean print(Plugin plugin, String print, Object... args);

    public boolean println(Plugin plugin, String print, Object... args);

    public boolean register(Plugin plugin, Object id, Object object, RegisterType type);

    public Object get(Plugin plugin, Object id, Object... options);

    public AdvancedGuild getAdvancedGuild(Plugin plugin, long guild_id);

}
