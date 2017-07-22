package de.panzercraft.bot.supreme.commands;

import de.panzercraft.bot.supreme.commands.arguments.ArgumentList;
import de.panzercraft.bot.supreme.entities.MessageEvent;

/**
 * CommandContainer
 *
 * @author Panzer1119
 */
public class CommandContainer {

    public final String raw;
    public final String beheaded;
    public final String[] splitBeheaded;
    public final String invoke;
    public final ArgumentList arguments;
    public final MessageEvent event;

    public CommandContainer(String raw, String beheaded, String[] splitBeheaded, String invoke, ArgumentList arguments, MessageEvent event) {
        this.raw = raw;
        this.beheaded = beheaded;
        this.splitBeheaded = splitBeheaded;
        this.invoke = invoke;
        this.arguments = arguments;
        this.event = event;
    }

}
