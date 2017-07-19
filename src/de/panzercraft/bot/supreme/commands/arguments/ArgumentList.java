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
    
    public final String get(int index) {
        return arguments_raw.get(index);
    }
    
    public final Stream<String> stream() {
        return arguments_raw.stream();
    }
    
    public final boolean consume(Argument argument) {
        return consume(argument, true) > 0;
    }
    
    public final int consume(Argument argument, boolean all) {
        return consume(argument, true, all);
    }
    
    public final int consume(Argument argument, boolean ignoreCase, boolean all) {
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
            if ((ignoreCase && (argument_raw.equalsIgnoreCase(argument.getArgument()) || argument_raw.equalsIgnoreCase(argument.getCompleteArgument()))) || (!ignoreCase && (argument_raw.equals(argument.getArgument()) || argument_raw.equals(argument.getCompleteArgument())))) {
                times_found++;
                arguments_raw_iterator.remove();
            }
        }
        return times_found;
    }

}
