package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.entities.MessageEvent;
import net.dv8tion.jda.core.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageEmbedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;

/**
 * MessageListener
 *
 * @author Panzer1119
 */
public interface MessageListener extends Listener {

    @Override
    public default Object fired(Object... data) {
        final MessageType messageType = (MessageType) data[1];
        switch (messageType) {
            case RECEIVED:
                return onMessageReceived((MessageEvent) data[0]);
            case UPDATED:
                return onMessageUpdate((MessageUpdateEvent) data[0]);
            case DELETED:
                return onMessageDelete((MessageDeleteEvent) data[0]);
            case BULK_DELETED:
                return onMessageBulkDelete((MessageBulkDeleteEvent) data[0]);
            case EMBEDED:
                return onMessageEmbed((MessageEmbedEvent) data[0]);
            case REACTION_ADDED:
                return onMessageReactionAdd((MessageReactionAddEvent) data[0]);
            case REACTION_REMOVED:
                return onMessageReactionRemove((MessageReactionRemoveEvent) data[0]);
            case REACTION_REMOVED_ALL:
                return onMessageReactionRemoveAll((MessageReactionRemoveAllEvent) data[0]);
            default:
                return false;
        }
    }

    public boolean onMessageReceived(MessageEvent event);

    public boolean onMessageUpdate(MessageUpdateEvent event);

    public boolean onMessageDelete(MessageDeleteEvent event);

    public boolean onMessageBulkDelete(MessageBulkDeleteEvent event);

    public boolean onMessageEmbed(MessageEmbedEvent event);

    public boolean onMessageReactionAdd(MessageReactionAddEvent event);

    public boolean onMessageReactionRemove(MessageReactionRemoveEvent event);

    public boolean onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event);

}
