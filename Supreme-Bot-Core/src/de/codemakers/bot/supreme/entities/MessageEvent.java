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

    /**
     * Constructs a MessageEvent
     * @param api JDA
     * @param responseNumber ResponseNumber
     * @param messageId MessageID
     * @param channel MessageChannel
     */
    public MessageEvent(JDA api, long responseNumber, long messageId, MessageChannel channel) {
        super(api, responseNumber, messageId, channel);
    }

    /**
     * Returns the message
     * @return Message
     */
    public abstract Message getMessage();

    /**
     * Returns the author
     * @return User
     */
    public abstract User getAuthor();

    /**
     * Returns the member
     * @return Member
     */
    public abstract Member getMember();

    /**
     * Returns the text or private channel wether this message is private or not
     * @return MessageChannel
     */
    public abstract MessageChannel getMessageChannel();

    /**
     * Returns the text channel
     * @return MessageChannel
     */
    public abstract TextChannel getTextChannel();

    /**
     * Returns the private channel
     * @return PrivateChannel
     */
    public abstract PrivateChannel getPrivateChannel();

    /**
     * Sends a raw message to the source channel
     * @param message Raw Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendMessage(String message);

    /**
     * Sends a formatted message to the source channel
     * @param format String that gets formatted
     * @param args Arguments
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendMessageFormat(String format, Object... args);

    /**
     * Sends a message to the source channel
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendMessage(Message message);

    /**
     * Sends an embed message to the source channel
     * @param message MessageEmbed
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendMessage(MessageEmbed message);
    
    /**
     * Sends a raw message to the source channel
     * @param delay Delay in ms when the message gets deleted
     * @param message Raw Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendMessage(long delay, String message);

    /**
     * Sends a formatted message to the source channel
     * @param delay Delay in ms when the message gets deleted
     * @param format String that gets formatted
     * @param args Arguments
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendMessageFormat(long delay, String format, Object... args);

    /**
     * Sends a message to the source channel
     * @param delay Delay in ms when the message gets deleted
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendMessage(long delay, Message message);

    /**
     * Sends an embed message to the source channel
     * @param delay Delay in ms when the message gets deleted
     * @param message MessageEmbed
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendMessage(long delay, MessageEmbed message);

    /**
     * Sends a raw message to the source channel
     * and returns a Message object with that
     * you can edit your Message afterwards
     * @param message Raw Message
     * @return Message
     */
    public abstract Message sendAndWaitMessage(String message);

    /**
     * Sends a formatted message to the source channel
     * and returns a Message object with that
     * you can edit your Message afterwards
     * @param format String that gets formatted
     * @param args Arguments
     * @return Message
     */
    public abstract Message sendAndWaitMessageFormat(String format, Object... args);

    /**
     * Sends a message to the source channel
     * and returns a Message object with that
     * you can edit your Message afterwards
     * @param message Message
     * @return Message
     */
    public abstract Message sendAndWaitMessage(Message message);

    /**
     * Sends an embed message to the source channel
     * and returns a Message object with that
     * you can edit your Message afterwards
     * @param message MessageEmbed
     * @return Message
     */
    public abstract Message sendAndWaitMessage(MessageEmbed message);

    /**
     * Sends a file and optionally a message to the source channel
     * @param file File
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendFile(File file, Message message);

    /**
     * Sends a file with a custom filename and optionally a message to the source channel
     * @param file File
     * @param fileName Custom Filename
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendFile(File file, String fileName, Message message);

    /**
     * Sends a file read from an inputstream with a custom filename and optionally a message to the source channel
     * @param inputStream InputStream
     * @param fileName Custom Filename
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendFile(InputStream inputStream, String fileName, Message message);

    /**
     * Sends a file read from an byte array with a custom filename and optionally a message to the source channel
     * @param data Byte Array
     * @param fileName Custom Filename
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendFile(byte[] data, String fileName, Message message);
    
    /**
     * Sends a file and optionally a message to the source channel
     * @param delay Delay in ms when the message gets deleted
     * @param file File
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendFile(long delay, File file, Message message);

    /**
     * Sends a file with a custom filename and optionally a message to the source channel
     * @param delay Delay in ms when the message gets deleted
     * @param file File
     * @param fileName Custom Filename
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendFile(long delay, File file, String fileName, Message message);

    /**
     * Sends a file read from an inputstream with a custom filename and optionally a message to the source channel
     * @param delay Delay in ms when the message gets deleted
     * @param inputStream InputStream
     * @param fileName Custom Filename
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendFile(long delay, InputStream inputStream, String fileName, Message message);

    /**
     * Sends a file read from an byte array with a custom filename and optionally a message to the source channel
     * @param delay Delay in ms when the message gets deleted
     * @param data Byte Array
     * @param fileName Custom Filename
     * @param message Message
     * @return <tt>true</tt> if it was successful
     */
    public abstract boolean sendFile(long delay, byte[] data, String fileName, Message message);

    /**
     * Sends a file and optionally a message to the source channel
     * and returns a Message object with that
     * you can edit your Message afterwards
     * @param file File
     * @param message Message
     * @return Message
     */
    public abstract Message sendAndWaitFile(File file, Message message);

    /**
     * Sends a file with a custom filename and optionally a message to the source channel
     * and returns a Message object with that
     * you can edit your Message afterwards
     * @param file File
     * @param fileName Custom Filename
     * @param message Message
     * @return Message
     */
    public abstract Message sendAndWaitFile(File file, String fileName, Message message);

    /**
     * Sends a file read from an inputstream with a custom filename and optionally a message to the source channel
     * and returns a Message object with that
     * you can edit your Message afterwards
     * @param inputStream InputStream
     * @param fileName Custom Filename
     * @param message Message
     * @return Message
     */
    public abstract Message sendAndWaitFile(InputStream inputStream, String fileName, Message message);

    /**
     * Sends a file read from an byte array with a custom filename and optionally a message to the source channel
     * and returns a Message object with that
     * you can edit your Message afterwards
     * @param data Byte Array
     * @param fileName Custom Filename
     * @param message Message
     * @return Message
     */
    public abstract Message sendAndWaitFile(byte[] data, String fileName, Message message);

    /**
     * Returns true if this is a private message
     * @return <tt>true</tt> if this message comes from a private messages or not 
     */
    public abstract boolean isPrivate();

    /**
     * Returns the group (idk what this is, but nvm)
     * @return Group
     */
    public abstract Group getGroup();
    
    /**
     * Returns the guild from where the message comes (or null if this is a private message)
     * @return Guild
     */
    public abstract Guild getGuild();

}
