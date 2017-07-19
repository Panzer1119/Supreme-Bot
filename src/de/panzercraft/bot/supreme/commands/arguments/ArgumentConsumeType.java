package de.panzercraft.bot.supreme.commands.arguments;

/**
 * ArgumentConsumeType
 * 
 * @author Panzer1119
 */
public enum ArgumentConsumeType {
    FIRST               (false, false),
    FIRST_IGNORE_CASE   (false, true),
    ALL                 (true, false),
    ALL_IGNORE_CASE     (true, true);
    
    private final boolean consumeAll;
    private final boolean ignoreCase;
    
    ArgumentConsumeType(boolean consumeAll, boolean ignoreCase) {
        this.consumeAll = consumeAll;
        this.ignoreCase = ignoreCase;
    }

    public final boolean isConsumingAll() {
        return consumeAll;
    }

    public final boolean isIgnoringCase() {
        return ignoreCase;
    }
}
