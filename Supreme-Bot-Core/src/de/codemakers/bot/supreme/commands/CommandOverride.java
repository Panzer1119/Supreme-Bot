package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * CommandOverride
 *
 * @author Panzer1119
 */
public class CommandOverride {

    private Command command;
    private Function<ArgumentList, ArgumentList> converter;

    public CommandOverride(Command command, ArgumentList association, String new_association) {
        this.command = command;
        String temp = association.toString();
        int braces = 0;
        for (char c : temp.toCharArray()) {
            if (c == '\\') {
                continue;
            }
            if (c == '{') {
                braces++;
            } else if (c == '}') {
                braces--;
            }
        }
        if (braces > 0) {
            throw new IllegalArgumentException("Existing association misses " + braces + " '}'");
        } else if (braces < 0) {
            throw new IllegalArgumentException("Existing association misses " + braces + " '{'");
        }
        final List<Text> text = new ArrayList<>();
        association.getContentRawArguments().forEach((argument) -> {
            if (argument.startsWith("{") && argument.endsWith("}")) {
                text.add(new Variable(argument.substring(1, argument.length() - 1)));
            } else {
                text.add(new Text(argument));
            }
        });
        converter = (arguments) -> {
            final ArgumentList arguments_new = new ArgumentList(arguments.getGuild(), arguments.getUser());
            return arguments_new;
        };
    }

}
