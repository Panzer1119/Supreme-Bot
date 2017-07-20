package de.panzercraft.bot.supreme.entities;

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

/**
 * MessageEvent
 *
 * @author Panzer1119
 */
public class MessageEvent extends GenericMessageEvent {

    private final Message message;

    public MessageEvent(JDA api, long responseNumber, Message message) {
        super(api, responseNumber, message.getIdLong(), message.getChannel());
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public User getAuthor() {
        return message.getAuthor();
    }

    public Member getMember() {
        return (isFromType(ChannelType.TEXT) || isFromType(ChannelType.PRIVATE)) ? getGuild().getMember(getAuthor()) : null;
    }
    
    public MessageChannel getMessageChannel() {
        return message.getChannel();
    }

    public Group getGroup() {
        return message.getGroup();
    }

    public Guild getGuild() {
        return message.getGuild();
    }

}
