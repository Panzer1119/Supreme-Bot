package de.codemakers.bot.supreme.commands.arguments;

import de.codemakers.bot.supreme.util.Standard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
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
        final String[] other = consumeOther(argument, type, 1);
        if (other == null || other.length == 0) {
            return null;
        }
        return other[0];
    }

    public final String consumePrevious(Argument argument, ArgumentConsumeType type) {
        final String[] other = consumeOther(argument, type, -1);
        if (other == null || other.length == 0) {
            return null;
        }
        return other[0];
    }

    public final String[] consumeOther(Argument argument, ArgumentConsumeType type, int offset) {
        if (argument == null) {
            return new String[0];
        }
        if (offset == 0) {
            return new String[]{argument.getCompleteArgument(0, -1)};
        }
        final ArrayList<String> output = new ArrayList<>();
        int index = -1;
        while ((index = consume(argument, type, true, index + 1)) != -1) {
            if (type.isConsume()) {
                index--;
            }
            List<String> other = null;
            if (offset > 0) {
                other = arguments_content.subList(index + 1, Math.min(index + offset + 1, arguments_content.size()));
            } else if (offset < 0) {
                other = arguments_content.subList(Math.max(0, index + offset), index);
            }
            if (type.isAll()) {
                if (other != null) {
                    output.addAll(other);
                    other.clear();
                }
            } else if (other != null) {
                return other.toArray(new String[other.size()]);
            } else {
                return new String[0];
            }
        }
        return output.toArray(new String[output.size()]);
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
        final String temp = getRaw(index);
        if (temp == null) {
            return null;
        }
        if ((temp.startsWith("<@!") || temp.startsWith("<@")) && temp.endsWith(">")) {
            consumeRaw(index);
            return Standard.getUserById(temp.substring("<@".length() + (temp.startsWith("<@!") ? 1 : 0), temp.length() - ">".length()));
        }
        return null;
    }

    public final Member consumeMemberFirst() {
        return consumeMember(0);
    }

    public final Member consumeMemberLast() {
        return consumeMember(size() - 1);
    }

    public final Member consumeMember(int index) {
        if (guild == null) {
            return null;
        }
        final String temp = getRaw(index);
        if (temp == null) {
            return null;
        }
        if ((temp.startsWith("<@!") || temp.startsWith("<@")) && temp.endsWith(">")) {
            consumeRaw(index);
            return guild.getMemberById(temp.substring("<@".length() + (temp.startsWith("<@!") ? 1 : 0), temp.length() - ">".length()));
        }
        return null;
    }

    public final TextChannel consumeTextChannelFirst() {
        return consumeTextChannel(0);
    }

    public final TextChannel consumeTextChannelLast() {
        return consumeTextChannel(size() - 1);
    }

    public final TextChannel consumeTextChannel(int index) {
        if (guild == null) {
            return null;
        }
        final String temp = getRaw(index);
        if (temp == null) {
            return null;
        }
        if ((temp.startsWith("<@!") || temp.startsWith("<@")) && temp.endsWith(">")) {
            consumeRaw(index);
            return guild.getTextChannelById(temp.substring("<@".length() + (temp.startsWith("<@!") ? 1 : 0), temp.length() - ">".length()));
        }
        return null;
    }

    public final Role consumeRoleFirst() {
        return consumeRole(0);
    }

    public final Role consumeRoleLast() {
        return consumeRole(size() - 1);
    }

    public final Role consumeRole(int index) {
        if (guild == null) {
            return null;
        }
        final String temp = getRaw(index);
        if (temp == null) {
            return null;
        }
        if ((temp.startsWith("<@!") || temp.startsWith("<@")) && temp.endsWith(">")) {
            consumeRaw(index);
            return guild.getRoleById(temp.substring("<@".length() + (temp.startsWith("<@!") ? 1 : 0), temp.length() - ">".length()));
        }
        return null;
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
        final Iterator<String> arguments_raw_iterator = arguments_content.iterator();
        while (arguments_raw_iterator.hasNext()) {
            if (index < index_start) {
                index++;
                continue;
            }
            final String argument_raw = arguments_raw_iterator.next();
            if (argument_raw == null) {
                continue;
            }
            if (argument.takes(type, argument_raw)) {
                times_found++;
                if (type.isConsume()) {
                    arguments_raw_iterator.remove();
                    arguments_content_raw.remove(index);
                    index--;
                }
                if (returnIndex) {
                    return index;
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
        StringBuilder sb = new StringBuilder();
        arguments_content.stream().forEach((argument_raw) -> sb.append(argument_raw));
        return sb.toString().substring(1);
    }

}
