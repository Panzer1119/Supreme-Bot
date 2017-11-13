package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.settings.AbstractConfig;
import de.codemakers.bot.supreme.settings.ConfigType;
import de.codemakers.bot.supreme.sql.annotations.SQLField;
import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import java.sql.JDBCType;
import java.util.List;

/**
 * ConfigData
 *
 * @author Panzer1119
 */
@SQLTable(name = "configs", types = {JDBCType.BIGINT, JDBCType.BIGINT, JDBCType.BIGINT, JDBCType.VARCHAR, JDBCType.VARCHAR})
public class ConfigData {

    @SQLField(index = 1, column = "guild_ID", nullBehavior = NullBehavior.NOT_NULL, defaultValue = "0", primaryKey = true, type = JDBCType.BIGINT)
    public final long guild_id;
    @SQLField(index = 2, column = "user_ID", nullBehavior = NullBehavior.NOT_NULL, defaultValue = "0", primaryKey = true, type = JDBCType.BIGINT)
    public final long user_id;
    @SQLField(index = 3, column = "config_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long config_id;
    @SQLField(index = 4, column = "config_key", length = "255", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.VARCHAR)
    public final String key;
    @SQLField(index = 5, column = "config_value", length = "7000", nullBehavior = NullBehavior.NULL, defaultValue = "NULL", type = JDBCType.VARCHAR)
    public String value;
    private AbstractConfig config = null;

    public ConfigData(long guild_id, long user_id, long config_id, String key, String value) {
        if (key == null) {
            throw new NullPointerException("The key must not be null!");
        }
        this.guild_id = guild_id;
        this.user_id = user_id;
        this.config_id = config_id;
        this.key = key;
        this.value = value;
    }

    public final ConfigData setValue(String value) {
        delete();
        this.value = value;
        SQLUtil.serializeObjects(ConfigData.class, MySQL.STANDARD_DATABASE, true, this);
        return this;
    }

    public final ConfigData delete() {
        SQLUtil.removeObjects(ConfigData.class, MySQL.STANDARD_DATABASE, this);
        return this;
    }

    public final AbstractConfig getConfig() {
        if (config == null) {
            config = AbstractConfig.getConfig(config_id);
        }
        return config;
    }

    public final ConfigData unregister() {
        config = null;
        return this;
    }

    public final ConfigType getConfigType() {
        if (guild_id == 0 && user_id == 0) {
            return ConfigType.BOT_CONFIG;
        } else if (guild_id != 0 && user_id == 0) {
            return ConfigType.GUILD_CONFIG;
        } else if (guild_id == 0 && user_id != 0) {
            return ConfigType.USER_CONFIG;
        } else if (guild_id != 0 && user_id != 0) {
            return ConfigType.GUILD_USER_CONFIG;
        }
        return null;
    }

    public static final List<ConfigData> getConfigDatas(long guild_id, long user_id, long config_id, String key) {
        final StringBuilder sql_extra = new StringBuilder(" WHERE");
        if (guild_id >= 0) {
            sql_extra.append(" guild_ID = ").append(SQLUtil.quote(guild_id));
            if (user_id >= 0 || config_id >= 0 || key != null) {
                sql_extra.append(" AND");
            }
        }
        if (user_id >= 0) {
            sql_extra.append(" user_ID = ").append(SQLUtil.quote(user_id));
            if (config_id >= 0 || key != null) {
                sql_extra.append(" AND");
            }
        }
        if (config_id >= 0) {
            sql_extra.append(" config_ID = ").append(SQLUtil.quote(config_id));
            if (key != null) {
                sql_extra.append(" AND");
            }
        }
        if (key != null) {
            sql_extra.append(" config_key = ").append(SQLUtil.quote(key));
        }
        if (sql_extra.length() == " WHERE".length()) {
            sql_extra.delete(0, sql_extra.length());
        }
        final String temp = String.format("SELECT * FROM configs%s;", sql_extra.toString());
        if (Database.DEBUG_SQL) {
            System.err.println("CONFIG SQL: " + temp);
        }
        final Result result = MySQL.STANDARD_DATABASE.executeQuery(temp);
        if (result == null) {
            return null;
        }
        final List<ConfigData> configDatas = SQLUtil.deserializeObjectsOfResultSet(ConfigData.class, result.resultSet);
        result.close();
        return configDatas;
    }

    public static final ConfigData getConfigData(long guild_id, long user_id, long config_id, String key, String... value) {
        final List<ConfigData> configDatas = getConfigDatas(guild_id, user_id, config_id, key);
        final ConfigData configData = configDatas == null ? null : configDatas.stream().findFirst().orElse(null);
        return configData == null ? ((value != null && value.length == 1) ? new ConfigData(guild_id, user_id, config_id, key, value[0]) : null) : configData;
    }

    public static final String getValue(long guild_id, long user_id, long config_id, String key) {
        if (key == null) {
            return null;
        }
        final ConfigData configData = getConfigData(guild_id, user_id, config_id, key);
        return configData == null ? null : configData.value;
    }

}
