package de.codemakers.bot.supreme.settings;

import de.codemakers.bot.supreme.plugin.Plugin;
import de.codemakers.bot.supreme.plugin.PluginProvider;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * SimpleSettings
 *
 * @author Panzer1119
 */
public class SimpleSettings extends Settings {
    
    private final PluginProvider provider;
    private final Plugin plugin;
    private final String guild_id;
    
    public SimpleSettings(PluginProvider provider, Plugin plugin, String guild_id) {
        this.provider = provider;
        this.plugin = plugin;
        this.guild_id = guild_id;
    }

    @Override
    public final String getProperty(String key, String defaultValue) {
        try {
            loadSettings();
            String value = settings.getProperty(key);
            if (value == null) {
                return defaultValue;
            } else {
                return value;
            }
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public final byte getProperty(String key, byte defaultValue) {
        try {
            return Byte.parseByte(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public final short getProperty(String key, short defaultValue) {
        try {
            return Short.parseShort(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public final int getProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public final long getProperty(String key, long defaultValue) {
        try {
            return Long.parseLong(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public final float getProperty(String key, float defaultValue) {
        try {
            return Float.parseFloat(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public final double getProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public final char getProperty(String key, char defaultValue) {
        try {
            return getProperty(key, "" + defaultValue).charAt(0);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public final boolean getProperty(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public final boolean removeProperty(String key) {
        try {
            settings.remove(key);
            saveSettings();
            return getProperty(key, null) == null;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public final Object setProperty(String key, String value) {
        return setProperty(key, value, true);
    }

    @Override
    public final Object setProperty(String key, byte value) {
        return setProperty(key, "" + value, true);
    }

    @Override
    public final Object setProperty(String key, short value) {
        return setProperty(key, "" + value, true);
    }

    @Override
    public final Object setProperty(String key, int value) {
        return setProperty(key, "" + value, true);
    }

    @Override
    public final Object setProperty(String key, long value) {
        return setProperty(key, "" + value, true);
    }

    @Override
    public final Object setProperty(String key, float value) {
        return setProperty(key, "" + value, true);
    }

    @Override
    public final Object setProperty(String key, double value) {
        return setProperty(key, "" + value, true);
    }

    @Override
    public final Object setProperty(String key, char value) {
        return setProperty(key, "" + value, true);
    }

    @Override
    public final Object setProperty(String key, boolean value) {
        return setProperty(key, "" + value, true);
    }

    @Override
    public final Object setProperty(String key, String value, boolean save) {
        final Object old = settings.setProperty(key, value);
        if (save) {
            saveSettings();
        }
        return old;
    }

    @Override
    public final boolean loadSettings(String jar_path) {
        return false;
    }

    @Override
    public final boolean loadSettings() {
        setSettings(provider.getSettings(plugin, guild_id).getSettings());
        return true;
    }

    @Override
    public final boolean loadSettings(File file) {
        return false;
    }

    @Override
    public final boolean loadSettings(InputStream inputStream) {
        return false;
    }

    @Override
    public final boolean saveSettings() {
        return provider.setSettings(plugin, guild_id, this);
    }

    @Override
    public final boolean saveSettings(File file) {
        return false;
    }

    @Override
    public final boolean saveSettings(OutputStream outputStream) {
        return false;
    }

    @Override
    public final Settings direct() {
        return this;
    }

    @Override
    public final ArrayList<Map.Entry<String, String>> toArrayList() {
        return null;
    }

    @Override
    public final EmbedBuilder toEmbed(EmbedBuilder builder) {
        return builder;
    }

    @Override
    protected String generateComment() {
        return null;
    }

}
