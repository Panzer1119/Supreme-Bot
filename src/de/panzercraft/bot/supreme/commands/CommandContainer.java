package de.panzercraft.bot.supreme.commands;

import de.panzercraft.bot.supreme.commands.arguments.ArgumentList;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
    public final MessageReceivedEvent event;

    public CommandContainer(String raw, String beheaded, String[] splitBeheaded, String invoke, ArgumentList arguments, MessageReceivedEvent event) {
        this.raw = raw;
        this.beheaded = beheaded;
        this.splitBeheaded = splitBeheaded;
        this.invoke = invoke;
        this.arguments = arguments;
        this.event = event;
    }

}
