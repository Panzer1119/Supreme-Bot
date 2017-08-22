package de.codemakers.bot.supreme.entities;

import de.codemakers.bot.supreme.util.Util;
import java.io.File;
import java.io.InputStream;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * MessageEvent
 *
 * @author Panzer1119
 */
public class DefaultMessageEvent extends MessageEvent {

    private final Message message;

    public DefaultMessageEvent(JDA api, long responseNumber, Message message) {
        super(api, responseNumber, message.getIdLong(), message.getChannel());
        this.message = message;
    }

    @Override
    public final Message getMessage() {
        return message;
    }

    @Override
    public final User getAuthor() {
        return message.getAuthor();
    }

    @Override
    public final Member getMember() {
        return (isFromType(ChannelType.TEXT) || isFromType(ChannelType.PRIVATE)) ? getGuild().getMember(getAuthor()) : null;
    }
    
    @Override
    public final MessageChannel getMessageChannel() {
        return message.getChannel();
    }
    
    @Override
    public final TextChannel getTextChannel() {
        return message.getTextChannel();
    }
    
    @Override
    public final PrivateChannel getPrivateChannel() {
        return message.getPrivateChannel();
    }
    
    @Override
    public final boolean sendMessage(String message_) {
        try {
            if (isPrivate()) {
                Util.sendPrivateMessage(message.getAuthor(), message_);
            } else {
                message.getChannel().sendMessage(message_).queue();
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public final boolean sendMessageFormat(String format, Object... args) {
        try {
            if (isPrivate()) {
                Util.sendPrivateMessageFormat(message.getAuthor(), format, args);
            } else {
                message.getChannel().sendMessageFormat(format, args).queue();
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public final boolean sendMessage(Message message_) {
        try {
            if (isPrivate()) {
                Util.sendPrivateMessage(message.getAuthor(), message_);
            } else {
                message.getChannel().sendMessage(message_).queue();
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public final boolean sendMessage(MessageEmbed message_) {
        try {
            if (isPrivate()) {
                Util.sendPrivateMessage(message.getAuthor(), message_);
            } else {
                message.getChannel().sendMessage(message_).queue();
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean sendMessage(long delay, String message_) {
        return Util.deleteMessage(sendAndWaitMessage(message_), delay);
    }

    @Override
    public boolean sendMessageFormat(long delay, String format, Object... args) {
        return Util.deleteMessage(sendAndWaitMessageFormat(format, args), delay);
    }

    @Override
    public boolean sendMessage(long delay, Message message_) {
        return Util.deleteMessage(sendAndWaitMessage(message_), delay);
    }

    @Override
    public boolean sendMessage(long delay, MessageEmbed message_) {
        return Util.deleteMessage(sendAndWaitMessage(message_), delay);
    }
    
    @Override
    public final Message sendAndWaitMessage(String message_) {
        try {
            if (isPrivate()) {
                return Util.sendAndWaitPrivateMessage(message.getAuthor(), message_);
            } else {
                return message.getChannel().sendMessage(message_).complete();
            }
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public final Message sendAndWaitMessageFormat(String format, Object... args) {
        try {
            if (isPrivate()) {
                return Util.sendAndWaitPrivateMessageFormat(message.getAuthor(), format, args);
            } else {
                return message.getChannel().sendMessageFormat(format, args).complete();
            }
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public final Message sendAndWaitMessage(Message message_) {
        try {
            if (isPrivate()) {
                return Util.sendAndWaitPrivateMessage(message.getAuthor(), message_);
            } else {
                return message.getChannel().sendMessage(message_).complete();
            }
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public final Message sendAndWaitMessage(MessageEmbed message_) {
        try {
            if (isPrivate()) {
                return Util.sendAndWaitPrivateMessage(message.getAuthor(), message_);
            } else {
                return message.getChannel().sendMessage(message_).complete();
            }
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public final boolean sendFile(File file, Message message) {
        try {
            message.getChannel().sendFile(file, message).queue();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public final boolean sendFile(File file, String fileName, Message message) {
        try {
            message.getChannel().sendFile(file, fileName, message).queue();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public final boolean sendFile(InputStream inputStream, String fileName, Message message) {
        try {
            message.getChannel().sendFile(inputStream, fileName, message).queue();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public final boolean sendFile(byte[] data, String fileName, Message message) {
        try {
            message.getChannel().sendFile(data, fileName, message).queue();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public final Message sendAndWaitFile(File file, Message message) {
        try {
            return message.getChannel().sendFile(file, message).complete();
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public final Message sendAndWaitFile(File file, String fileName, Message message) {
        try {
            return message.getChannel().sendFile(file, fileName, message).complete();
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public final Message sendAndWaitFile(InputStream inputStream, String fileName, Message message) {
        try {
            return message.getChannel().sendFile(inputStream, fileName, message).complete();
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public final Message sendAndWaitFile(byte[] data, String fileName, Message message) {
        try {
            return message.getChannel().sendFile(data, fileName, message).complete();
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public final boolean isPrivate() {
        return getGuild() == null;
    }

    @Override
    public final Group getGroup() {
        return message.getGroup();
    }

    @Override
    public final Guild getGuild() {
        return message.getGuild();
    }

}
