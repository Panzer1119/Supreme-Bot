package de.panzercraft.bot.supreme.commands.arguments;

/**
 * ArgumentConsumeType
 * 
 * @author Panzer1119
 */
public enum ArgumentConsumeType {
    FIRST                       (false, false, false),
    FIRST_IGNORE_CASE           (false, false, true),
    ALL                         (false, true, false),
    ALL_IGNORE_CASE             (false, true, true),
    CONSUME_FIRST               (true, false, false),
    CONSUME_FIRST_IGNORE_CASE   (true, false, false),
    CONSUME_ALL                 (true, true, false),
    CONSUME_ALL_IGNORE_CASE     (true, true, true);
    
    private final boolean consume;
    private final boolean all;
    private final boolean ignoreCase;

    ArgumentConsumeType(boolean consume, boolean all, boolean ignoreCase) {
        this.consume = consume;
        this.all = all;
        this.ignoreCase = ignoreCase;
    }

    public final boolean isConsume() {
        return consume;
    }

    public final boolean isAll() {
        return all;
    }

    public final boolean isIgnoreCase() {
        return ignoreCase;
    }

}
