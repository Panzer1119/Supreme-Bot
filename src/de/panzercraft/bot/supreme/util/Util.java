package de.panzercraft.bot.supreme.util;

import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * Util
 *
 * @author Panzer1119
 */
public class Util {

    public static <T> int indexOf(T[] array, T toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            final T t = array[i];
            if (t == null) {
                continue;
            }
            if (t.equals(toTest) || t == toTest) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int indexOf(T[] array, FilterSimplex<T> filter) {
        if (array == null || array.length == 0 || filter == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            final T t = array[i];
            if (filter.filter(t)) {
                return i;
            }
        }
        return -1;
    }

    public static <T> boolean contains(T[] array, T toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return false;
        }
        for (T t : array) {
            if (t == null) {
                continue;
            }
            if (t.equals(toTest) || t == toTest) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean contains(T[] array, T... toTest) {
        return contains(array, null, toTest);
    }

    public static <T> boolean contains(T[] array, FilterDuplex<T> filter, T... toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return false;
        }
        if (filter == null) {
            filter = FilterDuplex.createFilterEquals();
        }
        if (toTest.length == 0) {
            return true;
        }
        for (T t_1 : toTest) {
            boolean found = false;
            for (T t_2 : array) {
                if (filter.filter(t_1, t_2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public interface FilterDuplex<T> {

        public boolean filter(T arrayEntry, T toTestEntry);

        public static <T> FilterDuplex<T> createFilterEquals() {
            return (T arrayEntry, T toTestEntry) -> {
                if (arrayEntry == null || toTestEntry == null) {
                    return false;
                }
                return (arrayEntry == toTestEntry || arrayEntry.equals(toTestEntry) || toTestEntry.equals(arrayEntry));
            };
        }

        public static FilterDuplex<String> createStringFilterEqualsIgnoreCase() {
            return (String arrayEntry, String toTestEntry) -> {
                if (arrayEntry == null || toTestEntry == null) {
                    return false;
                }
                return (arrayEntry.equalsIgnoreCase(toTestEntry) || toTestEntry.equalsIgnoreCase(arrayEntry));
            };
        }

        public static <T> FilterDuplex<T> createFilterAlways() {
            return (T arrayEntry, T toTestEntry) -> {
                return true;
            };
        }

        public static <T> FilterDuplex<T> createFilterNever() {
            return (T arrayEntry, T toTestEntry) -> {
                return false;
            };
        }

    }

    public static interface FilterSimplex<T> {

        public boolean filter(T entry);

    }

    public static final boolean deleteMessage(Message message, long delayInMillis) {
        if (delayInMillis < 0) {
            return false;
        } else if (delayInMillis == 0) {
            message.delete().queue();
            return true;
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                message.delete().queue();
            }
        }, delayInMillis);
        return true;
    }
    
    public static final boolean sendPrivateMessage(User user, String message) {
        try {
            final PrivateChannel privateChannel = user.openPrivateChannel().complete();
            privateChannel.sendMessage(message).queue();
            privateChannel.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }
    
    public static final boolean sendPrivateMessageFormat(User user, String message, Object... format) {
        try {
            final PrivateChannel privateChannel = user.openPrivateChannel().complete();
            privateChannel.sendMessageFormat(message, format).queue();
            privateChannel.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }
    
    public static final boolean sendPrivateMessage(User user, Message message) {
        try {
            final PrivateChannel privateChannel = user.openPrivateChannel().complete();
            privateChannel.sendMessage(message).queue();
            privateChannel.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }
    
    public static final boolean sendPrivateMessage(User user, MessageEmbed message) {
        try {
            final PrivateChannel privateChannel = user.openPrivateChannel().complete();
            privateChannel.sendMessage(message).queue();
            privateChannel.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }

}
