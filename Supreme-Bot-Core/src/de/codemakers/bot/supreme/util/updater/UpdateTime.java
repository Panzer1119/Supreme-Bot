package de.codemakers.bot.supreme.util.updater;

/**
 * UpdateTime
 *
 * @author Panzer1119
 */
public class UpdateTime {

    private long firstTime = -1;
    private long lastTime = -1;
    private long deltaTime = 0;
    private long duration = -1;
    private boolean remove = false;
    private boolean isUpdating = false;

    public UpdateTime() {
        this(0);
    }

    public UpdateTime(long deltaTime) {
        this(deltaTime, -1);
    }

    public UpdateTime(long deltaTime, long duration) {
        setDeltaTime(deltaTime);
        setDuration(duration);
    }

    public final long getLastTime() {
        return lastTime;
    }

    public final UpdateTime setLastTime(long lastTime) {
        this.lastTime = lastTime;
        if (this.firstTime < 0) {
            this.firstTime = lastTime;
        }
        return this;
    }

    public final long getDeltaTime() {
        return deltaTime;
    }

    public final UpdateTime setDeltaTime(long deltaTime) {
        this.deltaTime = Math.max(0, deltaTime);
        return this;
    }

    public final long getDuration() {
        return duration;
    }

    public final UpdateTime setDuration(long duration) {
        this.duration = duration;
        if (duration < 0) {
            duration = -1;
        }
        return this;
    }

    public final long getFirstTime() {
        return firstTime;
    }

    public final boolean isRemove() {
        return remove;
    }

    public final UpdateTime setRemove(boolean remove) {
        this.remove = remove;
        return this;
    }

    public final boolean isUpdating() {
        return isUpdating;
    }

    public final UpdateTime setIsUpdating(boolean isUpdating) {
        this.isUpdating = isUpdating;
        return this;
    }

    public final boolean isEnded(long timestamp) {
        if (duration < 0 || firstTime < 0) {
            return false;
        }
        return timestamp >= (firstTime + duration);
    }

    public final boolean needsUpdate(long timestamp) {
        if (timestamp < 0) {
            return false;
        }
        if (lastTime < 0) {
            return true;
        }
        return timestamp >= (lastTime + deltaTime);
    }

    public final UpdateTime update(long timestamp, long deltaTime) {
        setLastTime(timestamp);
        if (deltaTime >= 0) {
            setDeltaTime(deltaTime);
        }
        return this;
    }

    @Override
    public String toString() {
        return "UpdateTime{" + "firstTime=" + firstTime + ", lastTime=" + lastTime + ", deltaTime=" + deltaTime + ", duration=" + duration + ", remove=" + remove + ", isUpdating=" + isUpdating + '}';
    }

}
