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

    public static final boolean DEBUG = true;

    private final Message message;

    public DefaultMessageEvent(MessageEvent event) {
        this(event.getJDA(), event.getResponseNumber(), event.getMessage());
    }

    public DefaultMessageEvent(JDA api, long responseNumber, Message message) {
        super(api, responseNumber, message.getIdLong(), message.getChannel());
        this.message = message;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public User getAuthor() {
        return message.getAuthor();
    }

    @Override
    public Member getMember() {
        return (isFromType(ChannelType.TEXT) || isFromType(ChannelType.PRIVATE)) ? getGuild().getMember(getAuthor()) : null;
    }

    @Override
    public MessageChannel getMessageChannel() {
        return message.getChannel();
    }

    @Override
    public TextChannel getTextChannel() {
        return message.getTextChannel();
    }

    @Override
    public PrivateChannel getPrivateChannel() {
        return message.getPrivateChannel();
    }

    @Override
    public boolean sendMessage(String message_) {
        try {
            if (isPrivate()) {
                Util.sendPrivateMessage(message.getAuthor(), message_);
            } else {
                message.getChannel().sendMessage(message_).queue();
            }
            return true;
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean sendMessageFormat(String format, Object... args) {
        try {
            if (isPrivate()) {
                Util.sendPrivateMessageFormat(message.getAuthor(), format, args);
            } else {
                message.getChannel().sendMessageFormat(format, args).queue();
            }
            return true;
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean sendMessage(Message message_) {
        try {
            if (isPrivate()) {
                Util.sendPrivateMessage(message.getAuthor(), message_);
            } else {
                message.getChannel().sendMessage(message_).queue();
            }
            return true;
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean sendMessage(MessageEmbed message_) {
        try {
            if (isPrivate()) {
                Util.sendPrivateMessage(message.getAuthor(), message_);
            } else {
                message.getChannel().sendMessage(message_).queue();
            }
            return true;
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
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
    public Message sendAndWaitMessage(String message_) {
        try {
            if (isPrivate()) {
                return Util.sendAndWaitPrivateMessage(message.getAuthor(), message_);
            } else {
                return message.getChannel().sendMessage(message_).complete();
            }
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public Message sendAndWaitMessageFormat(String format, Object... args) {
        try {
            if (isPrivate()) {
                return Util.sendAndWaitPrivateMessageFormat(message.getAuthor(), format, args);
            } else {
                return message.getChannel().sendMessageFormat(format, args).complete();
            }
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public Message sendAndWaitMessage(Message message_) {
        try {
            if (isPrivate()) {
                return Util.sendAndWaitPrivateMessage(message.getAuthor(), message_);
            } else {
                return message.getChannel().sendMessage(message_).complete();
            }
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public Message sendAndWaitMessage(MessageEmbed message_) {
        try {
            if (isPrivate()) {
                return Util.sendAndWaitPrivateMessage(message.getAuthor(), message_);
            } else {
                return message.getChannel().sendMessage(message_).complete();
            }
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean sendFile(File file, Message message_) {
        try {
            message.getChannel().sendFile(file, message_).queue();
            return true;
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean sendFile(File file, String fileName, Message message_) {
        try {
            message.getChannel().sendFile(file, fileName, message_).queue();
            return true;
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean sendFile(InputStream inputStream, String fileName, Message message_) {
        try {
            message.getChannel().sendFile(inputStream, fileName, message_).queue();
            return true;
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean sendFile(byte[] data, String fileName, Message message_) {
        try {
            message.getChannel().sendFile(data, fileName, message_).queue();
            return true;
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean sendFile(long delay, File file, Message message) {
        return Util.deleteMessage(sendAndWaitFile(file, message), delay);
    }

    @Override
    public boolean sendFile(long delay, File file, String fileName, Message message) {
        return Util.deleteMessage(sendAndWaitFile(file, fileName, message), delay);
    }

    @Override
    public boolean sendFile(long delay, InputStream inputStream, String fileName, Message message) {
        return Util.deleteMessage(sendAndWaitFile(inputStream, fileName, message), delay);
    }

    @Override
    public boolean sendFile(long delay, byte[] data, String fileName, Message message) {
        return Util.deleteMessage(sendAndWaitFile(data, fileName, message), delay);
    }

    @Override
    public Message sendAndWaitFile(File file, Message message_) {
        try {
            return message.getChannel().sendFile(file, message_).complete();
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public Message sendAndWaitFile(File file, String fileName, Message message_) {
        try {
            return message.getChannel().sendFile(file, fileName, message_).complete();
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public Message sendAndWaitFile(InputStream inputStream, String fileName, Message message_) {
        try {
            return message.getChannel().sendFile(inputStream, fileName, message_).complete();
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public Message sendAndWaitFile(byte[] data, String fileName, Message message_) {
        try {
            return message.getChannel().sendFile(data, fileName, message_).complete();
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean isPrivate() {
        return getGuild() == null;
    }

    @Override
    public Group getGroup() {
        return message.getGroup();
    }

    @Override
    public Guild getGuild() {
        return message.getGuild();
    }

}
