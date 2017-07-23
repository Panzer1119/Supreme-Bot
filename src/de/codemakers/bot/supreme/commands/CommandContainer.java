package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.arguments.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;

/**
 * CommandContainer
 *
 * @author Panzer1119
 */
public class CommandContainer {

    public final String raw;
    public final String beheaded;
    public final String[] splitBeheaded;
    public final Invoker invoker;
    public final ArgumentList arguments;
    public final MessageEvent event;

    public CommandContainer(String raw, String beheaded, String[] splitBeheaded, Invoker invoker, ArgumentList arguments, MessageEvent event) {
        this.raw = raw;
        this.beheaded = beheaded;
        this.splitBeheaded = splitBeheaded;
        this.invoker = invoker;
        this.arguments = arguments;
        this.event = event;
    }

}
