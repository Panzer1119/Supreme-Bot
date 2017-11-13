package de.codemakers.bot.supreme.sql.annotations;

import de.codemakers.bot.supreme.sql.NullBehavior;
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

    String length() default "0";

    boolean send() default true;

    boolean receive() default true;

    NullBehavior nullBehavior() default NullBehavior.STANDARD;

    String defaultValue() default "";

    boolean primaryKey() default false;

    String extra() default "";

    JDBCType type();

}
