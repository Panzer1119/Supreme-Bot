package de.panzercraft.bot.supreme.listeners;

import de.panzercraft.bot.supreme.commands.CommandHandler;
import de.panzercraft.bot.supreme.commands.CommandParser;
import de.panzercraft.bot.supreme.util.Standard;
import java.util.List;
import net.dv8tion.jda.core.entities.Message.Attachment;
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
        try {
            if (event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                return;
            }
            final String message = event.getMessage().getContent().trim();
            if (isCommand(message, event)) {
                CommandHandler.handleCommand(CommandParser.parser(message, event));
            } else {
                final List<Attachment> attachments = event.getMessage().getAttachments();
                if (attachments == null || attachments.isEmpty()) {
                    System.out.println(String.format("[%s] [%s] %s: %s", event.getGuild().getName(), event.getTextChannel().getName(), event.getAuthor().getName(), message));
                } else {
                    String text = "";
                    for (Attachment attachment : attachments) {
                        text += "\n";
                        if (attachment.isImage()) {
                            text += String.format("+IMAGE: \"%s\" (ID: %s) (PROXYURL: %s) (W: %d, H: %d)", attachment.getFileName(), attachment.getId(), attachment.getProxyUrl(), attachment.getWidth(), attachment.getHeight());
                        } else {
                            text += String.format("+FILE: \"%s\" (ID: %s) (URL: %s)", attachment.getFileName(), attachment.getId(), attachment.getUrl());
                        }
                    }
                    System.out.println(String.format("[%s] [%s] %s: %s%s", event.getGuild().getName(), event.getTextChannel().getName(), event.getAuthor().getName(), message, text));
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
    
    private final boolean isCommand(String message, MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            return false;
        }
        return message.startsWith(Standard.getCommandPrefixByGuild(event.getGuild()));
    }
    
}
