package de.codemakers.bot.supreme.util;

import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.settings.Config;
import de.codemakers.io.file.AdvancedFile;
import java.io.BufferedReader;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public static final String generateRandomString(int length) {
        return generateRandomString(Standard.STANDARD_NAMESPACE, length);
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

    public static final AdvancedFile generateRandomAdvancedFile(AdvancedFile parent, int length, String prefix, String suffix) {
        return generateRandomAdvancedFile(parent, Standard.STANDARD_NAMESPACE, length, prefix, suffix);
    }

    public static final AdvancedFile generateRandomAdvancedFile(AdvancedFile parent, char[] alphabet, int length, String prefix, String suffix) {
        if (length < 0) {
            return null;
        }
        AdvancedFile advancedFile = null;
        while (advancedFile == null || advancedFile.exists()) {
            advancedFile = new AdvancedFile(parent, String.format("%s%s%s", prefix, (length != 0 ? generateRandomString(alphabet, length) : ""), suffix));
        }
        return advancedFile;
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

    public static final String rolesToString(List<Role> roles, boolean asMention, boolean respectMutes) {
        if (roles == null || roles.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        roles.stream().forEach((role) -> {
            if (respectMutes && asMention) {
                sb.append(Config.CONFIG.getRoleNameForRole(role));
            } else if (asMention) {
                sb.append(role.getAsMention());
            } else {
                sb.append(Standard.getCompleteName(role));
            }
            sb.append(", ");
        });
        sb.delete(sb.length() - ", ".length(), sb.length());
        return sb.toString();
    }

    public static <T> String makeTable(List<T> items, Converter<T, String> converter) {
        return makeTable(items, converter, 16, 4);
    }

    /**
     * Makes a controllers-like display of list of items
     *
     * @param items items in the controllers
     * @param columnLength length of a column(filled up with whitespace)
     * @param columns amount of columns
     * @return formatted controllers
     */
    public static <T> String makeTable(List<T> items, Converter<T, String> converter, int columnLength, int columns) {
        final StringBuilder sb = new StringBuilder();
        sb.append("```").append(Standard.NEW_LINE_DISCORD);
        int counter = 0;
        for (T item : items) {
            counter++;
            sb.append(String.format("%-" + columnLength + "s", converter.convert(item)));
            if (counter % columns == 0) {
                sb.append(Standard.NEW_LINE_DISCORD);
            }
        }
        if (counter % columns != 0) {
            sb.append(Standard.NEW_LINE_DISCORD);
        }
        sb.append("```").append(Standard.NEW_LINE_DISCORD);
        return sb.toString();
    }

    public static String makeTable(String text) {
        return String.format("```%n%s%n```%n", text);
    }

    public static final int getGoodSquareNumber(int number) {
        final double temp = Math.sqrt(number);
        if (temp % 1.0 == 0) {
            return (int) temp;
        } else {
            return (int) (temp + 1);
        }
    }

    /**
     * Example:
     *
     * text = "123Standard.NEW_LINE_DISCORD456Standard.NEW_LINE_DISCORD789";
     *
     * text_new = modifyColumnwiseToString(text, "|", ":");
     *
     * text_new ==
     * "|123:Standard.NEW_LINE_DISCORD|456:Standard.NEW_LINE_DISCORD|789:";
     *
     * @param text Text to modify
     * @param delimiter Delimiter
     * @param prefix Prefix
     * @param suffix Suffix
     * @return Modified String
     */
    public static final String modifyColumnwiseToString(String text, String delimiter, String prefix, String suffix) {
        if (prefix == null && suffix == null) {
            return text;
        }
        if (text == null) {
            return null;
        } else if (text.isEmpty()) {
            return prefix + suffix;
        }
        if (delimiter == null) {
            delimiter = Standard.NEW_LINE_DISCORD;
        }
        final String[] split = text.split(delimiter);
        String out = "";
        for (String g : split) {
            out += prefix + g + suffix + delimiter;
        }
        return out;
    }

    /**
     * Returns the time from a String by using RegEx
     *
     * @param text String
     * @return Time in milliseconds
     */
    public static final Long getTime(String text) {
        if (text == null) {
            return null;
        }
        try {
            final Matcher matcher = Standard.getTimePattern().matcher(text);
            if (!matcher.find()) {
                return null;
            }
            Double time = 0.0;
            do {
                Double number;
                String type;
                try {
                    type = matcher.group(2);
                    number = Double.parseDouble(matcher.group(1));
                    type = type.toLowerCase();
                } catch (Exception ex) {
                    continue;
                }
                if (type.startsWith("y")) {
                    time += TimeUnit.YEARS.getMillis() * number;
                } else if (type.startsWith("w")) {
                    time += TimeUnit.WEEKS.getMillis() * number;
                } else if (type.startsWith("d")) {
                    time += TimeUnit.DAYS.getMillis() * number;
                } else if (type.startsWith("h")) {
                    time += TimeUnit.HOURS.getMillis() * number;
                } else if (type.startsWith("m")) {
                    time += TimeUnit.MINUTES.getMillis() * number;
                } else if (type.startsWith("s")) {
                    time += TimeUnit.SECONDS.getMillis() * number;
                } else {
                    time += TimeUnit.MILLISECONDS.getMillis() * number;
                }
            } while (matcher.find());
            return Math.round(time);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final boolean stringEquals(String text, String[] toTest) {
        for (String g : toTest) {
            if (text.equals(g)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean stringEqualsIgnoreCase(String text, String[] toTest) {
        for (String g : toTest) {
            if (text.equalsIgnoreCase(g)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean stringContains(String text, String[] toTest) {
        for (String g : toTest) {
            if (text.contains(g)) {
                return true;
            }
        }
        return false;
    }

    public static final <T> T[] concatArrays(T[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            return null;
        }
        return (T[]) Arrays.asList(arrays).toArray();
    }

    public static final String getTimeAsString(long time, boolean withMilliseconds) {
        return getTimeAsString(time, false, withMilliseconds);
    }

    public static final String getTimeAsString(long time, boolean longNames, boolean withMilliseconds) {
        return getTimeAsString(time, longNames, false, withMilliseconds);
    }

    public static final String getTimeAsString(long time, boolean longNames, boolean withWhitespaces, boolean withMilliseconds) {
        if (time < 0) {
            time = -time;
        }
        if (time < 1000) {
            return "0s";
        }
        try {
            final StringBuilder text = new StringBuilder();
            final AtomicLong time_ = new AtomicLong(time);
            TimeUnit.forEach((timeUnit) -> {
                final int amount = (int) (time_.get() / timeUnit.getMillis());
                time_.addAndGet(-amount * timeUnit.getMillis());
                if (amount != 0) {
                    text.append(amount);
                    if (withWhitespaces) {
                        text.append(" ");
                    }
                    text.append((longNames ? timeUnit.getNameLong() + (amount != 1 ? "s" : "") : timeUnit.getNameShort()));
                    if (withWhitespaces) {
                        text.append(" ");
                    }
                }
            }, withMilliseconds);
            if (withWhitespaces && text.length() >= 1) {
                text.deleteCharAt(text.length() - 1);
            }
            return text.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return (time / 1000) + "s";
        }
    }

    public static final String stringToLettersAndDigitsOnly(String text) {
        return stringToLettersAndDigitsOnly(text, false);
    }

    public static final String stringToLettersAndDigitsOnly(String text, boolean invert, Character... exceptions) {
        if (text == null) {
            return null;
        }
        try {
            String out = "";
            for (int i = 0; i < text.length(); i++) {
                if (Character.isLetterOrDigit(text.charAt(i)) == !invert || contains(exceptions, text.charAt(i))) {
                    out += text.charAt(i);
                }
            }
            return out;
        } catch (Exception ex) {
            return null;
        }
    }

    public static final boolean isStringLettersAndDigitsOnly(String text) {
        if (text == null) {
            return false;
        }
        if (text.isEmpty()) {
            return true;
        }
        return text.equals(stringToLettersAndDigitsOnly(text));
    }

    public static final String stringToLettersOnly(String text) {
        return stringToLettersOnly(text, false);
    }

    public static final String stringToLettersOnly(String text, boolean invert, Character... exceptions) {
        if (text == null) {
            return null;
        }
        try {
            String out = "";
            for (int i = 0; i < text.length(); i++) {
                if (Character.isLetter(text.charAt(i)) == !invert || contains(exceptions, text.charAt(i))) {
                    out += text.charAt(i);
                }
            }
            return out;
        } catch (Exception ex) {
            return null;
        }
    }

    public static final boolean isStringLettersOnly(String text) {
        if (text == null) {
            return false;
        }
        if (text.isEmpty()) {
            return true;
        }
        return text.equals(stringToLettersOnly(text));
    }

    public static final String stringToDigitsOnly(String text) {
        return stringToDigitsOnly(text, false);
    }

    public static final String stringToDigitsOnly(String text, boolean invert, Character... exceptions) {
        if (text == null) {
            return null;
        }
        try {
            String out = "";
            for (int i = 0; i < text.length(); i++) {
                if (Character.isDigit(text.charAt(i)) == !invert || contains(exceptions, text.charAt(i))) {
                    out += text.charAt(i);
                }
            }
            return out;
        } catch (Exception ex) {
            return null;
        }
    }

    public static final boolean isStringDigitsOnly(String text) {
        if (text == null) {
            return false;
        }
        if (text.isEmpty()) {
            return true;
        }
        return text.equals(stringToDigitsOnly(text));
    }

    public static final String advancedFileToString(AdvancedFile file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            String out = "";
            final BufferedReader br = file.getReader();
            String line = null;
            while ((line = br.readLine()) != null) {
                out += line;
            }
            br.close();
            return out;
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }

    public static final HashMap<String, String> JSONToCookies(String text_json) {
        if (text_json == null) {
            return new HashMap<>();
        }
        try {
            final HashMap<String, String> cookies = new HashMap<>();
            final JSONArray array = new JSONArray(text_json);
            for (int i = 0; i < array.length(); i++) {
                final JSONObject object = array.getJSONObject(i);
                cookies.put(object.getString("name"), object.getString("value"));
            }
            return cookies;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new HashMap<>();
        }
    }

    public static final String cookiesToString(HashMap<String, String> cookies) {
        if (cookies == null) {
            return null;
        }
        if (cookies.isEmpty()) {
            return "";
        }
        try {
            final StringBuilder out = new StringBuilder();
            cookies.forEach((name, value) -> {
                out.append(name);
                out.append("=");
                out.append(value);
                out.append(";");
            });
            return out.substring(0, out.length() - ";".length());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final String embedMessageToString(MessageEmbed message) {
        if (message == null) {
            return "";
        }
        final StringBuilder temp = new StringBuilder();
        temp.append("[EmbedType: ").append(message.getType()).append("] ");
        if (message.getColor() != null) {
            temp.append("[Color: #").append(Integer.toHexString(message.getColor().getRGB()).substring(2)).append(", Alpha: ").append(Integer.toHexString(message.getColor().getRGB()).substring(0, 2)).append("] ");
        } else {
            temp.append("[Color: null] ");
        }
        temp.append("[Title: ").append(message.getTitle()).append("] ");
        temp.append("[Description: ").append(message.getDescription()).append("] ");
        if (message.getThumbnail() != null) {
            temp.append("[").append(String.format("Thumbnail: %s (W: %d, H: %d)", message.getThumbnail().getProxyUrl(), message.getThumbnail().getWidth(), message.getThumbnail().getHeight())).append("] ");
        }
        if (message.getImage() != null) {
            temp.append("[").append(String.format("Image: %s (W: %d, H: %d)", message.getImage().getProxyUrl(), message.getImage().getWidth(), message.getImage().getHeight())).append("] ");
        }
        if (message.getSiteProvider() != null) {
            temp.append("[SiteProvider: ").append(message.getSiteProvider().getName()).append(" (").append(message.getSiteProvider().getUrl()).append(")] ");
        }
        if (message.getUrl() != null) {
            temp.append("[Url: ").append(message.getUrl()).append(")] ");
        }
        if (message.getVideoInfo() != null) {
            temp.append("[").append(String.format("VideoInfo: %s (W: %d, H: %d)", message.getVideoInfo().getUrl(), message.getVideoInfo().getWidth(), message.getVideoInfo().getHeight())).append("] ");
        }
        temp.append("[").append(message.getFields().stream().map((field) -> String.format("[%s; %s; %b]", field.getName(), field.getValue(), field.isInline())).collect(Collectors.joining(", "))).append("] ");
        if (message.getFooter() != null) {
            temp.append("[Footer: ").append(message.getFooter().getText()).append(" (").append(message.getFooter().getProxyIconUrl()).append(")] ");
        }
        return temp.toString();
    }

    public static final String getUptimeMessage(MessageEvent event, Instant timestamp) {
        if (timestamp == null) {
            return null;
        }
        final long longest_uptime = Config.CONFIG.getLongestUptime();
        final long uptime = getUptime(timestamp);
        final Instant longest_uptime_start = Config.CONFIG.getLongestUptimeStart(uptime);
        final StringBuilder output = new StringBuilder();
        if (event != null) {
            output.append(event.getAuthor().getAsMention()).append(" ");
        }
        output.append("i have been online for ").append(getTimeAsString(uptime, true, true, false));
        final String from_to = String.format("From %s to %s", Standard.STANDARD_DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(longest_uptime_start, Standard.getZoneId())), Standard.STANDARD_DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(longest_uptime_start.plusMillis(longest_uptime), Standard.getZoneId())));
        if (uptime > longest_uptime) {
            output.append(String.format(" (This is %s ahead the longest uptime of %s (%s))", getTimeAsString(uptime - longest_uptime, true, true, false), getTimeAsString(longest_uptime, true, true, false), from_to));
        } else if (longest_uptime > 0) {
            output.append(String.format(" (This is %s behind the longest uptime of %s (%s))", getTimeAsString(longest_uptime - uptime, true, true, false), getTimeAsString(longest_uptime, true, true, false), from_to));
        }
        return output.toString();
    }

    public static final Duration getUptimeAsDuration(Instant timestamp) {
        if (timestamp == null) {
            return Duration.ZERO;
        }
        return Duration.between(Standard.getStarted(), timestamp);
    }

    public static final long getUptime(Instant timestamp) {
        if (timestamp == null) {
            return 0;
        }
        return getUptimeAsDuration(timestamp).toMillis();
    }

    public static final Pattern PATTERN_VOICECHANNEL = Pattern.compile("(.+)#(\\d+)");

    public static final VoiceChannel resolveVoiceChannel(Guild guild, String voiceChannel_string) {
        if (guild == null || voiceChannel_string == null || voiceChannel_string.isEmpty()) {
            return null;
        }
        VoiceChannel voiceChannel = null;
        try {
            voiceChannel = guild.getVoiceChannelById(voiceChannel_string);
        } catch (Exception ex) {
            voiceChannel = null;
        }
        if (voiceChannel == null) {
            int skip = 0;
            final Matcher matcher = PATTERN_VOICECHANNEL.matcher(voiceChannel_string);
            if (matcher.matches()) {
                try {
                    skip = Integer.parseInt(matcher.group(2)) - 1;
                    voiceChannel_string = matcher.group(1);
                } catch (Exception ex) {
                }
            }
            voiceChannel = guild.getVoiceChannelsByName(voiceChannel_string, true).stream().skip(skip).findFirst().orElse(null);
        }
        return voiceChannel;
    }

    public static final Random RANDOM = new Random();

    public static final long getRandomLong() {
        return RANDOM.nextLong();
    }

    public static final long getRandomLong(Long... nots) {
        long random = getRandomLong();
        while (contains(nots, random)) {
            random = getRandomLong();
        }
        return random;
    }

    public static final LocalDateTime stringToLocalDateTime(String text, String pattern) {
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception ex) {
            return null;
        }
    }

    public static final LocalDateTime stringToLocalDateTime(String text) {
        try {
            return LocalDateTime.parse(text, Standard.STANDARD_DATE_TIME_FORMATTER);
        } catch (Exception ex) {
            try {
                return LocalDateTime.parse(text, Standard.STANDARD_DATE_TIME_FILE_FORMATTER);
            } catch (Exception ex2) {
                try {
                    return Timestamp.valueOf(text).toLocalDateTime();
                } catch (Exception ex3) {
                    try {
                        return LocalDateTime.parse(text);
                    } catch (Exception ex4) {
                        try {
                            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (Exception ex5) {
                            return null;
                        }
                    }
                }
            }
        }
    }

    public static final Instant stringToInstant(String text) {
        try {
            return Instant.parse(text);
        } catch (Exception ex) {
            try {
                return Instant.ofEpochMilli(Long.parseLong(text));
            } catch (Exception ex2) {
                try {
                    return Timestamp.valueOf(text).toInstant();
                } catch (Exception ex3) {
                    return null;
                }
            }
        }
    }

    public static final List<AdvancedFile> getAndDownloadAttachmentsOfMessage(Message message) {
        return message.getAttachments().stream().map((attachment) -> new AbstractMap.SimpleEntry<AdvancedFile, Attachment>(Standard.getTempFile(attachment.getFileName()), attachment)).filter((image) -> image.getValue().download(image.getKey().toFile())).map((image) -> image.getKey()).collect(Collectors.toList());
    }

}
