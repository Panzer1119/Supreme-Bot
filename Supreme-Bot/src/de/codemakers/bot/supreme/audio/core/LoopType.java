package de.codemakers.bot.supreme.audio.core;

/**
 * LoopType
 *
 * @author Panzer1119
 */
public enum LoopType {
    NONE            (false, false, "not looping"),
    LOOP            (true, false, "looping"),
    LOOP_SINGLE     (true, true, "single looping");
    
    private final boolean loop;
    private final boolean single;
    private final String text;
    
    LoopType (boolean loop, boolean single, String text) {
        this.loop = loop;
        this.single = single;
        this.text = text;
    }

    public final boolean isLoop() {
        return loop;
    }

    public final boolean isSingle() {
        return single;
    }
    
    public final String getText() {
        return text;
    }
    
    public static final LoopType of(boolean loop, boolean single) {
        if (!loop && !single) {
            return NONE;
        } else if (loop && !single) {
            return LOOP;
        } else if (loop && single) {
            return LOOP_SINGLE;
        } else {
            return null;
        }
    }
}
