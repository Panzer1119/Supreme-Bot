package de.codemakers.bot.supreme.settings;

import de.codemakers.bot.supreme.sql.LocalConfigData;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * AbstractLocalConfig
 *
 * @author Panzer1119
 */
public abstract class AbstractLocalConfig {

    private static final ArrayList<AbstractLocalConfig> LOCAL_CONFIGS = new ArrayList<>();

    private final long config_id;

    public AbstractLocalConfig(long config_id) {
        this.config_id = config_id;
        register();
    }

    public final long getConfigId() {
        return config_id;
    }

    public final List<LocalConfigData> getLocalConfigDatasById(long id, boolean isUserConfig) {
        return LocalConfigData.getLocalConfigDatasById(id, isUserConfig);
    }

    public final List<LocalConfigData> getLocalConfigDatasByKey(String key) {
        return LocalConfigData.getLocalConfigDatasByConfigIdAndKey(config_id, key);
    }

    public final LocalConfigData getLocalConfigDataByIdAndKey(long id, String key, boolean isUserConfig) {
        return LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKey(id, config_id, key, isUserConfig);
    }

    public final String getValue(long id, String key, boolean isUserConfig) {
        return LocalConfigData.getValue(id, config_id, key, isUserConfig);
    }

    public final String getValue(long id, String key, Supplier<String> defaultValue, boolean isUserConfig) {
        final String value = LocalConfigData.getValue(id, config_id, key, isUserConfig);
        return (value == null && defaultValue != null) ? defaultValue.get() : value;
    }

    public final boolean getValue(long id, String key, boolean defaultValue, boolean isUserConfig) {
        final String value = LocalConfigData.getValue(id, config_id, key, isUserConfig);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public final char getValue(long id, String key, char defaultValue, boolean isUserConfig) {
        final String value = LocalConfigData.getValue(id, config_id, key, isUserConfig);
        return value == null ? defaultValue : value.charAt(0);
    }

    public final byte getValue(long id, String key, byte defaultValue, boolean isUserConfig) {
        final String value = LocalConfigData.getValue(id, config_id, key, isUserConfig);
        return value == null ? defaultValue : Byte.parseByte(value);
    }

    public final short getValue(long id, String key, short defaultValue, boolean isUserConfig) {
        final String value = LocalConfigData.getValue(id, config_id, key, isUserConfig);
        return value == null ? defaultValue : Short.parseShort(value);
    }

    public final int getValue(long id, String key, int defaultValue, boolean isUserConfig) {
        final String value = LocalConfigData.getValue(id, config_id, key, isUserConfig);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public final long getValue(long id, String key, long defaultValue, boolean isUserConfig) {
        final String value = LocalConfigData.getValue(id, config_id, key, isUserConfig);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    public final float getValue(long id, String key, float defaultValue, boolean isUserConfig) {
        final String value = LocalConfigData.getValue(id, config_id, key, isUserConfig);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    public final double getValue(long id, String key, double defaultValue, boolean isUserConfig) {
        final String value = LocalConfigData.getValue(id, config_id, key, isUserConfig);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    public final LocalConfigData setValue(long id, String key, String value, boolean isUserConfig) {
        final LocalConfigData localConfigData = LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKeyForAdding(id, config_id, key, value, isUserConfig);
        return localConfigData == null ? null : localConfigData.setValue(value);
    }

    public final LocalConfigData setValue(long id, String key, boolean value, boolean isUserConfig) {
        final LocalConfigData localConfigData = LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKeyForAdding(id, config_id, key, "" + value, isUserConfig);
        return localConfigData == null ? null : localConfigData.setValue("" + value);
    }

    public final LocalConfigData setValue(long id, String key, char value, boolean isUserConfig) {
        final LocalConfigData localConfigData = LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKeyForAdding(id, config_id, key, "" + value, isUserConfig);
        return localConfigData == null ? null : localConfigData.setValue("" + value);
    }

    public final LocalConfigData setValue(long id, String key, byte value, boolean isUserConfig) {
        final LocalConfigData localConfigData = LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKeyForAdding(id, config_id, key, "" + value, isUserConfig);
        return localConfigData == null ? null : localConfigData.setValue("" + value);
    }

    public final LocalConfigData setValue(long id, String key, short value, boolean isUserConfig) {
        final LocalConfigData localConfigData = LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKeyForAdding(id, config_id, key, "" + value, isUserConfig);
        return localConfigData == null ? null : localConfigData.setValue("" + value);
    }

    public final LocalConfigData setValue(long id, String key, int value, boolean isUserConfig) {
        final LocalConfigData localConfigData = LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKeyForAdding(id, config_id, key, "" + value, isUserConfig);
        return localConfigData == null ? null : localConfigData.setValue("" + value);
    }

    public final LocalConfigData setValue(long id, String key, long value, boolean isUserConfig) {
        final LocalConfigData localConfigData = LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKeyForAdding(id, config_id, key, "" + value, isUserConfig);
        return localConfigData == null ? null : localConfigData.setValue("" + value);
    }

    public final LocalConfigData setValue(long id, String key, float value, boolean isUserConfig) {
        final LocalConfigData localConfigData = LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKeyForAdding(id, config_id, key, "" + value, isUserConfig);
        return localConfigData == null ? null : localConfigData.setValue("" + value);
    }

    public final LocalConfigData setValue(long id, String key, double value, boolean isUserConfig) {
        final LocalConfigData localConfigData = LocalConfigData.getLocalConfigDataByIdAndConfigIdAndKeyForAdding(id, config_id, key, "" + value, isUserConfig);
        return localConfigData == null ? null : localConfigData.setValue("" + value);
    }

    public final AbstractLocalConfig register() {
        if (getLocalConfig(config_id) != null) {
            throw new IllegalAccessError("The local config " + config_id + " already exists!");
        }
        LOCAL_CONFIGS.add(this);
        return this;
    }

    public final AbstractLocalConfig unregister() {
        LOCAL_CONFIGS.remove(this);
        return this;
    }

    public final EmbedBuilder toEmbedBuilder(long id, boolean isUserConfig) {
        final EmbedBuilder builder = Standard.getMessageEmbed(Color.YELLOW, null);
        getLocalConfigDatasById(id, isUserConfig).stream().filter((localConfigData) -> !Util.contains(Standard.ULTRA_FORBIDDEN_LOCAL, localConfigData.key)).forEach((localConfigData) -> {
            builder.addField(localConfigData.key, localConfigData.value, false);
        });
        return builder;
    }

    public static final AbstractLocalConfig getLocalConfig(long config_id) {
        return LOCAL_CONFIGS.stream().filter((user_config) -> user_config.config_id == config_id).findFirst().orElse(null);
    }

}
