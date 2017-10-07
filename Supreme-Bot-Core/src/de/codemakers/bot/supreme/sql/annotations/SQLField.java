package de.codemakers.bot.supreme.sql.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.JDBCType;

/**
 * SQLField
 *
 * @author Panzer1119
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLField {

    int index();

    String column();

    boolean send() default true;

    boolean receive() default true;

    JDBCType type();

}
