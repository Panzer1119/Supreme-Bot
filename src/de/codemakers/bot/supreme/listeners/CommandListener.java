package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.commands.CommandParser;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Standard;
import java.util.List;
import net.dv8tion.jda.core.entities.Guild;
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
    public final void onMessageReceived(MessageReceivedEvent event_received) { //FIXME Bug mit privaten Nachrichten
        if (event_received == null) {
            return;
        }
        final MessageEvent event = new MessageEvent(event_received.getJDA(), event_received.getResponseNumber(), event_received.getMessage());
        try {
            if (event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                return;
            }
            final String message = event.getMessage().getContent().trim();
            final Guild guild = event.getGuild();
            if (isCommand(message, event)) {
                CommandHandler.handleCommand(CommandParser.parser(message, event));
            } else {
                final List<Attachment> attachments = event.getMessage().getAttachments();
                if (attachments == null || attachments.isEmpty()) {
                    System.out.println(String.format("[%s] [%s] %s: %s", (guild != null ? guild.getName() : "PRIVATE"), event.getMessageChannel().getName(), event.getAuthor().getName(), message));
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
                    System.out.println(String.format("[%s] [%s] %s: %s%s", event.getGuild().getName(), event.getMessageChannel().getName(), event.getAuthor().getName(), message, text));
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }
    
    private final boolean isCommand(String message, MessageEvent event) {
        if (message == null || event == null || event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            return false;
        }
        return message.startsWith(Standard.getCommandPrefixByGuild(event.getGuild()));
    }
    
}
