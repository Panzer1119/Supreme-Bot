package de.codemakers.bot.supreme.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

/**
 * Util
 *
 * @author Panzer1119
 */
public class Util {

    public static final ArrayList<Timer> TIMERS = new ArrayList<>();

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
        if (message == null || delayInMillis < 0) {
            return false;
        } else if (delayInMillis == 0) {
            message.delete().queue();
            return true;
        }
        sheduleTimerAndRemove(() -> {
            message.delete().queue();
        }, delayInMillis);
        return true;
    }

    public static final Timer sheduleTimerAtFixedRateAndRemove(RunnableFeedback run, Runnable end, long delay, long period, long duration) {
        return sheduleTimerAtFixedRateAndRemove(run, end, delay, period, duration, createTimer());
    }

    public static final Timer sheduleTimerAtFixedRateAndRemove(RunnableFeedback run, Runnable end, long delay, long period, long duration, Timer timer) {
        final Timer t = sheduleTimerAndRemove(() -> {
            try {
                end.run();
            } catch (Exception ex) {
            }
            timer.cancel();
            TIMERS.remove(timer);
        }, duration);
        sheduleTimerAtFixedRate(() -> {
            try {
                if (!run.run()) {
                    t.killAndFireAllTask();
                    timer.cancel();
                }
            } catch (Exception ex) {
            }
        }, delay, period, timer);
        return t;
    }

    public static final Timer sheduleTimerAtFixedRate(Runnable run, long delay, long period) {
        return sheduleTimerAtFixedRate(run, delay, period, createTimer());
    }

    /**
     * @param run Runnable
     * @param delay delay in milliseconds before task is to be executed.
     * @param period time in milliseconds between successive task executions.
     * @param timer Timer
     */
    public static final Timer sheduleTimerAtFixedRate(Runnable run, long delay, long period, Timer timer) {
        if (timer == null || run == null) {
            return null;
        } else if (delay < 0 || period < 0) {
            return null;
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    run.run();
                } catch (Exception ex) {
                }
            }
        }, delay, period);
        return timer;
    }

    public static final Timer sheduleTimerAndRemove(Runnable run, long delay) {
        return sheduleTimerAndRemove(run, delay, createTimer());
    }

    public static final Timer sheduleTimerAndRemove(Runnable run, long delay, Timer timer) {
        return sheduleTimer(() -> {
            try {
                run.run();
            } catch (Exception ex) {
            }
            TIMERS.remove(timer);
        }, delay, timer);
    }

    public static final Timer sheduleTimer(Runnable run, long delay) {
        return sheduleTimer(run, delay, createTimer());
    }

    /**
     * @param run Runnable
     * @param delay delay in milliseconds before task is to be executed.
     * @param timer Timer
     */
    public static final Timer sheduleTimer(Runnable run, long delay, Timer timer) {
        if (timer == null || run == null) {
            return null;
        } else if (delay < 0) {
            return null;
        } else if (delay == 0) {
            run.run();
            return null;
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    run.run();
                } catch (Exception ex) {
                }
            }
        }, delay);
        return timer;
    }

    public static final Timer createTimer() {
        final Timer timer = new Timer();
        TIMERS.add(timer);
        return timer;
    }

    public static final boolean killAndFireAllTimerTask() {
        try {
            TIMERS.stream().forEach((timer) -> {
                timer.killAndFireAllTask();
            });
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }

    public static final boolean killAllTimer() {
        try {
            TIMERS.stream().forEach((timer) -> {
                timer.cancel();
            });
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
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

    public static final Message sendAndWaitPrivateMessage(User user, String message) {
        try {
            final PrivateChannel privateChannel = user.openPrivateChannel().complete();
            final Message temp = privateChannel.sendMessage(message).complete();
            privateChannel.close();
            return temp;
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }

    public static final Message sendAndWaitPrivateMessageFormat(User user, String message, Object... format) {
        try {
            final PrivateChannel privateChannel = user.openPrivateChannel().complete();
            final Message temp = privateChannel.sendMessageFormat(message, format).complete();
            privateChannel.close();
            return temp;
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }

    public static final Message sendAndWaitPrivateMessage(User user, Message message) {
        try {
            final PrivateChannel privateChannel = user.openPrivateChannel().complete();
            final Message temp = privateChannel.sendMessage(message).complete();
            privateChannel.close();
            return temp;
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }

    public static final Message sendAndWaitPrivateMessage(User user, MessageEmbed message) {
        try {
            final PrivateChannel privateChannel = user.openPrivateChannel().complete();
            final Message temp = privateChannel.sendMessage(message).complete();
            privateChannel.close();
            return temp;
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }

    public static final String generateRandomString(char[] alphabet, int length) {
        String out = "";
        for (int i = 0; i < length; i++) {
            out += alphabet[(int) (Math.random() * alphabet.length)];
        }
        return out;
    }

    public static final String joinNumbers(String separator, int... numbers) {
        if (numbers == null || numbers.length == 0) {
            return "";
        }
        String out = "";
        for (int i : numbers) {
            out += (separator != null ? separator : "") + i;
        }
        if (separator != null) {
            out = out.substring(separator.length());
        }
        return out;
    }

    public static final String joinList(List list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return (String) list.stream().collect(Collectors.joining());
    }

    public static final String joinList(List list, String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return (String) list.stream().collect(Collectors.joining(delimiter));
    }

    public static final String joinList(List list, String delimiter, String prefix, String suffix) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return (String) list.stream().collect(Collectors.joining(delimiter, prefix, suffix));
    }

    public static final String rolesToString(List<Role> roles, boolean asMention) {
        if (roles == null || roles.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        roles.stream().forEach((role) -> {
            if (asMention) {
                sb.append(role.getAsMention());
            } else {
                sb.append("\"");
                sb.append(role.getName());
                sb.append("\"");
            }
            sb.append(", ");
        });
        sb.delete(sb.length() - ", ".length(), sb.length());
        return sb.toString();
    }

}
