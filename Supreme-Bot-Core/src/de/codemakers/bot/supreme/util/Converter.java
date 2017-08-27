package de.codemakers.bot.supreme.util;

/**
 * Converter
 *
 * @author Panzer1119
 */
public interface Converter<S, D> {

    public D convert(S source);

}
