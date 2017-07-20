package de.panzercraft.bot.supreme.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * Settings
 *
 * @author Panzer1119
 */
public class Settings {
    
    private final Properties settings = new Properties();
    private File file = null;
    
    public Settings() {
        this(null);
    }
    
    public Settings(File file) {
        setFile(file);
    }
    
    public final String getProperty(String key) {
        return getProperty(key, null);
    }
    
    public final String getProperty(String key, String defaultValue) {
        return settings.getProperty(key, defaultValue);
    }
    
    public final Object setProperty(String key, String value) {
        return setProperty(key, value, true);
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
            System.err.println(ex);
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
            System.err.println(ex);
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
        settings.stringPropertyNames().stream().filter((key) -> !key.equalsIgnoreCase("token")).forEach((key) -> {
            builder.addField(key, settings.getProperty(key, "" + null), false);
        });
        return builder;
    }
    
    protected static final String generateComment() {
        return "Changed on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

}
