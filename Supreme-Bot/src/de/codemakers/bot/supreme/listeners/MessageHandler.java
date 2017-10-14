package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.commands.CommandParser;
import de.codemakers.bot.supreme.commands.CommandType;
import de.codemakers.bot.supreme.entities.DefaultMessageEvent;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.settings.Config;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import de.codemakers.bot.supreme.util.updater.Updater;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageEmbedEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * CommandListener
 *
 * @author Panzer1119
 */
public class MessageHandler extends ListenerAdapter {

    private static final boolean DEBUG = false;

    @Override
    public final void onMessageReceived(MessageReceivedEvent event_received) {
        if (event_received == null || event_received.getAuthor().getIdLong() == Standard.getSelfUser().getIdLong()) {
            return;
        }
        Updater.submit(() -> {
            try {
                final MessageEvent event = new DefaultMessageEvent(event_received.getJDA(), event_received.getResponseNumber(), event_received.getMessage());
                final String content_raw = event.getMessage().getRawContent().trim();
                final String content = event.getMessage().getContent().trim();
                final Guild guild = event.getGuild();
                final CommandType commandType = CommandType.getCommandType(content, content_raw, event);
                if (commandType.isCommand()) {
                    CommandHandler.handleCommand(CommandParser.parser(commandType, content, content_raw, event));
                } else if (content_raw.contains(Standard.getSelfUser().getAsMention()) || content_raw.contains(Standard.getSelfMemberByGuild(guild).getAsMention())) {
                    final String reaction = Config.CONFIG.getGuildReactionOnMention(guild.getIdLong());
                    if (reaction != null) {
                        event.getMessage().addReaction(reaction).queue();
                    }
                }
                final Object[] output = ListenerManager.fireListeners(MessageListener.class, CommandHandler.ADMIN_PREDICATE, new Object[]{event, MessageType.RECEIVED});
                String embed_messages = event.getMessage().getEmbeds().stream().map((messageEmbed) -> Util.embedMessageToString(messageEmbed)).collect(Collectors.joining(Standard.NEW_LINE_DISCORD));
                if (!embed_messages.isEmpty()) {
                    embed_messages = Standard.NEW_LINE_DISCORD + embed_messages;
                }
                final List<Attachment> attachments = event.getMessage().getAttachments();
                if (attachments == null || attachments.isEmpty()) {
                    System.out.println(String.format("[Used %d time%s] [%s] [%s] %s: %s%s", output.length, (output.length == 1 ? "" : "s"), (guild != null ? guild.getName() : "PRIVATE"), event.getMessageChannel().getName(), event.getAuthor().getName(), content, embed_messages));
                } else {
                    String text = "";
                    for (Attachment attachment : attachments) {
                        text += Standard.NEW_LINE_DISCORD;
                        if (attachment.isImage()) {
                            text += String.format("IMAGE: \"%s\" (ID: %s) (PROXYURL: %s) (W: %d, H: %d)", attachment.getFileName(), attachment.getId(), attachment.getProxyUrl(), attachment.getWidth(), attachment.getHeight());
                        } else {
                            text += String.format("FILE: \"%s\" (ID: %s) (URL: %s)", attachment.getFileName(), attachment.getId(), attachment.getUrl());
                        }
                    }
                    System.out.println(String.format("[Used %d time%s] [%s] [%s] %s: %s%s%s", output.length, (output.length == 1 ? "" : "s"), event.getGuild().getName(), event.getMessageChannel().getName(), event.getAuthor().getName(), content, text, embed_messages));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public final void onMessageUpdate(MessageUpdateEvent event) {
        final Object[] output = ListenerManager.fireListeners(MessageListener.class, CommandHandler.ADMIN_PREDICATE, new Object[]{event, MessageType.UPDATED});
        if (DEBUG && output.length > 0) {
            System.out.println(String.format("%d plugin%s used this message event: %s", output.length, (output.length == 1 ? "" : "s"), event));
        }
    }

    @Override
    public final void onMessageDelete(MessageDeleteEvent event) {
        final Object[] output = ListenerManager.fireListeners(MessageListener.class, CommandHandler.ADMIN_PREDICATE, new Object[]{event, MessageType.DELETED});
        if (DEBUG && output.length > 0) {
            System.out.println(String.format("%d plugin%s used this message event: %s", output.length, (output.length == 1 ? "" : "s"), event));
        }
    }

    @Override
    public final void onMessageBulkDelete(MessageBulkDeleteEvent event) {
        final Object[] output = ListenerManager.fireListeners(MessageListener.class, CommandHandler.ADMIN_PREDICATE, new Object[]{event, MessageType.BULK_DELETED});
        if (DEBUG && output.length > 0) {
            System.out.println(String.format("%d plugin%s used this message event: %s", output.length, (output.length == 1 ? "" : "s"), event));
        }
    }

    @Override
    public final void onMessageEmbed(MessageEmbedEvent event) {
        final Object[] output = ListenerManager.fireListeners(MessageListener.class, CommandHandler.ADMIN_PREDICATE, new Object[]{event, MessageType.EMBEDED});
        if (DEBUG && output.length > 0) {
            System.out.println(String.format("%d plugin%s used this message event: %s", output.length, (output.length == 1 ? "" : "s"), event));
        }
    }

    @Override
    public final void onMessageReactionAdd(MessageReactionAddEvent event) {
        final Object[] output = ListenerManager.fireListeners(MessageListener.class, CommandHandler.ADMIN_PREDICATE, new Object[]{event, MessageType.REACTION_ADDED});
        if (DEBUG && output.length > 0) {
            System.out.println(String.format("%d plugin%s used this message event: %s", output.length, (output.length == 1 ? "" : "s"), event));
        }
    }

    @Override
    public final void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        final Object[] output = ListenerManager.fireListeners(MessageListener.class, CommandHandler.ADMIN_PREDICATE, new Object[]{event, MessageType.REACTION_REMOVED});
        if (DEBUG && output.length > 0) {
            System.out.println(String.format("%d plugin%s used this message event: %s", output.length, (output.length == 1 ? "" : "s"), event));
        }
    }

    @Override
    public final void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event) {
        final Object[] output = ListenerManager.fireListeners(MessageListener.class, CommandHandler.ADMIN_PREDICATE, new Object[]{event, MessageType.REACTION_REMOVED_ALL});
        if (DEBUG && output.length > 0) {
            System.out.println(String.format("%d plugin%s used this message event: %s", output.length, (output.length == 1 ? "" : "s"), event));
        }
    }

}
