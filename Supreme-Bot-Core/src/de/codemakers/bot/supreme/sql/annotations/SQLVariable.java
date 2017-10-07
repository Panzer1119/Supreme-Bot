package de.codemakers.bot.supreme.sql.annotations;

import de.codemakers.bot.supreme.sql.SQLVariableType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SQLVariable
 *
 * @author Panzer1119
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLVariable {

    SQLVariableType type();

}
