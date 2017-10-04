package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.sql.annotations.SQLField;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Map;

/**
 * SQLSerializer
 *
 * @author Panzer1119
 */
public interface SQLDeserializer {

    public Object deserialize(ResultSet resultSet, Map.Entry<Field, SQLField> field, Object defaultReturn) throws Exception;

    public boolean acceptClass(Class<?> clazz);

    public boolean acceptField(Map.Entry<Field, SQLField> field);

}
