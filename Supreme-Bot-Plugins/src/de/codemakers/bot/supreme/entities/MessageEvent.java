package de.codemakers.bot.supreme.entities;

import java.io.File;
import java.io.InputStream;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

/**
 * MessageEvent
 *
 * @author Panzer1119
 */
public abstract class MessageEvent extends GenericMessageEvent { //FIXME JDA Dependency komplett entfernen!!! Weil zu unsicher!!!

    public MessageEvent(JDA api, long responseNumber, long messageId, MessageChannel channel) {
        super(api, responseNumber, messageId, channel);
    }

    public abstract Message getMessage();

    public abstract User getAuthor();

    public abstract Member getMember();

    public abstract MessageChannel getMessageChannel();

    public abstract TextChannel getTextChannel();

    public abstract PrivateChannel getPrivateChannel();

    public abstract boolean sendMessage(String message);

    public abstract boolean sendMessageFormat(String format, Object... args);

    public abstract boolean sendMessage(Message message);

    public abstract boolean sendMessage(MessageEmbed message);

    public abstract Message sendAndWaitMessage(String message);

    public abstract Message sendAndWaitMessageFormat(String format, Object... args);

    public abstract Message sendAndWaitMessage(Message message);

    public abstract Message sendAndWaitMessage(MessageEmbed message);

    public abstract boolean sendFile(File file, Message message);

    public abstract boolean sendFile(File file, String fileName, Message message);

    public abstract boolean sendFile(InputStream inputStream, String fileName, Message message);

    public abstract boolean sendFile(byte[] data, String fileName, Message message);

    public abstract Message sendAndWaitFile(File file, Message message);

    public abstract Message sendAndWaitFile(File file, String fileName, Message message);

    public abstract Message sendAndWaitFile(InputStream inputStream, String fileName, Message message);

    public abstract Message sendAndWaitFile(byte[] data, String fileName, Message message);

    public abstract boolean isPrivate();

    public abstract Group getGroup();

    public abstract Guild getGuild();

}
