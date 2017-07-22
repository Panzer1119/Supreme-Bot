package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Standard;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * CommandParser
 *
 * @author Panzer1119
 */
public class CommandParser {

    public static final CommandContainer parser(String raw, MessageEvent event) {
        final String beheaded = raw.replaceFirst(Standard.getCommandPrefixByGuild(event.getGuild()), "");
        final String beheaded_corrected = beheaded.replace("\\", "\\\\");
        final String[] beheaded_split = getArguments(beheaded_corrected);
        final String invoke = beheaded_split[0];
        final ArrayList<String> split = new ArrayList<>(Arrays.asList(beheaded_split));
        final String[] args = split.subList(1, split.size()).toArray(new String[split.size() - 1]);
        final ArgumentList arguments = new ArgumentList(args);
        split.clear();
        return new CommandContainer(raw, beheaded_corrected, beheaded_split, invoke, arguments, event);
    }
    
    public static final String[] getArguments(String arguments) {
        boolean isArg = false;
        String temp = "";
        final ArrayList<String> args = new ArrayList<>();
        for (int i = 0; i < arguments.length(); i++) {
            char c = arguments.charAt(i);
            String c_string = "" + c;
            switch (c_string) {
                case Standard.COMMAND_ESCAPE_STRING:
                    if (isArg) {
                        i++;
                        if (arguments.length() > i) {
                            char c_2 = arguments.charAt(i);
                            temp += c_2;
                        }
                    } else {
                        temp += Standard.COMMAND_ESCAPE_STRING;
                    }
                    break;
                case Standard.COMMAND_ESCAPE_SPACE_STRING:
                    isArg = !isArg;
                    break;
                default:
                    if ((!c_string.equals(Standard.COMMAND_DELIMITER_STRING) || isArg)) {
                        temp += c;
                    } else {
                        if (!temp.isEmpty()) {
                            args.add(temp);
                        }
                        temp = "";
                    }
                    break;
            }
        }
        if (!temp.isEmpty()) {
            args.add(temp);
        }
        String[] args_s = args.toArray(new String[args.size()]);
        return args_s;
    }

}
