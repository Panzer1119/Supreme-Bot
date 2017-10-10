package de.codemakers.bot.supreme.settings;

import de.codemakers.bot.supreme.sql.GlobalConfigData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * AbstractGlobalConfig
 *
 * @author Panzer1119
 */
public abstract class AbstractGlobalConfig {

    private static final ArrayList<AbstractGlobalConfig> GLOBAL_CONFIGS = new ArrayList<>();

    private final long config_id;

    public AbstractGlobalConfig(long config_id) {
        this.config_id = config_id;
        register();
    }

    public final long getConfigId() {
        return config_id;
    }

    public final List<GlobalConfigData> getGlobalConfigDatas() {
        return GlobalConfigData.getGlobalConfigDatas();
    }

    public final GlobalConfigData getGlobalConfigDataByKey(String key) {
        return GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
    }

    public final String getValue(String key) {
        return GlobalConfigData.getValue(config_id, key);
    }

    public final String getValue(String key, Supplier<String> defaultValue) {
        final String value = GlobalConfigData.getValue(config_id, key);
        return (value == null && defaultValue != null) ? defaultValue.get() : value;
    }

    public final boolean getValue(String key, boolean defaultValue) {
        final String value = GlobalConfigData.getValue(config_id, key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public final char getValue(String key, char defaultValue) {
        final String value = GlobalConfigData.getValue(config_id, key);
        return value == null ? defaultValue : value.charAt(0);
    }

    public final byte getValue(String key, byte defaultValue) {
        final String value = GlobalConfigData.getValue(config_id, key);
        return value == null ? defaultValue : Byte.parseByte(value);
    }

    public final short getValue(String key, short defaultValue) {
        final String value = GlobalConfigData.getValue(config_id, key);
        return value == null ? defaultValue : Short.parseShort(value);
    }

    public final int getValue(String key, int defaultValue) {
        final String value = GlobalConfigData.getValue(config_id, key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public final long getValue(String key, long defaultValue) {
        final String value = GlobalConfigData.getValue(config_id, key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    public final float getValue(String key, float defaultValue) {
        final String value = GlobalConfigData.getValue(config_id, key);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    public final double getValue(String key, double defaultValue) {
        final String value = GlobalConfigData.getValue(config_id, key);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    public final GlobalConfigData setValue(String key, String value) {
        final GlobalConfigData globalConfigData = GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.setValue(value);
    }

    public final GlobalConfigData setValue(String key, boolean value) {
        final GlobalConfigData globalConfigData = GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final GlobalConfigData setValue(String key, char value) {
        final GlobalConfigData globalConfigData = GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final GlobalConfigData setValue(String key, byte value) {
        final GlobalConfigData globalConfigData = GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final GlobalConfigData setValue(String key, short value) {
        final GlobalConfigData globalConfigData = GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final GlobalConfigData setValue(String key, int value) {
        final GlobalConfigData globalConfigData = GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final GlobalConfigData setValue(String key, long value) {
        final GlobalConfigData globalConfigData = GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final GlobalConfigData setValue(String key, float value) {
        final GlobalConfigData globalConfigData = GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final GlobalConfigData setValue(String key, double value) {
        final GlobalConfigData globalConfigData = GlobalConfigData.getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.setValue("" + value);
    }

    public final AbstractGlobalConfig register() {
        if (getGlobalConfig(config_id) != null) {
            throw new IllegalAccessError("The global config " + config_id + " already exists!");
        }
        GLOBAL_CONFIGS.add(this);
        return this;
    }

    public final AbstractGlobalConfig unregister() {
        GLOBAL_CONFIGS.remove(this);
        return this;
    }

    public static final AbstractGlobalConfig getGlobalConfig(long config_id) {
        return GLOBAL_CONFIGS.stream().filter((user_config) -> user_config.config_id == config_id).findFirst().orElse(null);
    }

}
