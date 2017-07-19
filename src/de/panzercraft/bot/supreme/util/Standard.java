package de.panzercraft.bot.supreme.util;

import de.panzercraft.bot.supreme.settings.Settings;
import java.awt.Color;
import java.io.File;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Standard
 *
 * @author Panzer1119
 */
public class Standard {

    public static final String VERSION = "0.1";
    private static byte[] TOKEN = null;
    private static String COMMAND_PREFIX = "!";
    public static final String COMMAND_ESCAPE_STRING = "\\";
    public static final String COMMAND_ESCAPE_SPACE_STRING = "\"";
    public static final String COMMAND_DELIMITER_STRING = " ";
    public static final int STANDARD_NUMBER_OF_LINES_TO_GET_CLEARED = 10;
    
    public static final int PLAYLIST_LIMIT = 1000;
    
    public static final String STANDARD_SETTINGS_PATH = "settings.txt";
    public static final File STANDARD_SETTINGS_FILE = new File(STANDARD_SETTINGS_PATH);
    public static final Settings STANDARD_SETTINGS = new Settings(STANDARD_SETTINGS_FILE);
    
    public static final boolean init() {
        try {
            if (STANDARD_SETTINGS.getProperty("command_prefix") == null) {
                setCommandPrefix(COMMAND_PREFIX);
            }
            COMMAND_PREFIX = STANDARD_SETTINGS.getProperty("command_prefix", COMMAND_PREFIX);
            System.out.println(String.format("Loaded \"%s\" as Command Invoker", COMMAND_PREFIX));
            if (STANDARD_SETTINGS.getProperty("token") == null) {
                STANDARD_SETTINGS.setProperty("token", "Put your token here!");
            }
            TOKEN = STANDARD_SETTINGS.getProperty("token").getBytes();
            System.out.println(String.format("Loaded \"%s\" as Token", new String(TOKEN)));
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }
    
    public static final byte[] getToken() {
        return TOKEN;
    }
    
    public static final String getCommandPrefix() {
        return COMMAND_PREFIX;
    }
    
    public static final boolean setCommandPrefix(String commandPrefix) {
        if (commandPrefix.contains("\\")) {
            return false;
        }
        Standard.COMMAND_PREFIX = commandPrefix;
        STANDARD_SETTINGS.setProperty("command_prefix", COMMAND_PREFIX);
        System.out.println(String.format("Setted \"%s\" as Command Invoker", COMMAND_PREFIX));
        return true;
    }

    public static final Message getNoPermissionMessage(User user, String extra) {
        return new MessageBuilder().append(String.format(":warning: Sorry, %s you don't have the permissions to use this %s!", user.getAsMention(), extra)).build();
    }
    
    public static final EmbedBuilder getMessageEmbed(Color color, String message) {
        return new EmbedBuilder().setColor(color).setDescription(message);
    }
    
    public static final EmbedBuilder getMessageEmbed(Color color, String format, Object... args) {
        return getMessageEmbed(color, String.format(format, args));
    }
    
    public static final boolean sendErrorMessage(MessageReceivedEvent event, String content) {
        event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.RED, content).build()).queue();
        return true;
    }

}
