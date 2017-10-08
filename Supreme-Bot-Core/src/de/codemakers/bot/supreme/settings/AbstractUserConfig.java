package de.codemakers.bot.supreme.settings;

import de.codemakers.bot.supreme.sql.UserConfigData;
import java.util.ArrayList;
import java.util.List;

/**
 * AbstractUserConfig
 *
 * @author Panzer1119
 */
public abstract class AbstractUserConfig {

    private static final ArrayList<AbstractUserConfig> USER_CONFIGS = new ArrayList<>();

    private final long config_id;

    public AbstractUserConfig(long config_id) {
        this.config_id = config_id;
        register();
    }

    public final long getConfigId() {
        return config_id;
    }

    public final List<UserConfigData> getUserConfigDatasByUser(long user_id) {
        return UserConfigData.getUserConfigDatasByUser(user_id);
    }

    public final List<UserConfigData> getUserConfigDatasByKey(String key) {
        return UserConfigData.getUserConfigDatasByConfigIdAndKey(config_id, key);
    }

    public final UserConfigData getUserConfigDataByUserAndKey(long user_id, String key) {
        return UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
    }

    public final String getValue(long user_id, String key) {
        return UserConfigData.getValue(user_id, config_id, key);
    }

    public final String getValue(long user_id, String key, String defaultValue) {
        final String value = UserConfigData.getValue(user_id, config_id, key);
        return value == null ? defaultValue : value;
    }

    public final boolean getValue(long user_id, String key, boolean defaultValue) {
        final String value = UserConfigData.getValue(user_id, config_id, key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public final char getValue(long user_id, String key, char defaultValue) {
        final String value = UserConfigData.getValue(user_id, config_id, key);
        return value == null ? defaultValue : value.charAt(0);
    }

    public final byte getValue(long user_id, String key, byte defaultValue) {
        final String value = UserConfigData.getValue(user_id, config_id, key);
        return value == null ? defaultValue : Byte.parseByte(value);
    }

    public final short getValue(long user_id, String key, short defaultValue) {
        final String value = UserConfigData.getValue(user_id, config_id, key);
        return value == null ? defaultValue : Short.parseShort(value);
    }

    public final int getValue(long user_id, String key, int defaultValue) {
        final String value = UserConfigData.getValue(user_id, config_id, key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public final long getValue(long user_id, String key, long defaultValue) {
        final String value = UserConfigData.getValue(user_id, config_id, key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    public final float getValue(long user_id, String key, float defaultValue) {
        final String value = UserConfigData.getValue(user_id, config_id, key);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    public final double getValue(long user_id, String key, double defaultValue) {
        final String value = UserConfigData.getValue(user_id, config_id, key);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    public final UserConfigData setValue(long user_id, String key, String value) {
        final UserConfigData userConfigData = UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.setValue(value);
    }

    public final UserConfigData setValue(long user_id, String key, boolean value) {
        final UserConfigData userConfigData = UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.setValue("" + value);
    }

    public final UserConfigData setValue(long user_id, String key, char value) {
        final UserConfigData userConfigData = UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.setValue("" + value);
    }

    public final UserConfigData setValue(long user_id, String key, byte value) {
        final UserConfigData userConfigData = UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.setValue("" + value);
    }

    public final UserConfigData setValue(long user_id, String key, short value) {
        final UserConfigData userConfigData = UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.setValue("" + value);
    }

    public final UserConfigData setValue(long user_id, String key, int value) {
        final UserConfigData userConfigData = UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.setValue("" + value);
    }

    public final UserConfigData setValue(long user_id, String key, long value) {
        final UserConfigData userConfigData = UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.setValue("" + value);
    }

    public final UserConfigData setValue(long user_id, String key, float value) {
        final UserConfigData userConfigData = UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.setValue("" + value);
    }

    public final UserConfigData setValue(long user_id, String key, double value) {
        final UserConfigData userConfigData = UserConfigData.getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.setValue("" + value);
    }

    public final AbstractUserConfig register() {
        if (getUserConfig(config_id) != null) {
            throw new IllegalAccessError("The config " + config_id + " already exists!");
        }
        USER_CONFIGS.add(this);
        return this;
    }

    public final AbstractUserConfig unregister() {
        USER_CONFIGS.remove(this);
        return this;
    }

    public static final AbstractUserConfig getUserConfig(long config_id) {
        return USER_CONFIGS.stream().filter((user_config) -> user_config.config_id == config_id).findFirst().orElse(null);
    }

}
