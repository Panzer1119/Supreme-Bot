package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.settings.AbstractUserConfig;
import de.codemakers.bot.supreme.sql.annotations.SQLField;
import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * UserConfigData
 *
 * @author Panzer1119
 */
@SQLTable(name = "user_configs", types = {JDBCType.BIGINT, JDBCType.BIGINT, JDBCType.VARCHAR, JDBCType.VARCHAR})
public class UserConfigData {

    @SQLField(index = 1, column = "user_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long user_id;
    @SQLField(index = 2, column = "config_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long config_id;
    @SQLField(index = 3, column = "config_key", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.VARCHAR)
    public final String key;
    @SQLField(index = 4, column = "config_value", nullBehavior = NullBehavior.NULL, defaultValue = "NULL", type = JDBCType.VARCHAR)
    public String value;
    private AbstractUserConfig userConfig = null;

    public UserConfigData(long user_id, long config_id, String key, String value) {
        if (key == null) {
            throw new NullPointerException("The key must not be null!");
        }
        this.user_id = user_id;
        this.config_id = config_id;
        this.key = key;
        this.value = value;
    }

    public final UserConfigData setValue(String value) {
        SQLUtil.removeObjects(UserConfigData.class, MySQL.STANDARD_DATABASE, this);
        this.value = value;
        SQLUtil.serializeObjects(UserConfigData.class, MySQL.STANDARD_DATABASE, true, this);
        return this;
    }

    public final AbstractUserConfig getUserConfig() {
        if (userConfig == null) {
            userConfig = AbstractUserConfig.getUserConfig(config_id);
        }
        return userConfig;
    }

    public final UserConfigData unregister() {
        userConfig = null;
        return this;
    }

    public static final List<UserConfigData> getUserConfigDatasByUser(long user_id) {
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM user_configs WHERE user_id = %d;", user_id);
        final List<UserConfigData> userConfigDatas = SQLUtil.deserializeObjectsOfResultSet(UserConfigData.class, result.resultSet);
        result.close();
        return userConfigDatas;
    }

    public static final List<UserConfigData> getUserConfigDatasByConfigId(long config_id) {
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM user_configs WHERE config_ID = %d;", config_id);
        final List<UserConfigData> userConfigDatas = SQLUtil.deserializeObjectsOfResultSet(UserConfigData.class, result.resultSet);
        result.close();
        return userConfigDatas;
    }

    public static final List<UserConfigData> getUserConfigDatasByConfigIdAndKey(long config_id, String key) {
        if (key == null) {
            return new ArrayList<>();
        }
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM user_configs WHERE config_id = %d AND config_key = %s;", config_id, SQLUtil.quote(key));
        final List<UserConfigData> userConfigDatas = SQLUtil.deserializeObjectsOfResultSet(UserConfigData.class, result.resultSet);
        result.close();
        return userConfigDatas;
    }

    public static final List<UserConfigData> getUserConfigDatasByUserAndConfigId(long user_id, long config_id) {
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM user_configs WHERE user_id = %d AND config_id = %d;", user_id, config_id);
        final List<UserConfigData> userConfigDatas = SQLUtil.deserializeObjectsOfResultSet(UserConfigData.class, result.resultSet);
        result.close();
        return userConfigDatas;
    }

    public static final UserConfigData getUserConfigDataByUserAndConfigIdAndKey(long user_id, long config_id, String key) {
        if (key == null) {
            return null;
        }
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM user_configs WHERE user_id = %d AND config_id = %d AND config_key = %s;", user_id, config_id, SQLUtil.quote(key));
        final List<UserConfigData> userConfigDatas = SQLUtil.deserializeObjectsOfResultSet(UserConfigData.class, result.resultSet);
        result.close();
        return userConfigDatas.stream().findFirst().orElse(null);
    }

    public static final String getValue(long user_id, long config_id, String key) {
        if (key == null) {
            return null;
        }
        final UserConfigData userConfigData = getUserConfigDataByUserAndConfigIdAndKey(user_id, config_id, key);
        return userConfigData == null ? null : userConfigData.value;
    }

}
