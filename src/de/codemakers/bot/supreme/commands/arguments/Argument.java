package de.panzercraft.bot.supreme.commands.arguments;

/**
 * Argument
 *
 * @author Panzer1119
 */
public class Argument {

    private final String[] prefixes;
    private final String argument;

    public Argument(String argument) {
        this(argument, new String[0]);
    }

    public Argument(String argument, String... prefixes) {
        this.argument = argument;
        this.prefixes = prefixes;
    }

    public final boolean hasPrefixes() {
        return prefixes != null && prefixes.length > 0;
    }
    
    public final int getPrefixesLength() {
        return hasPrefixes() ? prefixes.length : -1;
    }
    
    public final String[] getPrefixes() {
        return prefixes;
    }

    public final String getArgument() {
        return argument;
    }

    public final String getCompleteArgument(int prefix) {
        if (prefixes == null || prefix < 0) {
            return argument;
        }
        return prefixes[prefix] + argument;
    }

    @Override
    public final String toString() {
        return getCompleteArgument(0);
    }
    
    public static final Argument ofString(String argument) {
        return new Argument(argument);
    }

}
