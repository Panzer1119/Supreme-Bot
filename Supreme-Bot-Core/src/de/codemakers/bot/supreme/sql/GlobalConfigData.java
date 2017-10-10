package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.settings.AbstractGlobalConfig;
import de.codemakers.bot.supreme.sql.annotations.SQLField;
import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import java.sql.JDBCType;
import java.util.List;

/**
 * GlobalConfigData
 *
 * @author Panzer1119
 */
@SQLTable(name = "global_configs", types = {JDBCType.BIGINT, JDBCType.VARCHAR, JDBCType.VARCHAR})
public class GlobalConfigData {

    @SQLField(index = 1, column = "config_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long config_id;
    @SQLField(index = 2, column = "config_key", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.VARCHAR)
    public final String key;
    @SQLField(index = 3, column = "config_value", nullBehavior = NullBehavior.NULL, defaultValue = "NULL", type = JDBCType.VARCHAR)
    public String value;
    private AbstractGlobalConfig globalConfig = null;

    public GlobalConfigData(long config_id, String key, String value) {
        if (key == null) {
            throw new NullPointerException("The key must not be null!");
        }
        this.config_id = config_id;
        this.key = key;
        this.value = value;
    }

    public final GlobalConfigData setValue(String value) {
        delete();
        this.value = value;
        SQLUtil.serializeObjects(GlobalConfigData.class, MySQL.STANDARD_DATABASE, true, this);
        return this;
    }

    public final GlobalConfigData delete() {
        SQLUtil.removeObjects(GlobalConfigData.class, MySQL.STANDARD_DATABASE, this);
        return this;
    }

    public final AbstractGlobalConfig getGlobalConfig() {
        if (globalConfig == null) {
            globalConfig = AbstractGlobalConfig.getGlobalConfig(config_id);
        }
        return globalConfig;
    }

    public final GlobalConfigData unregister() {
        globalConfig = null;
        return this;
    }

    public static final List<GlobalConfigData> getGlobalConfigDatas() {
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM global_configs;");
        if (result == null) {
            return null;
        }
        final List<GlobalConfigData> globalConfigDatas = SQLUtil.deserializeObjectsOfResultSet(GlobalConfigData.class, result.resultSet);
        result.close();
        return globalConfigDatas;
    }

    public static final List<GlobalConfigData> getGlobalConfigDatasByConfigId(long config_id) {
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM global_configs WHERE config_ID = %d;", config_id);
        if (result == null) {
            return null;
        }
        final List<GlobalConfigData> globalConfigDatas = SQLUtil.deserializeObjectsOfResultSet(GlobalConfigData.class, result.resultSet);
        result.close();
        return globalConfigDatas;
    }

    public static final GlobalConfigData getGlobalConfigDatasByConfigIdAndKey(long config_id, String key) {
        if (key == null) {
            return null;
        }
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM global_configs WHERE config_ID = %d AND config_key = %s;", config_id, SQLUtil.quote(key));
        if (result == null) {
            return null;
        }
        final List<GlobalConfigData> globalConfigDatas = SQLUtil.deserializeObjectsOfResultSet(GlobalConfigData.class, result.resultSet);
        result.close();
        if (globalConfigDatas == null) {
            return null;
        }
        return globalConfigDatas.stream().findFirst().orElse(null);
    }

    public static final GlobalConfigData getGlobalConfigDatasByConfigIdAndKeyForAdding(long config_id, String key, String value) {
        if (key == null) {
            return null;
        }
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM global_configs WHERE config_ID = %d AND config_key = %s;", config_id, SQLUtil.quote(key));
        if (result == null) {
            return null;
        }
        final List<GlobalConfigData> globalConfigDatas = SQLUtil.deserializeObjectsOfResultSet(GlobalConfigData.class, result.resultSet);
        result.close();
        if (globalConfigDatas == null) {
            return new GlobalConfigData(config_id, key, value);
        }
        final GlobalConfigData globalConfigData = globalConfigDatas.stream().findFirst().orElse(null);
        return globalConfigData == null ? new GlobalConfigData(config_id, key, value) : globalConfigData;
    }

    public static final String getValue(long config_id, String key) {
        if (key == null) {
            return null;
        }
        final GlobalConfigData globalConfigData = getGlobalConfigDatasByConfigIdAndKey(config_id, key);
        return globalConfigData == null ? null : globalConfigData.value;
    }

}
