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

    public AdvancedGuild getAdvancedGuild(Plugin plugin, String guild_id);

}
