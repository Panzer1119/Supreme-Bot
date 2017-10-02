package de.codemakers.bot.supreme.util;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * TimeUnit
 *
 * @author Panzer1119
 */
public enum TimeUnit {
    MILLISECONDS('n', "millisecond", 1L),
    SECONDS('s', "second", 1000L * MILLISECONDS.millis),
    MINUTES('m', "minute", 60L * SECONDS.millis),
    HOURS('h', "hour", 60L * MINUTES.millis),
    DAYS('d', "day", 24L * HOURS.millis),
    WEEKS('w', "week", 7L * DAYS.millis),
    YEARS('y', "year", (long) ((365.0 / 364.0) * 52L * WEEKS.millis));

    private final Character name_short;
    private final String name_long;
    private final long millis;

    private TimeUnit(Character name_short, String name_long, long millis) {
        this.name_short = name_short;
        this.name_long = name_long;
        this.millis = millis;
    }

    public final Character getNameShort() {
        return name_short;
    }

    public final String getNameLong() {
        return name_long;
    }

    public final long getMillis() {
        return millis;
    }

    public static final TimeUnit ofNameShort(Character name_short) {
        for (TimeUnit timeUnit : values()) {
            if (Objects.equals(timeUnit.name_short, name_short)) {
                return timeUnit;
            }
        }
        return null;
    }

    public static final TimeUnit ofNameLong(String name_long) {
        for (TimeUnit timeUnit : values()) {
            if (Objects.equals(timeUnit.name_long, name_long)) {
                return timeUnit;
            }
        }
        return null;
    }

    public static final TimeUnit ofMillis(long millis) {
        for (TimeUnit timeUnit : values()) {
            if (timeUnit.millis == millis) {
                return timeUnit;
            }
        }
        return null;
    }

    public static final void forEach(Consumer<TimeUnit> action) {
        final int length = values().length;
        for (int i = 0; i < length; i++) {
            action.accept(values()[length - i - 1]);
        }
    }
}
