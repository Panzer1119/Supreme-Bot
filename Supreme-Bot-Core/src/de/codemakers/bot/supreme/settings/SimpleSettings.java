package de.codemakers.bot.supreme.settings;

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

    private final DefaultSettings settings;

    public SimpleSettings(DefaultSettings settings) {
        this.settings = settings;
    }

    @Override
    public final String getProperty(String key, String defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    @Override
    public final byte getProperty(String key, byte defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    @Override
    public final short getProperty(String key, short defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    @Override
    public final int getProperty(String key, int defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    @Override
    public final long getProperty(String key, long defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    @Override
    public final float getProperty(String key, float defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    @Override
    public final double getProperty(String key, double defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    @Override
    public final char getProperty(String key, char defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    @Override
    public final boolean getProperty(String key, boolean defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    @Override
    public final boolean removeProperty(String key) {
        return settings.removeProperty(key);
    }

    @Override
    public final Object setProperty(String key, String value) {
        return settings.setProperty(key, value);
    }

    @Override
    public final Object setProperty(String key, byte value) {
        return settings.setProperty(key, value);
    }

    @Override
    public final Object setProperty(String key, short value) {
        return settings.setProperty(key, value);
    }

    @Override
    public final Object setProperty(String key, int value) {
        return settings.setProperty(key, value);
    }

    @Override
    public final Object setProperty(String key, long value) {
        return settings.setProperty(key, value);
    }

    @Override
    public final Object setProperty(String key, float value) {
        return settings.setProperty(key, value);
    }

    @Override
    public final Object setProperty(String key, double value) {
        return settings.setProperty(key, value);
    }

    @Override
    public final Object setProperty(String key, char value) {
        return settings.setProperty(key, value);
    }

    @Override
    public final Object setProperty(String key, boolean value) {
        return settings.setProperty(key, value);
    }

    @Override
    public final Object setProperty(String key, String value, boolean save) {
        return settings.setProperty(key, value, save);
    }

    @Override
    public final boolean loadSettings(String jar_path) {
        return false;
    }

    @Override
    public final boolean loadSettings() {
        return false;
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
        return false;
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
