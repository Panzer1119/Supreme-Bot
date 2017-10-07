package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.sql.annotations.SQLField;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * SQLSerializer
 *
 * @author Panzer1119
 */
public interface SQLSerializer {

    public String serialize(Object object, Map.Entry<Field, SQLField> field, String defaultReturn) throws Exception;

    public boolean acceptClass(Class<?> clazz);

    public boolean acceptField(Map.Entry<Field, SQLField> field);

}
