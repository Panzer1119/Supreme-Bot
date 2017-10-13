package de.codemakers.bot.supreme.settings;

import de.codemakers.bot.supreme.sql.ConfigData;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * AbstractConfig
 *
 * @author Panzer1119
 */
public abstract class AbstractConfig {

    private static final ArrayList<AbstractConfig> CONFIGS = new ArrayList<>();

    private final long config_id;

    public AbstractConfig(long config_id) {
        this.config_id = config_id;
        register();
    }

    public final long getConfigId() {
        return config_id;
    }

    public final List<ConfigData> getConfigDatas(long guild_id, long user_id, String key) {
        return ConfigData.getConfigDatas(guild_id, user_id, config_id, key);
    }

    public final ConfigData getConfigData(long guild_id, long user_id, String key) {
        return ConfigData.getConfigData(guild_id, user_id, config_id, key);
    }

    public final String getValue(long guild_id, long user_id, String key) {
        return ConfigData.getValue(guild_id, user_id, config_id, key);
    }

    public final String getValue(long guild_id, long user_id, String key, Supplier<String> defaultValue) {
        final String value = getValue(guild_id, user_id, key);
        return (value == null && defaultValue != null) ? defaultValue.get() : value;
    }

    public final boolean getValue(long guild_id, long user_id, String key, boolean defaultValue) {
        final String value = getValue(guild_id, user_id, key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public final char getValue(long guild_id, long user_id, String key, char defaultValue) {
        final String value = getValue(guild_id, user_id, key);
        return value == null ? defaultValue : value.charAt(0);
    }

    public final byte getValue(long guild_id, long user_id, String key, byte defaultValue) {
        final String value = getValue(guild_id, user_id, key);
        return value == null ? defaultValue : Byte.parseByte(value);
    }

    public final short getValue(long guild_id, long user_id, String key, short defaultValue) {
        final String value = getValue(guild_id, user_id, key);
        return value == null ? defaultValue : Short.parseShort(value);
    }

    public final int getValue(long guild_id, long user_id, String key, int defaultValue) {
        final String value = getValue(guild_id, user_id, key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public final long getValue(long guild_id, long user_id, String key, long defaultValue) {
        final String value = getValue(guild_id, user_id, key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    public final float getValue(long guild_id, long user_id, String key, float defaultValue) {
        final String value = getValue(guild_id, user_id, key);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    public final double getValue(long guild_id, long user_id, String key, double defaultValue) {
        final String value = getValue(guild_id, user_id, key);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    public final ConfigData setValue(long guild_id, long user_id, String key, String value) {
        final ConfigData configData = ConfigData.getConfigData(guild_id, user_id, config_id, key, value);
        return configData == null ? null : configData.setValue(value);
    }

    public final ConfigData setValue(long guild_id, long user_id, String key, boolean value) {
        final ConfigData globalConfigData = ConfigData.getConfigData(guild_id, user_id, config_id, key, "" + value);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final ConfigData setValue(long guild_id, long user_id, String key, char value) {
        final ConfigData globalConfigData = ConfigData.getConfigData(guild_id, user_id, config_id, key, "" + value);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final ConfigData setValue(long guild_id, long user_id, String key, byte value) {
        final ConfigData globalConfigData = ConfigData.getConfigData(guild_id, user_id, config_id, key, "" + value);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final ConfigData setValue(long guild_id, long user_id, String key, short value) {
        final ConfigData globalConfigData = ConfigData.getConfigData(guild_id, user_id, config_id, key, "" + value);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final ConfigData setValue(long guild_id, long user_id, String key, int value) {
        final ConfigData globalConfigData = ConfigData.getConfigData(guild_id, user_id, config_id, key, "" + value);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final ConfigData setValue(long guild_id, long user_id, String key, long value) {
        final ConfigData globalConfigData = ConfigData.getConfigData(guild_id, user_id, config_id, key, "" + value);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final ConfigData setValue(long guild_id, long user_id, String key, float value) {
        final ConfigData globalConfigData = ConfigData.getConfigData(guild_id, user_id, config_id, key, "" + value);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final ConfigData setValue(long guild_id, long user_id, String key, double value) {
        final ConfigData globalConfigData = ConfigData.getConfigData(guild_id, user_id, config_id, key, "" + value);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final AbstractConfig register() {
        if (getConfig(config_id) != null) {
            throw new IllegalAccessError("The config \"" + config_id + "\" already exists!");
        }
        CONFIGS.add(this);
        return this;
    }

    public final AbstractConfig unregister() {
        CONFIGS.remove(this);
        return this;
    }

    public final EmbedBuilder toEmbedBuilder(long guild_id, long user_id) {
        final ConfigType configType = ConfigType.of(guild_id, user_id);
        final EmbedBuilder builder = Standard.getMessageEmbed(Color.YELLOW, null);
        getConfigDatas(guild_id, user_id, null).stream().filter((configData) -> (configType != ConfigType.BOT_CONFIG || !Util.contains(Standard.ULTRA_FORBIDDEN, configData.key))).forEach((configData) -> {
            builder.addField(configData.key, configData.value, false);
        });
        return builder;
    }

    public static final AbstractConfig getConfig(long config_id) {
        return CONFIGS.stream().filter((config) -> config.config_id == config_id).findFirst().orElse(null);
    }

}
