package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.sql.annotations.SQLField;
import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import de.codemakers.bot.supreme.util.Util;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQLUtil
 *
 * @author Paul
 */
public class SQLUtil {

    public static final <T> ArrayList<T> deserializeObjectsOfResultSet(Class<? extends T> clazz, ResultSet resultSet, SQLDeserializer deserializer) {
        return deserializeObjectsOfResultSet(clazz, resultSet, false, deserializer);
    }

    public static final <T> ArrayList<T> deserializeObjectsOfResultSet(Class<? extends T> clazz, ResultSet resultSet, boolean forceAll, SQLDeserializer deserializer) {
        try {
            if (clazz == null || resultSet == null || resultSet.isClosed() || resultSet.isAfterLast() || (!resultSet.isBeforeFirst() && !resultSet.next()) || !clazz.isAnnotationPresent(SQLTable.class)) {
                return null;
            }
            final List<Map.Entry<Field, SQLField>> fields = getFields(clazz, false, forceAll);
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
                    System.err.println(ex);
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

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz, SQLDeserializer deserializer) {
        return deserializeObjects(clazz, false, deserializer);
    }

    public static final <T> ArrayList<T> deserializeObjects(Class<? extends T> clazz, boolean forceAll, SQLDeserializer deserializer) {
        final SQLTable sqlTable = clazz.getAnnotation(SQLTable.class);
        final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM %s;", sqlTable.name());
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

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, SQLSerializer serializer, T... objects) {
        return serializeObjects(clazz, database, forceAll, serializer, new ArrayList<>(Arrays.asList(objects)));
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, SQLSerializer serializer, ArrayList<T> objects) {
        return serializeObjects(clazz, database, false, serializer, objects);
    }

    public static final <T> boolean serializeObjects(Class<? extends T> clazz, Database database, boolean forceAll, SQLSerializer serializer, ArrayList<T> objects) {
        if (clazz == null || objects == null || objects.isEmpty() || !clazz.isAnnotationPresent(SQLTable.class)) {
            return false;
        }
        try {
            final SQLTable sqlTable = clazz.getAnnotation(SQLTable.class);
            final List<Map.Entry<Field, SQLField>> fields = getFields(clazz, true, forceAll);
            final String sql_format = String.format("INSERT INTO %s (%s) VALUES (%%s);", sqlTable.name(), fields.stream().map((field) -> field.getValue().column()).collect(Collectors.joining(", ")));
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
                    }).collect(Collectors.joining(", ")));
                    System.err.println("SQL: " + sql);
                    sqls.add(sql);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            });
            final String sql = sqls.stream().collect(Collectors.joining());
            sqls.clear();
            if (true) {
                return true;
            }
            if (sqls.size() >= Integer.MAX_VALUE) {
                database.executeLargeUpdate(sql);
            } else {
                database.executeUpdate(sql);
            }
            return true;
        } catch (Exception ex) {
            System.err.println("SQLUtil: serializeObjects error");
            ex.printStackTrace();
            return false;
        }
    }

    public static final <T> List<Map.Entry<Field, SQLField>> getFields(Class<? extends T> clazz, boolean send, boolean forceAll) {
        if (clazz == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(clazz.getFields()).stream().filter((field) -> field.isAnnotationPresent(SQLField.class)).map((field) -> new AbstractMap.SimpleEntry<>(field, field.getAnnotation(SQLField.class))).filter((field) -> (forceAll || (send ? field.getValue().send() : field.getValue().receive()))).sorted((field_1, field_2) -> field_1.getValue().index() - field_2.getValue().index()).collect(Collectors.toList());
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

}
