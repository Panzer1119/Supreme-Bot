package de.panzercraft.bot.supreme.util;

import de.panzercraft.bot.supreme.commands.arguments.Argument;
import de.panzercraft.bot.supreme.core.SupremeBot;
import de.panzercraft.bot.supreme.permission.PermissionRole;
import de.panzercraft.bot.supreme.settings.Settings;
import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.io.FileUtils;

/**
 * Standard
 *
 * @author Panzer1119
 */
public class Standard {

    public static final String VERSION = "0.1";
    public static final String COMMAND_ESCAPE_STRING = "\\";
    public static final String COMMAND_ESCAPE_SPACE_STRING = "\"";
    public static final String COMMAND_DELIMITER_STRING = " ";
    public static final int STANDARD_NUMBER_OF_LINES_TO_GET_CLEARED = 10;
    public static final int PLAYLIST_LIMIT = 1000;
    private static byte[] TOKEN = null;
    private static String STANDARD_COMMAND_PREFIX = "!";

    public static final String STANDARD_SETTINGS_FILE_PATH = "data/settings.txt";
    public static final File STANDARD_SETTINGS_FILE = new File(STANDARD_SETTINGS_FILE_PATH);
    public static final Settings STANDARD_SETTINGS = new Settings(STANDARD_SETTINGS_FILE);

    private static final HashMap<String, Settings> GUILD_SETTINGS = new HashMap<>();
    public static final String STANDARD_GUILD_SETTINGS_FOLDER_PATH = "data/guilds";
    public static final File STANDARD_GUILD_SETTINGS_FOLDER = new File(STANDARD_GUILD_SETTINGS_FOLDER_PATH);
    public static final FileNamer STANDARD_GUILD_SETTINGS_FILENAMER = new FileNamer("settings_", ".txt");
    
    public static final String STANDARD_PERMISSIONS_FILE_PATH = "data/permissions.txt";
    public static final File STANDARD_PERMISSIONS_FILE = new File(STANDARD_PERMISSIONS_FILE_PATH);
    public static final String STANDARD_PERMISSIONS_PATH = "/de/panzercraft/bot/supreme/permission/permissions.txt";

    public static final boolean reloadSettings() {
        try {
            STANDARD_SETTINGS.loadSettings();
            if (STANDARD_SETTINGS.getProperty("standard_command_prefix", null) == null) {
                STANDARD_SETTINGS.setProperty("standard_command_prefix", "!");
            }
            STANDARD_COMMAND_PREFIX = STANDARD_SETTINGS.getProperty("standard_command_prefix", "!");
            if (STANDARD_SETTINGS.getProperty("token", null) == null) {
                STANDARD_SETTINGS.setProperty("token", "Put your token here!");
            }
            TOKEN = STANDARD_SETTINGS.getProperty("token", null).getBytes();
            System.out.println("Reloaded Settings!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded Settings: " + ex);
            return false;
        }
    }

    public static final boolean reloadPermissions() {
        try {
            if (STANDARD_PERMISSIONS_FILE.exists() && STANDARD_PERMISSIONS_FILE.isFile()) {
                PermissionRole.loadPermissionRoles(STANDARD_PERMISSIONS_FILE);
            } else if (!STANDARD_PERMISSIONS_FILE.exists()) {
                try {
                    FileUtils.copyInputStreamToFile(Standard.class.getResourceAsStream(STANDARD_PERMISSIONS_PATH), STANDARD_PERMISSIONS_FILE);
                    PermissionRole.loadPermissionRoles(STANDARD_PERMISSIONS_FILE);
                } catch (Exception ex) {
                    System.err.println(ex);
                    PermissionRole.loadPermissionRoles(STANDARD_PERMISSIONS_PATH);
                }
            } else if (!STANDARD_PERMISSIONS_FILE.isFile()) {
                PermissionRole.loadPermissionRoles(STANDARD_PERMISSIONS_PATH);
            } else {
                return false;
            }
            System.out.println("Reloaded Permissions!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded Permissions: " + ex);
            return false;
        }
    }
    
    public static final boolean readGuildSettings() {
        try {
            GUILD_SETTINGS.clear();
            for (File file : STANDARD_GUILD_SETTINGS_FOLDER.listFiles()) {
                if (STANDARD_GUILD_SETTINGS_FILENAMER.isFileNameOfThis(file)) {
                    GUILD_SETTINGS.put(STANDARD_GUILD_SETTINGS_FILENAMER.getExtraOfFileName(file), new Settings(file).setAutoAddProperties(true));
                }
            }
            reloadAllGuildSettings();
            System.out.println("Reloaded Guild Settings Folder!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded Guild Settings Folder: " + ex);
            return false;
        }
    }
    
    public static final boolean reloadAllGuildSettings() {
        try {
            GUILD_SETTINGS.keySet().stream().forEach((guild_id) -> {
                GUILD_SETTINGS.get(guild_id).loadSettings();
            });
            System.out.println("Reloaded All Guild Settings!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded Guild Settings: " + ex);
            return false;
        }
    }
    
    public static final boolean saveAllGuildSettings() {
        try {
            GUILD_SETTINGS.keySet().stream().forEach((guild_id) -> {
                GUILD_SETTINGS.get(guild_id).saveSettings();
            });
            System.out.println("Saved All Guild Settings!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded Guild Settings: " + ex);
            return false;
        }
    }

    public static final byte[] getToken() {
        return TOKEN;
    }

