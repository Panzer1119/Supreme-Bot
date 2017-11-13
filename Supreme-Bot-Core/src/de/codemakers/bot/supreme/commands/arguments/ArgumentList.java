package de.codemakers.bot.supreme.commands.arguments;

import de.codemakers.bot.supreme.util.Standard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * ArgumentList
 *
 * @author Panzer1119
 */
public class ArgumentList {

    public static final Pattern PATTERN_MARKDOWN_ALL = Pattern.compile("<@(?:!?|#|&)(\\d+)>");
    public static final Pattern PATTERN_MARKDOWN_USER = Pattern.compile("<@(\\d+)>");
    public static final Pattern PATTERN_MARKDOWN_USER_RENAMED = Pattern.compile("<@!(\\d+)>");
    public static final Pattern PATTERN_MARKDOWN_USER_GENERAL = Pattern.compile("<@!?(\\d+)>");
    public static final Pattern PATTERN_MARKDOWN_GUILD_COMPLETE = Pattern.compile("<(.*)#(\\d+)>");
    public static final Pattern PATTERN_MARKDOWN_CHANNEL = Pattern.compile("<#(\\d+)>");
    public static final Pattern PATTERN_MARKDOWN_ROLE = Pattern.compile("<@&(\\d+)>");
    public static final Pattern PATTERN_MARKDOWN_CUSTOM_EMOJI = Pattern.compile("<:(\\w+):(\\d+)>");
    /**
     * https://regexr.com/3h26u
     */
    public static final Pattern PATTERN_MARKDOWN_UNIQUE_USERNAME = Pattern.compile("<(.*)#(\\d{4})>");
    /**
     * https://regexr.com/3h26r
     */
    public static final Pattern PATTERN_MARKDOWN_UNIQUE_USERNAME_WITH_ID = Pattern.compile("<(.*)#(\\d{4}):(\\d+)>");

    private final ArrayList<String> arguments_content_raw = new ArrayList<>();
    private final ArrayList<String> arguments_content = new ArrayList<>();
    private final Guild guild;

    public ArgumentList(Guild guild) {
        this.guild = guild;
    }

    public final ArgumentList addArguments(String... arguments) {
        addContentArguments(arguments);
        addContentRawArguments(arguments);
        return this;
    }

    public final ArgumentList setContentRawArguments(String... arguments) {
        addContentRawArguments(arguments);
        return this;
    }

    public final ArgumentList addContentRawArguments(String... arguments) {
        if (arguments != null && arguments.length > 0) {
            arguments_content_raw.addAll(Arrays.asList(arguments));
        }
        return this;
    }

    public final ArrayList<String> getContentRawArguments() {
        return arguments_content_raw;
    }

    public final String[] getContentRawArgumentsAsArray() {
        return arguments_content_raw.toArray(new String[arguments_content_raw.size()]);
    }

    public final ArgumentList setContentArguments(String... arguments) {
        addContentArguments(arguments);
        return this;
    }

    public final ArgumentList addContentArguments(String... arguments) {
        if (arguments != null && arguments.length > 0) {
            arguments_content.addAll(Arrays.asList(arguments));
        }
        return this;
    }

    public final ArrayList<String> getContentArguments() {
        return arguments_content;
    }

    public final String[] getContentArgumentsAsArray() {
        return arguments_content.toArray(new String[arguments_content.size()]);
    }

    public final ArgumentList clearArguments() {
        arguments_content_raw.clear();
        arguments_content.clear();
        return this;
    }

    public final boolean hasArguments() {
        return size() > 0;
    }

    public final boolean isEmpty() {
        return size() == 0;
    }

    public final int size() {
        return arguments_content.size();
    }

    public final boolean isSize(int size) {
        return size() == size;
    }

    public final boolean isSize(int size_min, int size_max) {
        return (size_min != -1 ? (size() >= size_min) : true) && (size_max != -1 ? (size() <= size_max) : true);
    }

    public final String getFirst() {
        return get(0);
    }

    public final String getLast() {
        return get(size() - 1);
    }

    public final String get(int index) {
        if (!isSize(index + 1, -1) || index < 0) {
            return null;
        }
        return arguments_content.get(index);
    }

    public final String getRawFirst() {
        return getRaw(0);
    }

    public final String getRawLast() {
        return getRaw(size() - 1);
    }

    public final String getRaw(int index) {
        if (!isSize(index + 1, -1) || index < 0) {
            return null;
        }
        return arguments_content_raw.get(index);
    }

    public final Stream<String> stream() {
        return arguments_content.stream();
    }

    public final Stream<String> streamRaw() {
        return arguments_content_raw.stream();
    }

    public final String consumeNext(Argument argument, ArgumentConsumeType type) {
        return consumeNext(argument, type, type);
    }

    public final String consumeNext(Argument argument, ArgumentConsumeType type_argument, ArgumentConsumeType type_other) {
        final String[] other = consumeOther(argument, type_argument, type_other, 1);
        if (other == null || other.length == 0) {
            return null;
        }
        return other[0];
    }

