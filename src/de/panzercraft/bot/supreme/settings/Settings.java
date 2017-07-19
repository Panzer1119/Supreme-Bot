package de.panzercraft.bot.supreme.settings;

import de.panzercraft.bot.supreme.util.Standard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

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
    
    public static final boolean loadStandardSettings() {
        return Standard.STANDARD_SETTINGS.loadSettings();
    }
    
    public static final boolean saveStandardSettings() {
        return Standard.STANDARD_SETTINGS.saveSettings();
    }
    
    protected static final String generateComment() {
        return "Changed on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

}
