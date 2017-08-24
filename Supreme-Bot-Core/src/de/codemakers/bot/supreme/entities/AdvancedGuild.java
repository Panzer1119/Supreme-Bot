package de.codemakers.bot.supreme.entities;

import de.codemakers.bot.supreme.settings.DefaultSettings;
import de.codemakers.bot.supreme.settings.SimpleSettings;
import de.codemakers.bot.supreme.util.Standard;
import java.io.File;
import java.util.HashMap;
import net.dv8tion.jda.core.entities.Guild;

/**
 * AdvancedGuild
 *
 * @author Panzer1119
 */
public class AdvancedGuild {

    private Guild guild = null;
    private String guild_id = null;
    private File folder = null;
    private DefaultSettings settings = null;
    private final HashMap<Object, Object> data = new HashMap<>();

    public AdvancedGuild(Guild guild, File folder) {
        this(guild);
        this.folder = folder;
    }

    public AdvancedGuild(Guild guild) {
        this.guild = guild;
    }

    public AdvancedGuild(String guild_id, File folder) {
        this(guild_id);
        this.folder = folder;
    }

    public AdvancedGuild(String guild_id) {
        this.guild_id = guild_id;
    }

    public AdvancedGuild(File folder) {
        this.folder = folder;
    }

    public AdvancedGuild() {
        this((File) null);
    }

    public Object getData(Object key) {
        return getData(key, null);
    }

    public Object getData(Object key, Object defaultValue) {
        final Object object = data.get(key);
        if (object == null) {
            return defaultValue;
        } else {
            return object;
        }
    }

    public boolean putData(Object key, Object value) {
        if (value == null) {
            if (data.containsKey(key)) {
                data.remove(key);
                return true;
            } else {
                return false;
            }
        } else {
            data.put(key, value);
            return true;
        }
    }

    public final File getFile(String path) {
        if (getFolder() == null) {
            return null;
        }
        return new File(folder.getAbsolutePath() + File.separator + path);
    }

    public final File getFolder() {
        if (folder == null) {
            if (guild != null) {
                folder = Standard.createGuildFolder(getGuild());
            } else {
                folder = Standard.createGuildFolder(getGuildId());
            }
        }
        if (folder != null) {
            folder.mkdirs();
        }
        return folder;
    }

    public final Guild getGuild() {
        if (guild == null) {
            guild = Standard.getGuildById(guild_id);
        }
        return guild;
    }

    public final String getGuildId() {
        if (guild_id == null && guild != null) {
            guild_id = getGuild().getId();
        }
        return guild_id;
    }

    public final File getPermissionsFile() {
        return getFile(Standard.STANDARD_PERMISSIONS_FILE_NAME);
    }

    public final DefaultSettings getSettings() {
        if (settings == null) {
            getFolder();
            if (folder == null) {
                return (DefaultSettings) Standard.STANDARD_NULL_SETTINGS;
            }
            settings = (DefaultSettings) new DefaultSettings(getFile(Standard.STANDARD_GUILD_SETTINGS_FILE_NAME)).setAutoAddProperties(true);
        }
        return settings;
    }

    public final SimpleSettings getPluginSettings() {
        return getSettings().toSimpleSettings();
    }

    public final AdvancedGuild sayHi() {
        if (getGuild() == null) {
            System.out.println(String.format("No guild, no welcome message:  \"%s\"", guild));
            return this;
        }
        getSettings().loadSettings();
        String hi = getSettings().getProperty("welcome_message", null);
        if (hi != null) {
            guild.getPublicChannel().sendMessage(hi).queue();
        } else {
            System.out.println(String.format("No welcome message wanted: \"%s\"", guild));
        }
        return this;
    }

}
