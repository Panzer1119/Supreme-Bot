package de.codemakers.bot.supreme.settings;

import de.codemakers.bot.supreme.util.Copyable;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * DefaultSettings
 *
 * @author Panzer1119
 */
public class DefaultSettings extends Settings implements Copyable {

    private File file = null;
    private boolean autoAddProperties = false;

    public DefaultSettings() {
        this(null);
    }

    public DefaultSettings(File file) {
        super();
        setFile(file);
    }

    public final String getProperty(String key, String defaultValue) {
        try {
            String value = settings.getProperty(key);
            if (autoAddProperties && value == null) {
                setProperty(key, defaultValue, true);
                return defaultValue;
            } else if (value == null) {
                return defaultValue;
            } else {
                return value;
            }
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public final byte getProperty(String key, byte defaultValue) {
        try {
            return Byte.parseByte(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public final short getProperty(String key, short defaultValue) {
        try {
            return Short.parseShort(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public final int getProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public final long getProperty(String key, long defaultValue) {
        try {
            return Long.parseLong(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public final float getProperty(String key, float defaultValue) {
        try {
            return Float.parseFloat(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public final double getProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public final char getProperty(String key, char defaultValue) {
        try {
            return getProperty(key, "" + defaultValue).charAt(0);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public final boolean getProperty(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(getProperty(key, "" + defaultValue));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public final boolean removeProperty(String key) {
        try {
            settings.remove(key);
            saveSettings();
            return getProperty(key, null) == null;
        } catch (Exception ex) {
            return false;
        }
    }

    public final Object setProperty(String key, String value) {
        return setProperty(key, value, true);
    }

    public final Object setProperty(String key, byte value) {
        return setProperty(key, "" + value, true);
    }

    public final Object setProperty(String key, short value) {
        return setProperty(key, "" + value, true);
    }

    public final Object setProperty(String key, int value) {
        return setProperty(key, "" + value, true);
    }

    public final Object setProperty(String key, long value) {
        return setProperty(key, "" + value, true);
    }

    public final Object setProperty(String key, float value) {
        return setProperty(key, "" + value, true);
    }

    public final Object setProperty(String key, double value) {
        return setProperty(key, "" + value, true);
    }

    public final Object setProperty(String key, char value) {
        return setProperty(key, "" + value, true);
    }

    public final Object setProperty(String key, boolean value) {
        return setProperty(key, "" + value, true);
    }

    public final Object setProperty(String key, String value, boolean save) {
        final Object old = settings.setProperty(key, value);
        if (save) {
            saveSettings();
        }
        return old;
    }

    public final boolean loadSettings(String jar_path) {
        if (jar_path == null) {
            return false;
        }
        try {
            return loadSettings(Settings.class.getResourceAsStream(jar_path));
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }

    public final boolean loadSettings() {
        return loadSettings(file);
    }

    public final boolean loadSettings(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            return loadSettings(new FileInputStream(file));
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }

    public final boolean loadSettings(InputStream inputStream) {
        if (inputStream == null) {
            return false;
        }
        try {
            settings.clear();
            settings.load(inputStream);
            inputStream.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public final boolean saveSettings() {
        return saveSettings(file);
    }

    public final boolean saveSettings(File file) {
        if (file == null) {
            return false;
        }
        if (file.getParentFile() == null) {
            file = file.getAbsoluteFile();
        }
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            return saveSettings(new FileOutputStream(file, false));
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }

    public final boolean saveSettings(OutputStream outputStream) {
        if (outputStream == null) {
            return false;
        }
        try {
            settings.store(outputStream, generateComment());
            outputStream.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public final File getFile() {
        return file;
    }

    public final Settings setFile(File file) {
        this.file = file;
        return this;
    }

    public final Properties getSettings() {
        return settings;
    }

    public final Settings direct() {
        return copy().setAutoAddProperties(false);
    }

    @Override
    public DefaultSettings copy() {
        final DefaultSettings copy = new DefaultSettings(file);
        copy.autoAddProperties = autoAddProperties;
        copy.settings.putAll(settings);
        return copy;
    }

    public final ArrayList<Map.Entry<String, String>> toArrayList() {
        final ArrayList<Map.Entry<String, String>> arrayList = new ArrayList<Map.Entry<String, String>>() {
            @Override
            public String toString() {
                String out = "";
                for (Map.Entry<String, String> property : this) {
                    out += String.format("\n%s=%s", property.getKey(), property.getValue());
                }
                return out;
            }
        };
        settings.stringPropertyNames().stream().forEach((key) -> {
            arrayList.add(new AbstractMap.SimpleEntry<>(key, settings.getProperty(key, "" + null)));
        });
        return arrayList;
    }

    public final EmbedBuilder toEmbed(EmbedBuilder builder) {
        settings.stringPropertyNames().stream().filter((key) -> !Util.contains(Standard.ULTRA_FORBIDDEN, key)).forEach((key) -> {
            builder.addField("" + key, "" + settings.getProperty(key, "" + null), false);
        });
        return builder;
    }

    public final boolean isAutoAddProperties() {
        return autoAddProperties;
    }

    public final Settings setAutoAddProperties(boolean autoAddProperties) {
        this.autoAddProperties = autoAddProperties;
        return this;
    }

    protected final String generateComment() {
        return "Changed on:";
    }

}
