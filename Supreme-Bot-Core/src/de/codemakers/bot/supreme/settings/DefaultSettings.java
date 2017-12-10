package de.codemakers.bot.supreme.settings;

import de.codemakers.bot.supreme.util.Copyable;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import de.codemakers.io.file.AdvancedFile;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * DefaultSettings
 *
 * @author Panzer1119
 */
public class DefaultSettings extends Settings implements Copyable {

    private final Properties settings;
    private AdvancedFile file = null;
    private boolean autoAddProperties = false;
    private boolean autoAutoAddProperties = false;

    public DefaultSettings() {
        this(null);
    }

    public DefaultSettings(AdvancedFile file) {
        super();
        settings = new Properties();
        setFile(file);
    }

    public final Properties getSettings() {
        return settings;
    }

    public final Settings setSettings(Properties settings) {
        this.settings.clear();
        this.settings.putAll(settings);
        return this;
    }

    @Override
    public final String getProperty(String key, String defaultValue) {
        try {
            final String value = settings.getProperty(key);
            if (value == null) {
                if (autoAddProperties) {
                    if (autoAutoAddProperties) {
                        autoAddProperties = false;
                    }
                    setProperty(key, defaultValue, true);
                }
                autoAutoAddProperties = false;
                return defaultValue;
            } else {
                autoAutoAddProperties = false;
                return value;
            }
        } catch (Exception ex) {
            autoAutoAddProperties = false;
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

    @Override
    public final boolean loadSettings() {
        return loadSettings(file);
    }

    @Override
    public final boolean loadSettings(AdvancedFile file) {
        if (file == null) {
            return false;
        }
        try {
            file.createAdvancedFile();
            return loadSettings(file.createInputStream());
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }

    @Override
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

    @Override
    public final boolean saveSettings() {
        return saveSettings(file);
    }

    @Override
    public final boolean saveSettings(AdvancedFile file) {
        if (file == null) {
            return false;
        }
        try {
            file.createAdvancedFile();
            return saveSettings(file.createOutputstream(false));
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }

    @Override
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

    public final AdvancedFile getFile() {
        return file;
    }

    public final Settings setFile(AdvancedFile file) {
        this.file = file;
        return this;
    }

    @Override
    public final Settings direct() {
        return copy().setAutoAddProperties(false);
    }

    @Override
    public final DefaultSettings copy() {
        final DefaultSettings copy = new DefaultSettings(file);
        copy.autoAddProperties = autoAddProperties;
        copy.settings.putAll(settings);
        return copy;
    }

    @Override
    public final ArrayList<Map.Entry<String, String>> toArrayList() {
        final ArrayList<Map.Entry<String, String>> arrayList = new ArrayList<Map.Entry<String, String>>() {
            @Override
            public String toString() {
                return stream().map((property) -> String.format("%s%s=%s", Standard.NEW_LINE, property.getKey(), property.getValue())).collect(Collectors.joining());
            }
        };
        settings.stringPropertyNames().stream().forEach((key) -> {
            arrayList.add(new AbstractMap.SimpleEntry<>(key, settings.getProperty(key, "" + null)));
        });
        return arrayList;
    }

    @Override
    public final EmbedBuilder toEmbed(EmbedBuilder builder) {
        settings.stringPropertyNames().stream().filter((key) -> !Util.contains(Standard.ULTRA_FORBIDDEN, key)).forEach((key) -> {
            builder.addField("" + key, "" + settings.getProperty(key, "" + null), false);
        });
        return builder;
    }

    public final boolean isAutoAddProperties() {
        return autoAddProperties;
    }

    public final DefaultSettings setAutoAddProperties(boolean autoAddProperties) {
        this.autoAddProperties = autoAddProperties;
        return this;
    }

    @Override
    protected final String generateComment() {
        return "Changed on:";
    }

    public final SimpleSettings toSimpleSettings() {
        return new SimpleSettings(this);
    }

    @Override
    public final DefaultSettings asAutoAdd() {
        autoAddProperties = true;
        autoAutoAddProperties = true;
        return this;
    }

}
