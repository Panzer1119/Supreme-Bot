package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * CommandParser
 *
 * @author Panzer1119
 */
public class CommandParser {

    public static final CommandContainer parser(String content, String content_raw, MessageEvent event) {
        final String commandPrefix = Standard.getCommandPrefixByGuild(event.getGuild());
        final String beheaded = content.replaceFirst(commandPrefix, "");
        final String beheaded_raw = content_raw.replaceFirst(commandPrefix, "");
        final String beheaded_corrected = beheaded.replace("\\", "\\\\");
        final String beheaded_corrected_raw = beheaded_raw.replace("\\", "\\\\");
        final String[] beheaded_split = getArguments(beheaded_corrected);
        final String[] beheaded_split_raw = getArguments(beheaded_corrected_raw);
        Invoker invoker = Invoker.getInvokerByInvokerString(beheaded_split[0]);
        if (invoker == null) {
            invoker = new Invoker(beheaded_split[0]);
        }
        final ArrayList<String> split = new ArrayList<>(Arrays.asList(beheaded_split));
        final ArrayList<String> split_raw = new ArrayList<>(Arrays.asList(beheaded_split_raw));
        final String[] args = split.subList(1, split.size()).toArray(new String[split.size() - 1]);
        final String[] args_raw = split_raw.subList(1, split_raw.size()).toArray(new String[split_raw.size() - 1]);
        final ArgumentList arguments = new ArgumentList(event.getGuild());
        arguments.setContentArguments(args);
        arguments.setContentRawArguments(args_raw);
        split.clear();
        split_raw.clear();
        return new CommandContainer(content, content_raw, beheaded_corrected, beheaded_corrected_raw, beheaded_split, beheaded_split_raw, invoker, arguments, event);
    }

    public static final String[] getArguments(String arguments) {
        if (arguments == null || arguments.isEmpty()) {
            return new String[0];
        }
        final boolean isEscaping = Util.stringContains(arguments, Standard.COMMAND_ESCAPE_SPACE_STRINGS);
        boolean isArg = false;
        String temp = "";
        final ArrayList<String> args = new ArrayList<>();
        for (int i = 0; i < arguments.length(); i++) {
            char c = arguments.charAt(i);
            String c_string = "" + c;
            if (Util.stringEquals(c_string, Standard.COMMAND_ESCAPE_SPACE_STRINGS)) {
                isArg = !isArg;
                continue;
            }
            switch (c_string) {
                case Standard.COMMAND_ESCAPE_STRING:
                    if (isEscaping || isArg) {
                        i++;
                        if (arguments.length() > i) {
                            char c_2 = arguments.charAt(i);
                            temp += c_2;
                        }
                    } else {
                        temp += Standard.COMMAND_ESCAPE_STRING;
                    }
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
