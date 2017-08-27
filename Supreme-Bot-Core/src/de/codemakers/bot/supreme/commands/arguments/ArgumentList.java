package de.codemakers.bot.supreme.commands.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * ArgumentList
 *
 * @author Panzer1119
 */
public class ArgumentList {

    private final ArrayList<String> arguments_raw = new ArrayList<>();

    public ArgumentList(String... arguments) {
        setRawArguments(arguments);
    }

    public final ArgumentList setRawArguments(String... arguments) {
        clearRawArguments();
        addRawArguments(arguments);
        return this;
    }

    public final ArgumentList addRawArguments(String... arguments) {
        if (arguments != null && arguments.length > 0) {
            arguments_raw.addAll(Arrays.asList(arguments));
        }
        return this;
    }

    public final ArgumentList clearRawArguments() {
        arguments_raw.clear();
        return this;
    }

    public final ArrayList<String> getRawArguments() {
        return arguments_raw;
    }

    public final String[] getRawArgumentsAsArray() {
        return arguments_raw.toArray(new String[arguments_raw.size()]);
    }

    public final boolean hasArguments() {
        return size() > 0;
    }

    public final boolean isEmpty() {
        return size() == 0;
    }

    public final int size() {
        return arguments_raw.size();
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
        return get(arguments_raw.size() - 1);
    }

    public final String get(int index) {
        if (!isSize(index + 1, -1) || index < 0) {
            return null;
        }
        return arguments_raw.get(index);
    }

    public final Stream<String> stream() {
        return arguments_raw.stream();
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
                other = arguments_raw.subList(index + 1, Math.min(index + offset + 1, arguments_raw.size()));
            } else if (offset < 0) {
                other = arguments_raw.subList(Math.max(0, index + offset), index);
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
        return consume(arguments_raw.size() - 1);
    }

    public final String consume(int index) {
        if (!isSize(index + 1, -1) || index < 0) {
            return null;
        }
        final String temp = arguments_raw.get(index);
        arguments_raw.remove(index);
        return temp;
    }

    public final boolean consumeFirst(Argument argument, ArgumentConsumeType type) {
        return consume(argument, type, 0);
    }

    public final boolean consumeLast(Argument argument, ArgumentConsumeType type) {
        return consume(argument, type, arguments_raw.size() - 1);
    }

    public final boolean consume(Argument argument, ArgumentConsumeType type, int index) {
        if (argument == null || arguments_raw.isEmpty() || index < 0 || index >= arguments_raw.size()) {
            return false;
        }
        final String argument_raw = arguments_raw.get(index);
        if (argument_raw == null) {
            return false;
        }
        if (argument.takes(type, argument_raw)) {
            if (type.isConsume()) {
                arguments_raw.remove(index);
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
        if (argument == null || arguments_raw.isEmpty() || index_start < 0 || index_start >= arguments_raw.size()) {
            if (returnIndex) {
                return -1;
            } else {
                return 0;
            }
        }
        int times_found = 0;
        int index = 0;
        final Iterator<String> arguments_raw_iterator = arguments_raw.iterator();
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
        arguments_raw.stream().forEach((argument_raw) -> sb.append(" " + argument_raw));
        return sb.toString().substring(1);
    }

}