    public final String consumePrevious(Argument argument, ArgumentConsumeType type) {
        return consumePrevious(argument, type, type);
    }

    public final String consumePrevious(Argument argument, ArgumentConsumeType type_argument, ArgumentConsumeType type_other) {
        final String[] other = consumeOther(argument, type_argument, type_other, -1);
        if (other == null || other.length == 0) {
            return null;
        }
        return other[0];
    }

    public final String[] consumeOther(Argument argument, ArgumentConsumeType type, int offset) {
        return consumeOther(argument, type, type, offset);
    }

    public final String[] consumeOther(Argument argument, ArgumentConsumeType type_argument, ArgumentConsumeType type_other, int offset) {
        if (argument == null) {
            return new String[0];
        }
        if (offset == 0) {
            return new String[]{argument.getCompleteArgument(0, -1)};
        }
        final ArrayList<String> output = new ArrayList<>();
        int index = -1;
        while ((index = consume(argument, type_argument, true, index + 1)) != -1) {
            if (type_argument.isConsume()) {
                index--;
            }
            List<String> other = new ArrayList<>();
            if (offset > 0) {
                for (int i = index + 1; i < Math.min(index + offset + 1, size()); i++) {
                    if (type_other.isConsume()) {
                        other.add(consume(i));
                    } else {
                        other.add(get(i));
                    }
                }
            } else if (offset < 0) {
                for (int i = Math.max(0, index + offset); i < index; i++) {
                    if (type_other.isConsume()) {
                        other.add(consume(i));
                        index--;
                    } else {
                        other.add(get(i));
                    }
                }
            }
            if (type_argument.isAll()) {
                output.addAll(other);
                other.clear();
            } else {
                return other.toArray(new String[other.size()]);
            }
        }
        return output.toArray(new String[output.size()]);
    }

    public final boolean isMarkdownFirst() {
        return isMarkdown(0);
    }

    public final boolean isMarkdownLast() {
        return isMarkdown(size() - 1);
    }

    public final boolean isMarkdown(int index) {
        final String temp = getRaw(index);
        if (temp == null) {
            return false;
        }
        Matcher matcher = PATTERN_MARKDOWN_ALL.matcher(temp);
        return matcher.matches();
    }

    public final String consumeFirst() {
        return consume(0);
    }

    public final String consumeLast() {
        return consume(size() - 1);
    }

    public final String consume(int index) {
        if (!isSize(index + 1, -1) || index < 0) {
            return null;
        }
        final String temp = arguments_content.get(index);
        arguments_content.remove(index);
        arguments_content_raw.remove(index);
        return temp;
    }

    public final String consumeRawFirst() {
        return consumeRaw(0);
    }

    public final String consumeRawLast() {
        return consumeRaw(size() - 1);
    }

    public final String consumeRaw(int index) {
        if (!isSize(index + 1, -1) || index < 0) {
            return null;
        }
        final String temp = arguments_content_raw.get(index);
        arguments_content_raw.remove(index);
        arguments_content.remove(index);
        return temp;
    }

    public final User consumeUserFirst() {
        return consumeUser(0);
    }

    public final User consumeUserLast() {
        return consumeUser(size() - 1);
    }

    public final User consumeUser(int index) {
        return getUser(index, true);
    }

    public final User getUserFirst() {
        return getUser(0);
    }

    public final User getUserLast() {
        return getUser(size() - 1);
    }

    public final User getUser(int index) {
        return getUser(index, false);
    }

    public final User getUser(int index, boolean consume) {
        final String temp = getRaw(index);
        if (temp == null) {
            return null;
        }
        Matcher matcher = PATTERN_MARKDOWN_USER.matcher(temp);
        if (!matcher.matches()) {
            matcher = PATTERN_MARKDOWN_USER_RENAMED.matcher(temp);
            if (!matcher.matches()) {
                return null;
            }
        }
        if (consume) {
            consumeRaw(index);
        }
        return Standard.getUserById(matcher.group(1));
    }

    public final Member consumeMemberFirst() {
        return consumeMember(0);
    }

    public final Member consumeMemberLast() {
        return consumeMember(size() - 1);
    }

    public final Member consumeMember(int index) {
        return getMember(index, true);
    }

    public final Member getMemberFirst() {
        return getMember(0);
    }

    public final Member getMemberLast() {
        return getMember(size() - 1);
    }

    public final Member getMember(int index) {
        return getMember(index, false);
    }

    public final Member getMember(int index, boolean consume) {
        if (guild == null) {
            return null;
        }
        final String temp = getRaw(index);
        if (temp == null) {
            return null;
        }
        final User user = getUser(index, consume);
        if (user == null) {
            return null;
        }
        return guild.getMember(user);
    }

    public final TextChannel consumeTextChannelFirst() {
        return consumeTextChannel(0);
    }

    public final TextChannel consumeTextChannelLast() {
        return consumeTextChannel(size() - 1);
    }

