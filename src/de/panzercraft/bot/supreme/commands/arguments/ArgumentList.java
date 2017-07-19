package de.panzercraft.bot.supreme.commands.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

    public final boolean isConsumedAll(Argument argument) {
        return isConsumed(argument, ArgumentConsumeType.ALL_IGNORE_CASE);
    }

    public final boolean isConsumed(Argument argument) {
        return isConsumed(argument, ArgumentConsumeType.FIRST_IGNORE_CASE);
    }
    
    public final boolean isConsumed(Argument argument, ArgumentConsumeType type) {
        return consume(argument, type) > 0;
    }

    public final int consumeAll(Argument argument) {
        return consume(argument, ArgumentConsumeType.ALL_IGNORE_CASE);
    }

    public final int consume(Argument argument) {
        return consume(argument, ArgumentConsumeType.FIRST_IGNORE_CASE);
    }

    public final int consume(Argument argument, ArgumentConsumeType type) {
        if (argument == null || arguments_raw.isEmpty()) {
            return 0;
        }
        int times_found = 0;
        final Iterator<String> arguments_raw_iterator = arguments_raw.iterator();
        while (arguments_raw_iterator.hasNext()) {
            final String argument_raw = arguments_raw_iterator.next();
            if (argument_raw == null) {
                continue;
            }
            if ((type.isIgnoringCase() && (argument_raw.equalsIgnoreCase(argument.getArgument()) || argument_raw.equalsIgnoreCase(argument.getCompleteArgument()))) || (!type.isIgnoringCase() && (argument_raw.equals(argument.getArgument()) || argument_raw.equals(argument.getCompleteArgument())))) {
                times_found++;
                arguments_raw_iterator.remove();
                if (!type.isConsumingAll()) {
                    return times_found;
                }
            }
        }
        return times_found;
    }

}
