package de.codemakers.bot.supreme.util;

import java.time.Duration;
import java.time.Instant;

/**
 * Timeout
 *
 * @author Panzer1119
 */
public class Timeout {

    private Instant start;
    private Duration duration;
    private Runnable run_after_timeout;

    public Timeout(long amount, TimeUnit unit, Runnable run_after_timeout) {
        this(Instant.now(), unit == null ? null : Duration.ofMillis(unit.toMillis(amount)), run_after_timeout);
    }

    public Timeout(Instant start, long amount, TimeUnit unit, Runnable run_after_timeout) {
        this(start, unit == null ? null : Duration.ofMillis(unit.toMillis(amount)), run_after_timeout);
    }

    public Timeout(Duration duration, Runnable run_after_timeout) {
        this(Instant.now(), duration, run_after_timeout);
    }

    public Timeout(Instant start, Duration duration, Runnable run_after_timeout) {
        this.start = start;
        this.duration = duration.isNegative() ? null : duration;
        this.run_after_timeout = run_after_timeout;
    }

    public final Instant getStart() {
        return start;
    }

    final Timeout setStart(Instant start) {
        this.start = start;
        return this;
    }

    public final Duration getDuration() {
        return duration;
    }

    final Timeout setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public final boolean isRunningAfterTimeout() {
        return run_after_timeout != null;
    }

    public final Runnable getRunAfterTimeout() {
        return run_after_timeout;
    }

    final Timeout setRunAfterTimeout(Runnable run_after_timeout) {
        this.run_after_timeout = run_after_timeout;
        return this;
    }

    public final Timeout runAfterTimeout() {
        if (isRunningAfterTimeout()) {
            run_after_timeout.run();
        }
        return this;
    }

    public final boolean isTimeout() {
        return isTimeout(Instant.now());
    }

    public final boolean isTimeout(Instant instant) {
        if (duration == null) {
            return false;
        }
        return Duration.between(start, instant).compareTo(duration) > 0;
    }

}
