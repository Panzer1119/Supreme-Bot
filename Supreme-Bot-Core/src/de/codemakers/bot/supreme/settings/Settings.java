package de.codemakers.bot.supreme.settings;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * Settings
 *
 * @author Panzer1119
 */
public abstract class Settings {

    public abstract String getProperty(String key, String defaultValue);

    public abstract byte getProperty(String key, byte defaultValue);

    public abstract short getProperty(String key, short defaultValue);

    public abstract int getProperty(String key, int defaultValue);

    public abstract long getProperty(String key, long defaultValue);

    public abstract float getProperty(String key, float defaultValue);

    public abstract double getProperty(String key, double defaultValue);

    public abstract char getProperty(String key, char defaultValue);

    public abstract boolean getProperty(String key, boolean defaultValue);

    public abstract boolean removeProperty(String key);

    public abstract Object setProperty(String key, String value);

    public abstract Object setProperty(String key, byte value);

    public abstract Object setProperty(String key, short value);

    public abstract Object setProperty(String key, int value);

    public abstract Object setProperty(String key, long value);

    public abstract Object setProperty(String key, float value);

    public abstract Object setProperty(String key, double value);

    public abstract Object setProperty(String key, char value);

    public abstract Object setProperty(String key, boolean value);

    public abstract Object setProperty(String key, String value, boolean save);

    public abstract boolean loadSettings(String jar_path);

    public abstract boolean loadSettings();

    public abstract boolean loadSettings(File file);

    public abstract boolean loadSettings(InputStream inputStream);

    public abstract boolean saveSettings();

    public abstract boolean saveSettings(File file);

    public abstract boolean saveSettings(OutputStream outputStream);

    public abstract Settings direct();

    public abstract ArrayList<Map.Entry<String, String>> toArrayList();

    public abstract EmbedBuilder toEmbed(EmbedBuilder builder);

    protected abstract String generateComment();

}
