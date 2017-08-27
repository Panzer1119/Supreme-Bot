package de.codemakers.bot.supreme.commands.arguments;

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
    CONSUME_FIRST_IGNORE_CASE   (true, false, true),
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
    
    public final ArgumentConsumeType toggleConsume() {
        switch (this) {
            case FIRST:
                return CONSUME_FIRST;
            case FIRST_IGNORE_CASE:
                return CONSUME_FIRST_IGNORE_CASE;
            case ALL:
                return CONSUME_ALL;
            case ALL_IGNORE_CASE:
                return CONSUME_ALL_IGNORE_CASE;
            case CONSUME_FIRST:
                return FIRST;
            case CONSUME_FIRST_IGNORE_CASE:
                return FIRST_IGNORE_CASE;
            case CONSUME_ALL:
                return ALL;
            case CONSUME_ALL_IGNORE_CASE:
                return ALL_IGNORE_CASE;
            default:
                return null;
        }
    }
    
    public final ArgumentConsumeType toggleAll() {
        switch (this) {
            case FIRST:
                return ALL;
            case FIRST_IGNORE_CASE:
                return ALL_IGNORE_CASE;
            case ALL:
                return FIRST;
            case ALL_IGNORE_CASE:
                return FIRST_IGNORE_CASE;
            case CONSUME_FIRST:
                return CONSUME_ALL;
            case CONSUME_FIRST_IGNORE_CASE:
                return CONSUME_ALL_IGNORE_CASE;
            case CONSUME_ALL:
                return CONSUME_FIRST;
            case CONSUME_ALL_IGNORE_CASE:
                return CONSUME_FIRST_IGNORE_CASE;
            default:
                return null;
        }
    }
    
    public final ArgumentConsumeType toggleIgnoreCase() {
        switch (this) {
            case FIRST:
                return FIRST_IGNORE_CASE;
            case FIRST_IGNORE_CASE:
                return FIRST;
            case ALL:
                return ALL_IGNORE_CASE;
            case ALL_IGNORE_CASE:
                return ALL;
            case CONSUME_FIRST:
                return CONSUME_FIRST_IGNORE_CASE;
            case CONSUME_FIRST_IGNORE_CASE:
                return CONSUME_FIRST;
            case CONSUME_ALL:
                return CONSUME_ALL_IGNORE_CASE;
            case CONSUME_ALL_IGNORE_CASE:
                return CONSUME_ALL;
            default:
                return null;
        }
    }

}
