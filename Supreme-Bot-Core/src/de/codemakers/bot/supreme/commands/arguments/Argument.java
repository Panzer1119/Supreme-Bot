package de.codemakers.bot.supreme.commands.arguments;

/**
 * Argument
 *
 * @author Panzer1119
 */
public class Argument {

    private final String[] prefixes;
    private final String argument;
    private String[] aliases = null;

    public Argument(String argument) {
        this(argument, new String[0]);
    }

    public Argument(String argument, String[] prefixes, String... aliases) {
        this.argument = argument;
        this.prefixes = prefixes;
        this.aliases = aliases;
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
    
    public final boolean hasAliases() {
        return aliases != null && aliases.length > 0;
    }
    
    public final int getAliasesLength() {
        return hasAliases() ? aliases.length : -1;
    }
    
    public final String[] getAliases() {
        return aliases;
    }

    public final String getCompleteArgument(int prefix, int alias) {
        String p = null;
        if (prefix < 0 || prefix >= getPrefixesLength()) {
            p = "";
        } else {
            p = prefixes[prefix];
        }
        if (alias < 0 || alias >= getAliasesLength()) {
            return p + argument;
        } else {
            return p + aliases[alias];
        }
    }
    
    public final boolean takes(ArgumentConsumeType type, String argument_raw) {
        if (argument_raw == null) {
            return false;
        }
        if ((type.isIgnoreCase() && argument_raw.equalsIgnoreCase(argument)) || argument_raw.equals(argument)) {
            return true;
        }
        for (int p = -1; p < (hasPrefixes() ? getPrefixesLength() : 1); p++) {
            for (int a = -1; a < (hasAliases() ? getAliasesLength() : 1); a++) {
                final String temp = getCompleteArgument(p, a);
                if ((type.isIgnoreCase() && argument_raw.equalsIgnoreCase(temp)) || argument_raw.equals(temp)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public final String toString() {
        return getCompleteArgument(0, -1);
    }
    
    public static final Argument ofString(String argument) {
        return new Argument(argument);
    }

}
