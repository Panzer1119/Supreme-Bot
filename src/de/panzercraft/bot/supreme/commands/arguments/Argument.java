package de.panzercraft.bot.supreme.commands.arguments;

import de.panzercraft.bot.supreme.util.Util;

/**
 * Argument
 *
 * @author Panzer1119
 */
public class Argument {

    private final String prefix;
    private final String argument;

    public Argument(String argument) {
        this("", argument);
    }

    public Argument(String prefix, String argument) {
        this.prefix = prefix;
        this.argument = argument;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final String getArgument() {
        return argument;
    }

    public final String getCompleteArgument() {
        return prefix + argument;
    }

    @Override
    public final String toString() {
        return getCompleteArgument();
    }
    
    public static final Argument ofString(String argument) {
        return new Argument(argument);
    }
    
    public static final Argument ofString(String argument, String... prefixes) {
        if (prefixes == null || prefixes.length == 0) {
            return new Argument(argument);
        } else {
            Util.contains(prefixes, null, argument);
            final int index = Util.indexOf(prefixes, (entry) -> {
                return argument.startsWith(entry);
            });
            if (index == -1) {
                return new Argument(argument);
            } else {
                return new Argument(argument.substring(0, prefixes[index].length()), argument.substring(prefixes[index].length()));
            }
        }
    }

}
