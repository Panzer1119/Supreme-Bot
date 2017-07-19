package de.panzercraft.bot.supreme.util;

import de.panzercraft.bot.supreme.settings.Settings;
import java.awt.Color;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
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
    private static String STANDARD_COMMAND_PREFIX = "!";
    private static final HashMap<String, String> COMMAND_PREFIXES = new HashMap<>();
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
            if (STANDARD_SETTINGS.getProperty("standard_command_prefix") == null) {
                setStandardCommandPrefix(STANDARD_COMMAND_PREFIX);
            }
            STANDARD_COMMAND_PREFIX = STANDARD_SETTINGS.getProperty("standard_command_prefix", STANDARD_COMMAND_PREFIX);
            System.out.println(String.format("Loaded \"%s\" as Standard Command Invoker", STANDARD_COMMAND_PREFIX));
            if (STANDARD_SETTINGS.getProperty("token") == null) {
                STANDARD_SETTINGS.setProperty("token", "Put your token here!");
            }
            TOKEN = STANDARD_SETTINGS.getProperty("token").getBytes();
            System.out.println(String.format("Loaded \"%s\" as Token", new String(TOKEN)));
            loadCommandPrefixesForGuilds();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
    }
    
    public static final byte[] getToken() {
        return TOKEN;
    }
    
    public static final boolean setToken(byte[] token) {
        Standard.TOKEN = token;
        STANDARD_SETTINGS.setProperty("token", new String(TOKEN));
        System.out.println(String.format("Setted \"%s\" as Token", new String(TOKEN)));
        return true;
    }
    
    public static final String getStandardCommandPrefix() {
        return STANDARD_COMMAND_PREFIX;
    }
    
    public static final boolean setStandardCommandPrefix(String commandPrefix) {
        if (commandPrefix.contains("\\")) {
            return false;
        }
        Standard.STANDARD_COMMAND_PREFIX = commandPrefix;
        STANDARD_SETTINGS.setProperty("command_prefix", STANDARD_COMMAND_PREFIX);
        System.out.println(String.format("Setted \"%s\" as Standard Command Invoker", STANDARD_COMMAND_PREFIX));
        return true;
    }
    
    public static final boolean loadCommandPrefixesForGuilds() {
        final Enumeration properties = STANDARD_SETTINGS.getSettings().propertyNames();
        while (properties.hasMoreElements()) {
            final String property = (String) properties.nextElement();
            if (property != null && !property.isEmpty() && property.startsWith("guild_command_prefix_")) {
                final String guild_id = property.replaceFirst("guild_command_prefix_", "");
                final String commandPrefix = STANDARD_SETTINGS.getProperty(property);
                setCommandPrefixForGuild(guild_id, commandPrefix);
            }
        }
        return true;
    }
    
    public static final String getCommandPrefixByGuild(Guild guild) {
        return getCommandPrefixByGuild(guild.getId());
    }
    
    public static final String getCommandPrefixByGuild(String guild_id) {
        String commandPrefix = COMMAND_PREFIXES.get(guild_id);
        if (commandPrefix == null) {
            commandPrefix = getStandardCommandPrefix();
            setCommandPrefixForGuild(guild_id, commandPrefix);
        }
        return commandPrefix;
    }
    
    public static final boolean setCommandPrefixForGuild(Guild guild, String commandPrefix) {
        return setCommandPrefixForGuild(guild.getId(), commandPrefix);
    }
    
    public static final boolean setCommandPrefixForGuild(String guild_id, String commandPrefix) {
        if (commandPrefix.contains("\\")) {
            return false;
        }
        COMMAND_PREFIXES.put(guild_id, commandPrefix);
        STANDARD_SETTINGS.setProperty("guild_command_prefix_" + guild_id, commandPrefix);
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
