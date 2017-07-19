package de.panzercraft.bot.supreme.listeners;

import de.panzercraft.bot.supreme.commands.CommandHandler;
import de.panzercraft.bot.supreme.commands.CommandParser;
import de.panzercraft.bot.supreme.util.Standard;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * CommandListener
 * 
 * @author Panzer1119
 */
public class CommandListener extends ListenerAdapter {
    
    @Override
    public final void onMessageReceived(MessageReceivedEvent event) {
        final String message = event.getMessage().getContent().trim();
        if (isCommand(message, event)) {
            CommandHandler.handleCommand(CommandParser.parser(message, event));
        }
    }
    
    private final boolean isCommand(String message, MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            return false;
        }
        return message.startsWith(Standard.getCommandPrefixByGuild(event.getGuild()));
    }
    
}