    public static final boolean setToken(byte[] token) {
        Standard.TOKEN = token;
        STANDARD_SETTINGS.setProperty("token", new String(TOKEN));
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
        return true;
    }
    
    //****************************************************************//
    //*********************GUILD SPECIFIC START***********************//
    //****************************************************************//
    
    public static final Settings getGuildSettings(Guild guild) {
        if (guild == null) {
            return null;
        }
        return getGuildSettings(guild.getId());
    }
    
    public static final Settings getGuildSettings(String guild_id) {
        Settings settings = GUILD_SETTINGS.get(guild_id);
        if (settings == null) {
            settings = new Settings(STANDARD_GUILD_SETTINGS_FILENAMER.createFile(STANDARD_GUILD_SETTINGS_FOLDER, guild_id)).setAutoAddProperties(true);
            settings.loadSettings();
            GUILD_SETTINGS.put(guild_id, settings);
        }
        return settings;
    }
    
    public static final String getCommandPrefixByGuild(Guild guild) {
        return getCommandPrefixByGuild(guild.getId());
    }

    public static final String getCommandPrefixByGuild(String guild_id) {
        return getGuildSettings(guild_id).getProperty("command_prefix", getStandardCommandPrefix());
    }

    public static final boolean setCommandPrefixForGuild(Guild guild, String commandPrefix) {
        return setCommandPrefixForGuild(guild.getId(), commandPrefix);
    }

    public static final boolean setCommandPrefixForGuild(String guild_id, String commandPrefix) {
        if (commandPrefix.contains("\\")) {
            return false;
        }
        getGuildSettings(guild_id).setProperty("command_prefix", commandPrefix);
        return true;
    }

    public static final long getAutoDeleteCommandNotFoundMessageDelayByGuild(Guild guild) {
        return getAutoDeleteCommandNotFoundMessageDelayByGuild(guild.getId());
    }
    
    public static final long getAutoDeleteCommandNotFoundMessageDelayByGuild(String guild_id) {
        return getGuildSettings(guild_id).getProperty("autoDeleteCommandNotFoundMessageDelay", -1);
    }
    
    public static final boolean setAutoDeleteCommandNotFoundMessageDelayForGuild(Guild guild, long autoDeleteCommandNotFoundMessageDelay) {
        return setAutoDeleteCommandNotFoundMessageDelayForGuild(guild.getId(), autoDeleteCommandNotFoundMessageDelay);
    }

    public static final boolean setAutoDeleteCommandNotFoundMessageDelayForGuild(String guild_id, long autoDeleteCommandNotFoundMessageDelay) {
        getGuildSettings(guild_id).setProperty("autoDeleteCommandNotFoundMessageDelay", autoDeleteCommandNotFoundMessageDelay);
        return true;
    }

    public static final boolean isAutoDeletingCommandByGuild(Guild guild) {
        return isAutoDeletingCommandByGuild(guild.getId());
    }
    
    public static final boolean isAutoDeletingCommandByGuild(String guild_id) {
        return getGuildSettings(guild_id).getProperty("autoDeletingCommand", false);
    }

    public static final boolean setAutoDeletingCommandForGuild(Guild guild, boolean autoDeletingCommand) {
        return setAutoDeletingCommandForGuild(guild.getId(), autoDeletingCommand);
    }
    
    public static final boolean setAutoDeletingCommandForGuild(String guild_id, boolean autoDeletingCommand) {
        getGuildSettings(guild_id).setProperty("autoDeletingCommand", autoDeletingCommand);
        return true;
    }
    
    //****************************************************************//
    //*********************GUILD SPECIFIC STOP************************//
    //****************************************************************//

    public static final Message getNoPermissionMessage(User user, String extra) {
        return new MessageBuilder().append(String.format(":warning: Sorry %s, you don't have the permissions to use this %s!", user.getAsMention(), extra)).build();
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

    public static final boolean isSuperOwner(Member member) {
        if (member == null) {
            return false;
        }
        return isSuperOwner(member.getUser());
    }

    public static final boolean isSuperOwner(User user) {
        if (user == null) {
            return false;
        }
        final String super_owner = STANDARD_SETTINGS.getProperty("super_owner", null);
        if (super_owner == null) {
            return true;
        }
        return user.getId().equals(super_owner);
    }
    
    public static final Guild getGuildById(String guild_id) {
        return SupremeBot.jda.getGuildById(guild_id);
    }
    
    public static final String resolveGuildId(Guild guild, String guild_id) {
        if (guild != null && guild_id != null && guild_id.equalsIgnoreCase("this")) {
            guild_id = guild.getId();
        }
        return guild_id;
    }

    public static final String[] ULTRA_FORBIDDEN = new String[]{"token", "super_owner"};
    public static final String[] STANDARD_ARGUMENT_PREFIXES = new String[]{"-", "/", "!"};
    public static final Argument ARGUMENT_GLOBAL = new Argument("global", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_DIRECT = new Argument("direct", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_PRIVATE = new Argument("private", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_ALL = new Argument("all", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_SETTINGS = new Argument("settings", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_GUILD_SETTINGS = new Argument("guild_settings", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_SETTINGS_SET = new Argument("set", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_SETTINGS_GET = new Argument("get", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_SETTINGS_REMOVE = new Argument("remove", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_SETTINGS_LIST = new Argument("list", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_SETTINGS_DEFAULT = new Argument("default", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_PERMISSIONS = new Argument("permissions", STANDARD_ARGUMENT_PREFIXES);

}