    public final TextChannel consumeTextChannel(int index) {
        return getTextChannel(index, true);
    }

    public final TextChannel getTextChannelFirst() {
        return getTextChannel(0);
    }

    public final TextChannel getTextChannelLast() {
        return getTextChannel(size() - 1);
    }

    public final TextChannel getTextChannel(int index) {
        return getTextChannel(index, false);
    }

    public final TextChannel getTextChannel(int index, boolean consume) {
        if (guild == null) {
            return null;
        }
        final String temp = getRaw(index);
        if (temp == null) {
            return null;
        }
        final Matcher matcher = PATTERN_MARKDOWN_CHANNEL.matcher(temp);
        if (!matcher.matches()) {
            return null;
        }
        if (consume) {
            consumeRaw(index);
        }
        return guild.getTextChannelById(matcher.group(1));
    }

    public final Role consumeRoleFirst() {
        return consumeRole(0);
    }

    public final Role consumeRoleLast() {
        return consumeRole(size() - 1);
    }

    public final Role consumeRole(int index) {
        return getRole(index, true);
    }

    public final Role getRoleFirst() {
        return getRole(0);
    }

    public final Role getRoleLast() {
        return getRole(size() - 1);
    }

    public final Role getRole(int index) {
        return getRole(index, false);
    }

    public final Role getRole(int index, boolean consume) {
        if (guild == null) {
            return null;
        }
        final String temp = getRaw(index);
        if (temp == null) {
            return null;
        }
        final Matcher matcher = PATTERN_MARKDOWN_ROLE.matcher(temp);
        if (!matcher.matches()) {
            return null;
        }
        if (consume) {
            consumeRaw(index);
        }
        return guild.getRoleById(matcher.group(1));
    }

    public final Emote consumeEmoteFirst() {
        return consumeEmote(0);
    }

    public final Emote consumeEmoteLast() {
        return consumeEmote(size() - 1);
    }

    public final Emote consumeEmote(int index) {
        return getEmote(index, true);
    }

    public final Emote getEmoteFirst() {
        return getEmote(0);
    }

    public final Emote getEmoteLast() {
        return getEmote(size() - 1);
    }

    public final Emote getEmote(int index) {
        return getEmote(index, false);
    }

    public final Emote getEmote(int index, boolean consume) {
        if (guild == null) {
            return null;
        }
        final String temp = getRaw(index);
        if (temp == null) {
            return null;
        }
        final Matcher matcher = PATTERN_MARKDOWN_CUSTOM_EMOJI.matcher(temp);
        if (!matcher.matches()) {
            return null;
        }
        if (consume) {
            consumeRaw(index);
        }
        return guild.getEmoteById(matcher.group(1));
    }

    public final boolean consumeFirst(Argument argument, ArgumentConsumeType type) {
        return consume(argument, type, 0);
    }

    public final boolean consumeLast(Argument argument, ArgumentConsumeType type) {
        return consume(argument, type, size() - 1);
    }

    public final boolean consume(Argument argument, ArgumentConsumeType type, int index) {
        if (argument == null || isEmpty() || index < 0 || index >= size()) {
            return false;
        }
        final String argument_raw = arguments_content.get(index);
        if (argument_raw == null) {
            return false;
        }
        if (argument.takes(type, argument_raw)) {
            if (type.isConsume()) {
                arguments_content.remove(index);
                arguments_content_raw.remove(index);
            }
            if (type.isAll()) {
                return consume(argument, type, index);
            }
            return true;
        } else {
            return false;
        }
    }

    public final boolean isConsumed(Argument argument, ArgumentConsumeType type) {
        return consume(argument, type, false) > 0;
    }

    public final int consume(Argument argument, ArgumentConsumeType type, boolean returnIndex) {
        return consume(argument, type, returnIndex, 0);
    }

    public final int consume(Argument argument, ArgumentConsumeType type, boolean returnIndex, int index_start) {
        if (argument == null || isEmpty() || index_start < 0 || index_start >= size()) {
            if (returnIndex) {
                return -1;
            } else {
                return 0;
            }
        }
        int times_found = 0;
        int index = 0;
        final Iterator<String> arguments_iterator = arguments_content.iterator();
        while (arguments_iterator.hasNext()) {
            if (index < index_start) {
                index++;
                continue;
            }
            final String argument_raw_ = arguments_iterator.next();
            if (argument_raw_ == null) {
                continue;
            }
            if (argument.takes(type, argument_raw_)) {
                times_found++;
                if (type.isConsume()) {
                    arguments_iterator.remove();
                    arguments_content_raw.remove(index);
                    index--;
                }
                if (returnIndex) {
                    return index + (type.isConsume() ? 1 : 0);
                } else if (!type.isAll()) {
                    return times_found;
                }
            }
            index++;
        }
        return (returnIndex ? -1 : times_found);
    }

    @Override
    public String toString() {
        return arguments_content.stream().collect(Collectors.joining(" "));
    }

}
