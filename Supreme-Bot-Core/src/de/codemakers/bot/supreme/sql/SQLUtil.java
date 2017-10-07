package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.sql.annotations.SQLField;
import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import de.codemakers.bot.supreme.sql.annotations.SQLVariable;
import de.codemakers.bot.supreme.util.Util;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * SQLUtil
 *
 * @author Paul
 */
public class SQLUtil {

    public static final <T> ArrayList<T> deserializeObjectsOfResultSet(Class<? extends T> clazz, ResultSet resultSet) {
        return deserializeObjectsOfResultSet(clazz, resultSet, getSQLDeserializer(clazz));
    }

    public static final <T> ArrayList<T> deserializeObjectsOfResultSet(Class<? extends T> clazz, ResultSet resultSet, SQLDeserializer deserializer) {
        return deserializeObjectsOfResultSet(clazz, resultSet, false, deserializer);
    }

    public static final <T> ArrayList<T> deserializeObjectsOfResultSet(Class<? extends T> clazz, ResultSet resultSet, boolean forceAll) {
        return deserializeObjectsOfResultSet(clazz, resultSet, forceAll, getSQLDeserializer(clazz));
    }

    public static final <T> ArrayList<T> deserializeObjectsOfResultSet(Class<? extends T> clazz, ResultSet resultSet, boolean forceAll, SQLDeserializer deserializer) {
        try {
            if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class) || resultSet == null || resultSet.isClosed() || resultSet.isAfterLast() || (!resultSet.isBeforeFirst() && !resultSet.next())) {
                return null;
            }
            final List<Map.Entry<Field, SQLField>> fields = getFields(clazz, FieldType.of(false, false, forceAll));
            final Constructor constructor = clazz.getConstructor(fields.stream().map((field) -> field.getKey().getType()).collect(Collectors.toList()).toArray(new Class<?>[0]));
            final ArrayList<T> unserializedObjects = new ArrayList<>();
            resultSet.first();
            do {
                try {
                    final Object[] args = fields.stream().map((field) -> {
                        try {
                            Object defaultReturn = null;
                            try {
                                defaultReturn = resultSet.getObject(field.getValue().index(), field.getKey().getType());
                            } catch (Exception ex) {
                            }
                            if (deserializer == null || !(deserializer.acceptClass(field.getKey().getType()) || deserializer.acceptField(field))) {
                                return defaultReturn;
                            } else {
                                return deserializer.deserialize(resultSet, field, defaultReturn);
                            }
                        } catch (Exception ex) {
                            System.err.println(ex);
                            return null;
                        }
                    }).toArray();
                    final Object instance = constructor.newInstance(args);
                    if (instance == null || !instance.getClass().equals(clazz)) {
                        continue;
                    }
                    unserializedObjects.add((T) instance);
                } catch (Exception ex) {
                    System.err.println("SQLUtil: unserializeObject error");
                    ex.printStackTrace();
                }
            } while (resultSet.next());
            resultSet.close();
            return unserializedObjects;
        } catch (Exception ex) {
            System.err.println("SQLUtil: unserializeObjects error");
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz) {
        return deserializeObjects(clazz, getSQLDeserializer(clazz));
    }

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz, SQLDeserializer deserializer) {
        return deserializeObjects(clazz, false, deserializer);
    }

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz, boolean forceAll) {
        return deserializeObjects(clazz, forceAll, getSQLDeserializer(clazz));
    }

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz, boolean forceAll, SQLDeserializer deserializer) {
        if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class)) {
            return null;
        }
        try {
            final SQLTable table = clazz.getAnnotation(SQLTable.class);
            return deserializeObjects(clazz, forceAll, deserializer, table.name());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz, String table) {
        return deserializeObjects(clazz, getSQLDeserializer(clazz), table);
    }

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz, SQLDeserializer deserializer, String table) {
        return deserializeObjects(clazz, false, deserializer, table);
    }

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz, boolean forceAll, String table) {
        return deserializeObjects(clazz, forceAll, getSQLDeserializer(clazz), table);
    }

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz, boolean forceAll, SQLDeserializer deserializer, String table) {
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM %s;", table);
        if (result == null) {
            return null;
        }
        final ArrayList<T> objects = deserializeObjectsOfResultSet(clazz, result.resultSet, forceAll, deserializer);
        try {
            result.statement.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return objects;
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, SQLSerializer serializer, T... objects) {
        return serializeObjects(clazz, database, false, serializer, objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, T... objects) {
        return serializeObjects(clazz, database, forceAll, getSQLSerializer(clazz), objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, SQLSerializer serializer, T... objects) {
        if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class)) {
            return false;
        }
        try {
            final SQLTable table = clazz.getAnnotation(SQLTable.class);
            return serializeObjects(clazz, database, forceAll, serializer, table.name(), objects);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, ArrayList<T> objects) {
        return serializeObjects(clazz, database, getSQLSerializer(clazz), objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, SQLSerializer serializer, ArrayList<T> objects) {
        if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class)) {
            return false;
        }
        try {
            final SQLTable table = clazz.getAnnotation(SQLTable.class);
            return serializeObjects(clazz, database, serializer, table.name(), objects);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, ArrayList<T> objects) {
        return serializeObjects(clazz, database, forceAll, getSQLSerializer(clazz), objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, SQLSerializer serializer, ArrayList<T> objects) {
        if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class)) {
            return false;
        }
        try {
            final SQLTable table = clazz.getAnnotation(SQLTable.class);
            return serializeObjects(clazz, database, forceAll, serializer, table.name(), objects);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, String table, T... objects) {
        return serializeObjects(clazz, database, getSQLSerializer(clazz), table, objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, SQLSerializer serializer, String table, T... objects) {
        return serializeObjects(clazz, database, false, serializer, table, objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, String table, T... objects) {
        return serializeObjects(clazz, database, forceAll, getSQLSerializer(clazz), table, objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, SQLSerializer serializer, String table, T... objects) {
        return serializeObjects(clazz, database, forceAll, serializer, table, new ArrayList<>(Arrays.asList(objects)));
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, String table, ArrayList<T> objects) {
        return serializeObjects(clazz, database, getSQLSerializer(clazz), table, objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, SQLSerializer serializer, String table, ArrayList<T> objects) {
        return serializeObjects(clazz, database, false, serializer, table, objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, String table, ArrayList<T> objects) {
        return serializeObjects(clazz, database, forceAll, getSQLSerializer(clazz), table, objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, SQLSerializer serializer, String table, ArrayList<T> objects) {
        if (clazz == null || database == null || !database.isConnected() || objects == null || objects.isEmpty()) {
            return false;
        }
        try {
            final List<Map.Entry<Field, SQLField>> fields_all = getFields(clazz, FieldType.ALL);
            final List<Map.Entry<Field, SQLField>> fields_needed = getFields(clazz, FieldType.of(true, false, forceAll));
            final String sql_format = String.format("INSERT INTO %s (%s) VALUES (%%s);", table, fields_needed.stream().map((field) -> field.getValue().column()).collect(Collectors.joining(", ")));
            final ArrayList<String> sqls = new ArrayList<>();
            final String sql_create = String.format("CREATE TABLE IF NOT EXISTS %s (%s, PRIMARY KEY (%s));", table, fields_all.stream().map((field) -> field.getValue()).map((field) -> (String.format("%s %s%s%s%s", field.column(), fromJDBCType(field.type()), field.nullBehavior() == NullBehavior.NOT_NULL ? " NOT NULL" : (field.nullBehavior() == NullBehavior.NULL ? " NULL" : ""), (field.defaultValue().isEmpty() ? "" : " DEFAULT " + field.defaultValue()), field.extra()))).collect(Collectors.joining(", ")), fields_all.stream().map((field) -> field.getValue()).filter((field) -> field.primaryKey()).map((field) -> field.column()).collect(Collectors.joining(", "))); //, field.primaryKey() ? ", PRIMARY KEY(" + field.column() + ")" : ""
            if (Database.DEBUG_SQL) {
                System.err.println("SQL TABLE CREATION: " + sql_create);
            }
            sqls.add(sql_create);
            objects.stream().forEach((object) -> {
                try {
                    final String sql = String.format(sql_format, fields_needed.stream().map((field) -> {
                        try {
                            String defaultReturn = null;
                            try {
                                defaultReturn = quote(field.getKey().get(object));
                            } catch (Exception ex) {
                            }
                            if (serializer == null || !(serializer.acceptClass(field.getKey().getType()) || serializer.acceptField(field))) {
                                return defaultReturn;
                            } else {
                                return quote(serializer.serialize(field.getKey().get(object), field, defaultReturn));
                            }
                        } catch (Exception ex) {
                            System.err.println(ex);
                            return null;
                        }
                    }).collect(Collectors.joining(", ")));
                    if (Database.DEBUG_SQL) {
                        System.err.println("SQL INSERTION: " + sql);
                    }
                    sqls.add(sql);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            });
            sqls.forEach((sql) -> database.executeUpdate(sql));
            sqls.clear();
            return true;
        } catch (Exception ex) {
            System.err.println("SQLUtil: serializeObjects error");
            ex.printStackTrace();
            return false;
        }
    }

    public static final <T> boolean removeObjects(Class<? extends T> clazz, Database database, T... objects) {
        return removeObjects(clazz, database, new ArrayList<>(Arrays.asList(objects)));
    }

    public static final <T> boolean removeObjects(Class<? extends T> clazz, Database database, ArrayList<T> objects) {
        return removeObjects(clazz, database, getSQLSerializer(clazz), objects);
    }

    public static final <T> boolean removeObjects(Class<? extends T> clazz, Database database, SQLSerializer serializer, T... objects) {
        return removeObjects(clazz, database, serializer, new ArrayList<>(Arrays.asList(objects)));
    }

    public static final <T> boolean removeObjects(Class<? extends T> clazz, Database database, SQLSerializer serializer, ArrayList<T> objects) {
        if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class)) {
            return false;
        }
        try {
            final SQLTable table = clazz.getAnnotation(SQLTable.class);
            return removeObjects(clazz, database, serializer, table.name(), objects);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final <T> boolean removeObjects(Class<? extends T> clazz, Database database, String table, T... objects) {
        return removeObjects(clazz, database, table, new ArrayList<>(Arrays.asList(objects)));
    }

    public static final <T> boolean removeObjects(Class<? extends T> clazz, Database database, String table, ArrayList<T> objects) {
        return removeObjects(clazz, database, getSQLSerializer(clazz), table, objects);
    }

    public static final <T> boolean removeObjects(Class<? extends T> clazz, Database database, SQLSerializer serializer, String table, T... objects) {
        return removeObjects(clazz, database, serializer, table, new ArrayList<>(Arrays.asList(objects)));
    }

    public static final <T> boolean removeObjects(Class<? extends T> clazz, Database database, SQLSerializer serializer, String table, ArrayList<T> objects) {
        if (clazz == null || database == null || !database.isConnected() || objects == null || objects.isEmpty()) {
            return false;
        }
        try {
            final List<Map.Entry<Field, SQLField>> fields = getFields(clazz, FieldType.of(false, true, false));
            final String sql_format = String.format("DELETE FROM %s WHERE %s;", table, (fields.isEmpty() ? "0" : fields.stream().map((field) -> field.getValue().column()).collect(Collectors.joining(" = %s AND ")) + " = %s"));
            final ArrayList<String> sqls = new ArrayList<>();
            objects.stream().forEach((object) -> {
                try {
                    final String sql = String.format(sql_format, fields.stream().map((field) -> {
                        try {
                            String defaultReturn = null;
                            try {
                                defaultReturn = quote(field.getKey().get(object));
                            } catch (Exception ex) {
                            }
                            if (serializer == null || !(serializer.acceptClass(field.getKey().getType()) || serializer.acceptField(field))) {
                                return defaultReturn;
                            } else {
                                return quote(serializer.serialize(field.getKey().get(object), field, defaultReturn));
                            }
                        } catch (Exception ex) {
                            System.err.println(ex);
                            return null;
                        }
                    }).collect(Collectors.toList()).toArray());
                    if (Database.DEBUG_SQL) {
                        System.err.println("SQL DELETION: " + sql);
                    }
                    sqls.add(sql);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            });
            sqls.forEach((sql) -> database.executeUpdate(sql));
            sqls.clear();
            return true;
        } catch (Exception ex) {
            System.err.println("SQLUtil: removeObjects error");
            ex.printStackTrace();
            return false;
        }
    }

    public static final <T> List<Map.Entry<Field, SQLField>> getFields(Class<? extends T> clazz, FieldType fieldType) {
        if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class) || fieldType == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(clazz.getFields()).stream().filter((field) -> field.isAnnotationPresent(SQLField.class)).map((field) -> new AbstractMap.SimpleEntry<>(field, field.getAnnotation(SQLField.class))).filter((field) -> {
            switch (fieldType) {
                case NONE:
                    return false;
                case SEND:
                    return field.getValue().send();
                case RECEIVE:
                    return field.getValue().receive();
                case REMOVE:
                    return field.getValue().primaryKey();
                case ALL:
                    return true;
                default:
                    return false;
            }
        }).sorted((field_1, field_2) -> field_1.getValue().index() - field_2.getValue().index()).collect(Collectors.toList());
    }

    public static final <T> List<Map.Entry<Field, SQLVariable>> getVariables(Class<? extends T> clazz, Predicate<Map.Entry<Field, SQLVariable>> predicate) {
        if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class)) {
            return new ArrayList<>();
        }
        return Arrays.asList(clazz.getFields()).stream().filter((field) -> field.isAnnotationPresent(SQLVariable.class)).map((field) -> new AbstractMap.SimpleEntry<>(field, field.getAnnotation(SQLVariable.class))).filter(predicate).collect(Collectors.toList());
    }

    public static final <T> List<Map.Entry<Field, SQLVariable>> getSerializerVariables(Class<? extends T> clazz) {
        return getVariables(clazz, (field) -> field.getValue().type() == SQLVariableType.SERIALIZER);
    }

    public static final <T> List<Map.Entry<Field, SQLVariable>> getDeserializerVariables(Class<? extends T> clazz) {
        return getVariables(clazz, (field) -> field.getValue().type() == SQLVariableType.DESERIALIZER);
    }

    public static final <T> SQLSerializer getSQLSerializer(Class<? extends T> clazz) {
        if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class)) {
            return null;
        }
        final List<Map.Entry<Field, SQLVariable>> variables = getSerializerVariables(clazz);
        if (variables.isEmpty()) {
            return null;
        }
        try {
            final Object object = variables.get(0).getKey().get(null);
            if (object == null || !(object instanceof SQLSerializer)) {
                return null;
            }
            return (SQLSerializer) object;
        } catch (Exception ex) {
            System.err.println("SQLUtil: getSQLSerializer error");
            ex.printStackTrace();
            return null;
        }
    }

    public static final <T> SQLDeserializer getSQLDeserializer(Class<? extends T> clazz) {
        if (clazz == null || !clazz.isAnnotationPresent(SQLTable.class)) {
            return null;
        }
        final List<Map.Entry<Field, SQLVariable>> variables = getDeserializerVariables(clazz);
        if (variables.isEmpty()) {
            return null;
        }
        try {
            final Object object = variables.get(0).getKey().get(null);
            if (object == null || !(object instanceof SQLDeserializer)) {
                return null;
            }
            return (SQLDeserializer) object;
        } catch (Exception ex) {
            System.err.println("SQLUtil: getSQLDeserializer error");
            ex.printStackTrace();
            return null;
        }
    }

    public static final String fromJDBCType(JDBCType type) {
        switch (type) {
            case VARCHAR:
                return type.name() + "(255)";
            case BIT:
            case TINYINT:
            case SMALLINT:
            case INTEGER:
            case BIGINT:
            case FLOAT:
            case REAL:
            case DOUBLE:
            case NUMERIC:
            case DECIMAL:
            case CHAR:
            case LONGVARCHAR:
            case DATE:
            case TIME:
            case TIMESTAMP:
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY:
            case NULL:
            case OTHER:
            case JAVA_OBJECT:
            case DISTINCT:
            case STRUCT:
            case ARRAY:
            case BLOB:
            case CLOB:
            case REF:
            case DATALINK:
            case BOOLEAN:
            case ROWID:
            case NCHAR:
            case NVARCHAR:
            case LONGNVARCHAR:
            case NCLOB:
            case SQLXML:
            case REF_CURSOR:
            case TIME_WITH_TIMEZONE:
            case TIMESTAMP_WITH_TIMEZONE:
            default:
                return type.name();
        }
    }

    public static final String quote(Object object) {
        if (object == null) {
            return null;
        }
        final String text = "" + object;
        if (text.isEmpty()) {
            return "";
        }
        return ((Util.isStringDigitsOnly(text) || (text.startsWith("'") && text.endsWith("'"))) ? text : "'" + text + "'");
    }

    private static enum FieldType {
        NONE,
        SEND,
        RECEIVE,
        REMOVE,
        ALL;

        public static final FieldType of(boolean send, boolean remove, boolean forceAll) {
            if (forceAll) {
                return ALL;
            } else if (remove) {
                return REMOVE;
            } else if (send) {
                return SEND;
            } else {
                return RECEIVE;
            }
        }
    }

}
