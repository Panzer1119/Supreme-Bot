package de.codemakers.bot.supreme.entities;

import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.TimeUnit;
import de.codemakers.bot.supreme.util.updater.Updateable;
import de.codemakers.bot.supreme.util.updater.Updater;
import net.dv8tion.jda.core.entities.Message;

/**
 * MessageUpdater
 *
 * @author Panzer1119
 */
public abstract class MessageUpdater {

    public final MessageUpdater ME = this;
    protected final Message message_first;
    protected Message message;
    protected final long period;
    protected final TimeUnit unit;
    protected final Updateable updateable;

    public MessageUpdater(Message message) {
        this(message, 1, TimeUnit.SECONDS);
    }

    public MessageUpdater(Message message, long period, TimeUnit unit) {
        if (message == null) {
            throw new NullPointerException("The message must not be null!");
        } else if (period <= 0) {
            throw new Error("The period must not be zero or negative!");
        } else if (unit == null) {
            throw new NullPointerException("The time unit must not be null!");
        } else {
            this.message_first = message;
            this.message = message;
            this.period = period;
            this.unit = unit;
            updateable = new Updateable() {
                @Override
                public final long update(long timestamp) {
                    ME.updateThis();
                    return unit.toMillis(period);
                }

                @Override
                public final void delete() {
                    ME.delete();
                }
            };
            Updater.addUpdateable(updateable);
        }
    }

    public final Message getMessage() {
        return message;
    }

    public final boolean updateThis() {
        if (message == null) {
            return false;
        }
        try {
            message = Standard.getUpdatedMessage(message);
            if (message == null) {
                deleteThis();
                return false;
            }
            update();
            return message != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public final boolean deleteThis() {
        try {
            Updater.removeUpdateable(updateable);
            delete();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public abstract void update();

    public abstract void delete();

}
