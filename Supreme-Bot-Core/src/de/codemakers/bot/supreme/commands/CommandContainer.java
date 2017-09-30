package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;

/**
 * CommandContainer
 *
 * @author Panzer1119
 */
public class CommandContainer {

    public final String content;
    public final String content_raw;
    public final String beheaded;
    public final String beheaded_raw;
    public final String[] splitBeheaded;
    public final String[] splitBeheaded_raw;
    public final Invoker invoker;
    public final ArgumentList arguments;
    public final MessageEvent event;

    public CommandContainer(String content, String content_raw, String beheaded, String beheaded_raw, String[] splitBeheaded, String[] splitBeheaded_raw, Invoker invoker, ArgumentList arguments, MessageEvent event) {
        this.content = content;
        this.content_raw = content_raw;
        this.beheaded = beheaded;
        this.beheaded_raw = beheaded_raw;
        this.splitBeheaded = splitBeheaded;
        this.splitBeheaded_raw = splitBeheaded_raw;
        this.invoker = invoker;
        this.arguments = arguments;
        this.event = event;
    }

}
