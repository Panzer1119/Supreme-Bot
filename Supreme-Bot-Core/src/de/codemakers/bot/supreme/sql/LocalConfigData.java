package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.settings.AbstractLocalConfig;
import de.codemakers.bot.supreme.sql.annotations.SQLField;
import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import de.codemakers.bot.supreme.sql.annotations.SQLVariable;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LocalConfigData
 *
 * @author Panzer1119
 */
@SQLTable(name = "local_configs", types = {JDBCType.BIGINT, JDBCType.BIGINT, JDBCType.VARCHAR, JDBCType.VARCHAR, JDBCType.TINYINT})
public class LocalConfigData {

    @SQLField(index = 1, column = "ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long id;
    @SQLField(index = 2, column = "config_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long config_id;
    @SQLField(index = 3, column = "config_key", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.VARCHAR)
    public final String key;
    @SQLField(index = 4, column = "config_value", nullBehavior = NullBehavior.NULL, defaultValue = "NULL", type = JDBCType.VARCHAR)
    public String value;
    @SQLField(index = 5, column = "isUserConfig", nullBehavior = NullBehavior.NOT_NULL, defaultValue = "1", primaryKey = true, type = JDBCType.TINYINT)
    public final boolean isUserConfig;
    private AbstractLocalConfig localConfig = null;

    public LocalConfigData(long id, long config_id, String key, String value, boolean isUserConfig) {
        if (key == null) {
            throw new NullPointerException("The key must not be null!");
        }
        this.id = id;
        this.config_id = config_id;
        this.key = key;
        this.value = value;
        this.isUserConfig = isUserConfig;
    }

    public final LocalConfigData setValue(String value) {
        SQLUtil.removeObjects(LocalConfigData.class, MySQL.STANDARD_DATABASE, this);
        this.value = value;
        SQLUtil.serializeObjects(LocalConfigData.class, MySQL.STANDARD_DATABASE, true, this);
        return this;
    }

    public final AbstractLocalConfig getLocalConfig() {
        if (localConfig == null) {
            localConfig = AbstractLocalConfig.getLocalConfig(config_id);
        }
        return localConfig;
    }

    public final LocalConfigData unregister() {
        localConfig = null;
        return this;
    }

    public static final List<LocalConfigData> getLocalConfigDatasById(long id, boolean isUserConfig) {
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM local_configs WHERE ID = %d AND isUserConfig = %d;", id, isUserConfig ? 1 : 0);
        final List<LocalConfigData> localConfigDatas = SQLUtil.deserializeObjectsOfResultSet(LocalConfigData.class, result.resultSet);
        result.close();
        return localConfigDatas;
    }

    public static final List<LocalConfigData> getLocalConfigDatasByConfigId(long config_id) {
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM local_configs WHERE config_ID = %d;", config_id);
        final List<LocalConfigData> localConfigDatas = SQLUtil.deserializeObjectsOfResultSet(LocalConfigData.class, result.resultSet);
        result.close();
        return localConfigDatas;
    }

    public static final List<LocalConfigData> getLocalConfigDatasByConfigIdAndKey(long config_id, String key) {
        if (key == null) {
            return new ArrayList<>();
        }
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM local_configs WHERE config_ID = %d AND config_key = %s;", config_id, SQLUtil.quote(key));
        final List<LocalConfigData> localConfigDatas = SQLUtil.deserializeObjectsOfResultSet(LocalConfigData.class, result.resultSet);
        result.close();
        return localConfigDatas;
    }

    public static final List<LocalConfigData> getLocalConfigDatasByIdAndConfigId(long id, long config_id, boolean isUserConfig) {
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM local_configs WHERE ID = %d AND config_ID = %d AND isUserConfig = %d;", id, config_id, isUserConfig ? 1 : 0);
        final List<LocalConfigData> localConfigDatas = SQLUtil.deserializeObjectsOfResultSet(LocalConfigData.class, result.resultSet);
        result.close();
        return localConfigDatas;
    }

    public static final LocalConfigData getLocalConfigDataByIdAndConfigIdAndKey(long id, long config_id, String key, boolean isUserConfig) {
        if (key == null) {
            return null;
        }
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM local_configs WHERE ID = %d AND config_ID = %d AND config_key = %s AND isUserConfig = %d;", id, config_id, SQLUtil.quote(key), isUserConfig ? 1 : 0);
        final List<LocalConfigData> localConfigDatas = SQLUtil.deserializeObjectsOfResultSet(LocalConfigData.class, result.resultSet);
        result.close();
        return localConfigDatas.stream().findFirst().orElse(null);
    }

    public static final String getValue(long id, long config_id, String key, boolean isUserConfig) {
        if (key == null) {
            return null;
        }
        final LocalConfigData localConfigData = getLocalConfigDataByIdAndConfigIdAndKey(id, config_id, key, isUserConfig);
        return localConfigData == null ? null : localConfigData.value;
    }

    @SQLVariable(type = SQLVariableType.SERIALIZER)
    public static final SQLSerializer SERIALIZER = new SQLSerializer() {
        @Override
        public String serialize(Object object, Map.Entry<Field, SQLField> field, String defaultReturn) throws Exception {
            if (object == null) {
                return null;
            }
            if (object instanceof Instant) {
                return Timestamp.from((Instant) object).toString();
            } else if (object instanceof LocalDateTime) {
                return Timestamp.valueOf((LocalDateTime) object).toString();
            } else if (object instanceof Boolean) {
                return ((Boolean) object) ? "1" : "0";
            }
            return defaultReturn;
        }

        @Override
        public boolean acceptClass(Class<?> clazz) {
            return Instant.class.equals(clazz) || LocalDateTime.class.equals(clazz) || Boolean.class.equals(clazz);
        }

        @Override
        public boolean acceptField(Map.Entry<Field, SQLField> field) {
            return field.getValue().index() == 5;
        }
    };

    @SQLVariable(type = SQLVariableType.DESERIALIZER)
    public static final SQLDeserializer DESERIALIZER = new SQLDeserializer() {
        @Override
        public final Object deserialize(ResultSet resultSet, Map.Entry<Field, SQLField> field, Object defaultReturn) throws Exception {
            if (Instant.class.equals(field.getKey().getType())) {
                final Timestamp timestamp = resultSet.getTimestamp(field.getValue().index());
                return (timestamp == null ? null : timestamp.toInstant());
            } else if (LocalDateTime.class.equals(field.getKey().getType())) {
                final Timestamp timestamp = resultSet.getTimestamp(field.getValue().index());
                return (timestamp == null ? null : timestamp.toLocalDateTime());
            }
            return defaultReturn;
        }

        @Override
        public final boolean acceptClass(Class<?> clazz) {
            return Instant.class.equals(clazz) || LocalDateTime.class.equals(clazz);
        }

        @Override
        public boolean acceptField(Map.Entry<Field, SQLField> field) {
            return false;
        }
    };

}
