package de.codemakers.bot.supreme.util;

import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.Argument;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.permission.PermissionRole;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.plugin.Plugin;
import de.codemakers.bot.supreme.plugin.PluginManager;
import de.codemakers.bot.supreme.settings.DefaultSettings;
import de.codemakers.bot.supreme.settings.Settings;
import de.codemakers.bot.supreme.util.updater.Updater;
import java.awt.Color;
import java.io.BufferedWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AccountManager;
import org.apache.commons.io.FileUtils;

/**
 * Standard
 *
 * @author Panzer1119
 */
public class Standard {

    public static final String VERSION = "0.1";
    public static final String COMMAND_ESCAPE_STRING = "\\";
    public static final String[] COMMAND_ESCAPE_SPACE_STRINGS = new String[]{"\"", "'", "»", "«", "„", "”", "“"};
    public static final String COMMAND_DELIMITER_STRING = " ";
    public static final String STANDARD_NUMBER_SEPARATOR = ":";
    public static final String STANDARD_ARRAY_SEPARATOR = ";";
    public static final String TAB = "  ";
    public static final String STANDARD_DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final DateTimeFormatter STANDARD_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(STANDARD_DATE_TIME_FORMAT);
    public static final String STANDARD_DATE_TIME_FILE_FORMAT = "yyyy.MM.dd_HH.mm.ss";
    public static final DateTimeFormatter STANDARD_DATE_TIME_FILE_FORMATTER = DateTimeFormatter.ofPattern(STANDARD_DATE_TIME_FILE_FORMAT);
    public static final String STANDARD_NAMESPACE_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final char[] STANDARD_NAMESPACE = STANDARD_NAMESPACE_STRING.toCharArray();
    public static final int STANDARD_NUMBER_OF_LINES_TO_GET_CLEARED = 10;
    public static final int PLAYLIST_LIMIT = 1000;
    public static final long STANDARD_MESSAGE_DELETING_DELAY = 5000L;
    private static byte[] TOKEN = null;
    private static String STANDARD_COMMAND_PREFIX = "!";
    private static String NICKNAME = "Supreme-Bot";
    private static final String PLUGIN_PERMISSION_ADMIN_STRING_NAME = "plugin_permission_admin_string";
    private static String PLUGIN_PERMISSION_ADMIN_STRING = null;
    public static Getter<JDA> getter = () -> null;

    /**
     * https://regexr.com/
     * (\d*[.,]*\d+)((?:w(?:eek(?:s)?)?)|(?:d(?:ay(?:s)?)?)|(?:h(?:our(?:s)?)?)|(?:m(?:in(?:ute(?:s)?)?)?)|(?:s(?:ec(?:ond(?:s)?)?)?))
     * (\d*[.,]*\d+)((?:y(?:ear(?:s)?)?)|(?:w(?:eek(?:s)?)?)|(?:d(?:ay(?:s)?)?)|(?:h(?:our(?:s)?)?)|(?:m(?:in(?:ute(?:s)?)?)?)|(?:s(?:ec(?:ond(?:s)?)?)?))
     * (\d*[.,]*\d+)((?:y(?:ear(?:s)?)?)|(?:w(?:eek(?:s)?)?)|(?:d(?:ay(?:s)?)?)|(?:h(?:our(?:s)?)?)|(?:m(?:in(?:(?:ute(?:s)?)|(?:s))?)?)|(?:s(?:ec(?:(?:ond(?:s)?)|(?:s))?)?))
     */
    public static final Pattern PATTERN_TIME = Pattern.compile("(\\d*[.,]*\\d+)((?:y(?:ear(?:s)?)?)|(?:w(?:eek(?:s)?)?)|(?:d(?:ay(?:s)?)?)|(?:h(?:our(?:s)?)?)|(?:m(?:in(?:(?:ute(?:s)?)|(?:s))?)?)|(?:s(?:ec(?:(?:ond(?:s)?)|(?:s))?)?))", Pattern.CASE_INSENSITIVE);

    public static final Settings STANDARD_NULL_SETTINGS = new DefaultSettings();

    public static final String STANDARD_DATA_FOLDER_NAME = "data";
    public static final AdvancedFile STANDARD_DATA_FOLDER = new AdvancedFile(false, (Object) STANDARD_DATA_FOLDER_NAME);
    public static final String STANDARD_SETTINGS_FILE_NAME = "settings.txt";
    public static final AdvancedFile STANDARD_SETTINGS_FILE = getFile(STANDARD_SETTINGS_FILE_NAME);
    public static final Settings STANDARD_SETTINGS = new DefaultSettings(STANDARD_SETTINGS_FILE);

    public static final String STANDARD_GUILDS_FOLDER_NAME = "guilds";
    public static final AdvancedFile STANDARD_GUILDS_FOLDER = getFile(STANDARD_GUILDS_FOLDER_NAME);
    public static final String STANDARD_GUILD_SETTINGS_FILE_NAME = "settings.txt";
    private static final ArrayList<AdvancedGuild> GUILDS = new ArrayList<>();

    public static final String STANDARD_PERMISSIONS_FILE_NAME = "permissions.xml";
    public static final AdvancedFile STANDARD_PERMISSIONS_FILE = getFile(STANDARD_PERMISSIONS_FILE_NAME);
    public static final String STANDARD_PERMISSIONS_PATH = '/' + PermissionRole.class.getName().substring(0, PermissionRole.class.getName().length() - PermissionRole.class.getSimpleName().length()).replace('.', '/') + STANDARD_PERMISSIONS_FILE_NAME;

    public static final String STANDARD_PLUGINS_FOLDER_NAME = "plugins";
    public static final AdvancedFile STANDARD_PLUGINS_FOLDER = getFile(STANDARD_PLUGINS_FOLDER_NAME);
    public static final PluginManager STANDARD_PLUGIN_MANAGER = new PluginManager();

    public static final String STANDARD_DOWNLOAD_FOLDER_NAME = "downloads";
    public static final AdvancedFile STANDARD_DOWNLOAD_FOLDER = getFile(STANDARD_DOWNLOAD_FOLDER_NAME);
    public static final String STANDARD_UPLOAD_FOLDER_NAME = "uploads";
    public static final AdvancedFile STANDARD_UPLOAD_FOLDER = getFile(STANDARD_UPLOAD_FOLDER_NAME);

    public static final String STANDARD_LOGS_FOLDER_NAME = "logs";
    public static final String STANDARD_LOG_FILE_FORMAT = "log_%s.txt";
    public static final String STANDARD_LOG_FILE_NAME = "log.txt";
    public static final AdvancedFile STANDARD_LOG_FOLDER = getFile(STANDARD_LOGS_FOLDER_NAME);
    public static final AdvancedFile CURRENT_LOG_FILE = Standard.createCurrentLogFile();

    public static final String STANDARD_RECORDINGS_FOLDER_NAME = "recordings";
    public static final AdvancedFile STANDARD_RECORDINGS_FOLE = getFile(STANDARD_RECORDINGS_FOLDER_NAME);

    public static PermissionRole STANDARD_PERMISSION_ROLE = null;

    public static final String XML_PERMISSIONROLES = "permissionroles";
    public static final String XML_PERMISSIONROLE = "permissionrole";
    public static final String XML_PERMISSIONS = "permissions";
    public static final String XML_PERMISSION = "permission";
    public static final String XML_PERMISSIONROLEID = "permissionroleid";
    public static final String XML_NAME = "name";
    public static final String XML_INHERIT = "inherit";
    public static final String XML_INHERITALL = "inheritall";
    public static final String XML_STANDARD = "standard";
    public static final String XML_ROLES = "roles";
    public static final String XML_ROLE = "role";
    public static final String XML_ROLEID = "roleid";
    public static final String XML_USERID = "userid";

    public static final String STANDARD_METHOD_NAME_NEW_INSTANCE = "newInstance";

    public static final ArrayList<Runnable> SHUTDOWNHOOKS = new ArrayList<>();

    public static final boolean DEBUG_PERMISSION_HANDLER = false;

    static {
        SHUTDOWNHOOKS.add(() -> {
            Util.killAndFireAllTimerTask();
            Updater.kill(10, TimeUnit.SECONDS);
        });
    }

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
            if (STANDARD_SETTINGS.getProperty("nickname", null) == null) {
                STANDARD_SETTINGS.setProperty("nickname", "Supreme-Bot");
            }
            setNickname(STANDARD_SETTINGS.getProperty("nickname", "Supreme-Bot"));
            reloadPluginPermissionAdminString();
            System.out.println("Reloaded Settings!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded Settings: " + ex);
            ex.printStackTrace();
            return false;
        }
    }

    public static final JDA getJDA() {
        return getter.get();
    }

    public static final boolean reloadPermissionRoles() {
        try {
            if (STANDARD_PERMISSIONS_FILE.exists() && STANDARD_PERMISSIONS_FILE.isFile()) {
                PermissionHandler.loadPermissionRoles(STANDARD_PERMISSIONS_FILE);
            } else if (!STANDARD_PERMISSIONS_FILE.exists()) {
                try {
                    FileUtils.copyInputStreamToFile(Standard.class.getResourceAsStream(STANDARD_PERMISSIONS_PATH), STANDARD_PERMISSIONS_FILE.toFile());
                    PermissionHandler.loadPermissionRoles(STANDARD_PERMISSIONS_FILE);
                } catch (Exception ex) {
                    System.err.println(ex);
                    PermissionHandler.loadPermissionRoles(STANDARD_PERMISSIONS_PATH);
                }
            } else if (!STANDARD_PERMISSIONS_FILE.isFile()) {
                PermissionHandler.loadPermissionRoles(STANDARD_PERMISSIONS_PATH);
            } else {
                return false;
            }
            System.out.println("Reloaded PermissionRoles!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded PermissionRoles: " + ex);
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean reloadPluginPermissionAdminString() {
        try {
            if (PLUGIN_PERMISSION_ADMIN_STRING == null) {
                PLUGIN_PERMISSION_ADMIN_STRING = STANDARD_SETTINGS.getProperty(PLUGIN_PERMISSION_ADMIN_STRING_NAME, null);
                if (PLUGIN_PERMISSION_ADMIN_STRING == null) {
                    PLUGIN_PERMISSION_ADMIN_STRING = Util.generateRandomString(250);
                    STANDARD_SETTINGS.setProperty(PLUGIN_PERMISSION_ADMIN_STRING_NAME, PLUGIN_PERMISSION_ADMIN_STRING);
                }
                System.out.println("Reloaded PluginPermissionAdminString!");
            }
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded PluginPermissionAdminString: " + ex);
            ex.printStackTrace();
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
        if (commandPrefix == null || commandPrefix.isEmpty() || commandPrefix.contains("\\")) {
            return false;
        }
        Standard.STANDARD_COMMAND_PREFIX = commandPrefix;
        STANDARD_SETTINGS.setProperty("command_prefix", STANDARD_COMMAND_PREFIX);
        return true;
    }

    public static final String getNickname() {
        return NICKNAME;
    }

    public static final boolean setNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return false;
        }
        Standard.NICKNAME = nickname;
        STANDARD_SETTINGS.setProperty("nickname", NICKNAME);
        try {
            final JDA jda = getJDA();
            if (jda != null) {
                final SelfUser user = jda.getSelfUser();
                if (user != null) {
                    if (!nickname.equals(user.getName())) {
                        final AccountManager manager = user.getManager();
                        if (manager != null) {
                            manager.setName(nickname).queue();
                            System.out.println("Changed nickname to \"" + nickname + "\".");
                            return true;
                        } else {
                            System.err.println("Failed to change nickname to \"" + nickname + "\": AccountManager is null!");
                        }
                    } else {
                        //System.err.println("Failed to change nickname to \"" + nickname + "\": Nickname is already set!");
                    }
                } else {
                    System.err.println("Failed to change nickname to \"" + nickname + "\": SelfUser is null!");
                }
            } else {
                System.err.println("Failed to change nickname to \"" + nickname + "\": JDA is null!");
            }
            return false;
        } catch (Exception ex) {
            System.err.println("Failed to change nickname to \"" + nickname + "\"");
            //ex.printStackTrace();
            return false;
        }
    }

    public static final AdvancedFile getFile(String fileName) {
        if (fileName == null) {
            return null;
        }
        return new AdvancedFile(STANDARD_DATA_FOLDER, fileName);
    }

    public static final AdvancedFile createCurrentLogFile() {
        return getLogFile(String.format(STANDARD_LOG_FILE_FORMAT, LocalDateTime.now().format(STANDARD_DATE_TIME_FILE_FORMATTER)));
    }

    public static final AdvancedFile getLogFile(String name) {
        return new AdvancedFile(STANDARD_LOG_FOLDER, name);
    }

    public static final Pattern getTimePattern() {
        return PATTERN_TIME;
    }

    //****************************************************************//
    //*********************GUILD SPECIFIC START***********************//
    //****************************************************************//
    public static final boolean initAdvancedGuilds() {
        try {
            GUILDS.stream().forEach((advancedGuild) -> {
                advancedGuild.sayHi();
            });
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean loadAllGuilds() {
        try {
            GUILDS.clear();
            for (AdvancedFile file : STANDARD_GUILDS_FOLDER.listAdvancedFiles()) {
                if (file.isDirectory()) {
                    GUILDS.add(new AdvancedGuild(file.getName(), file));
                }
            }
            reloadAllGuilds();
            System.out.println("Reloaded Guilds Folder!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded Guilds Folder: " + ex);
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean reloadAllGuilds() {
        try {
            reloadAllGuildSettings();
            reloadAllGuildPermissions();
            System.out.println("Reloaded All Guilds!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded All Guilds: " + ex);
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean reloadAllGuildSettings() {
        try {
            GUILDS.stream().forEach((advancedGuild) -> {
                advancedGuild.getSettings().loadSettings();
                setNicknameForGuild(advancedGuild.getGuildId(), getNicknameByGuild(advancedGuild.getGuildId()));
            });
            System.out.println("Reloaded All Guild Settings!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Reloaded All Guild Settings: " + ex);
            ex.printStackTrace();
            return false;
        }
    }

    private static final boolean reloadAllGuildPermissions() {
        try {
            GUILDS.stream().forEach((advancedGuild) -> {
                final AdvancedFile file = advancedGuild.getPermissionsFile();
                if (file != null) {
                    PermissionHandler.loadPermissionsForGuild(file, advancedGuild.getGuildIdLong());
                }
            });
            System.out.println("Reloaded All Guild Permissions!");
            return true;
        } catch (Exception ex) {
            System.out.println("Not Reloaded All Guild Permissions!");
            return false;
        }
    }

    public static final boolean saveAllGuildSettings() {
        try {
            GUILDS.stream().forEach((advancedGuild) -> {
                advancedGuild.getSettings().saveSettings();
            });
            System.out.println("Saved All Guild Settings!");
            return true;
        } catch (Exception ex) {
            System.err.println("Not Saved All Guild Settings: " + ex);
            ex.printStackTrace();
            return false;
        }
    }

    public static final AdvancedFile createGuildFolder(Guild guild) {
        if (guild == null) {
            return null;
        }
        return createGuildFolder(guild.getId());
    }

    public static final AdvancedFile createGuildFolder(String guild_id) {
        if (guild_id == null) {
            return null;
        }
        try {
            final AdvancedFile file = new AdvancedFile(false, STANDARD_GUILDS_FOLDER, guild_id);
            file.createAdvancedFile();
            return file;
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }

    public static final AdvancedGuild getAdvancedGuild(Guild guild) {
        if (guild == null) {
            return null;
        }
        return getAdvancedGuild(guild.getId());
    }

    public static final AdvancedGuild getAdvancedGuild(String guild_id) {
        if (guild_id == null) {
            return null;
        }
        AdvancedGuild advancedGuild = GUILDS.stream().filter((advancedGuild_) -> guild_id.equals(advancedGuild_.getGuildId())).findFirst().orElse(null);
        if (advancedGuild == null) {
            advancedGuild = new AdvancedGuild(guild_id);
            advancedGuild.getSettings().loadSettings();
            try {
                final AdvancedFile permissions = advancedGuild.getFile(Standard.STANDARD_PERMISSIONS_FILE_NAME);
                permissions.createAdvancedFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            GUILDS.add(advancedGuild);
        }
        return advancedGuild;
    }

    public static final Settings getGuildSettings(Guild guild) {
        if (guild == null) {
            return STANDARD_NULL_SETTINGS;
        }
        return getGuildSettings(guild.getId());
    }

    public static final Settings getGuildSettings(String guild_id) {
        if (guild_id == null) {
            return STANDARD_NULL_SETTINGS;
        }
        final AdvancedGuild advancedGuild = getAdvancedGuild(guild_id);
        if (advancedGuild == null) {
            return STANDARD_NULL_SETTINGS;
        }
        return advancedGuild.getSettings();
    }

    public static final Member getSelfMemberByGuild(Guild guild) {
        if (guild == null) {
            return null;
        }
        return getSelfMemberByGuild(guild.getId());
    }

    public static final Member getSelfMemberByGuild(String guild_id) {
        final Guild guild = getGuildById(guild_id);
        if (guild != null) {
            try {
                final SelfUser user = getJDA().getSelfUser();
                if (user != null) {
                    return guild.getMemberById(user.getId());
                } else {
                    System.err.println("Failed to get SelfMember by Guild: SelfUser is null!");
                    return null;
                }
            } catch (Exception ex) {
                System.err.print("Failed to get SelfMember by Guild: ");
                ex.printStackTrace();
                return null;
            }
        } else {
            System.err.println("Failed to get SelfMember by Guild: Guild is null!");
            return null;
        }
    }

    public static final User getSelfUser() {
        if (getJDA() == null) {
            return null;
        }
        return getJDA().getSelfUser();
    }

    //________________________________________________________________________//
    public static final String getCommandPrefixByGuild(Guild guild) {
        if (guild == null) {
            return getStandardCommandPrefix();
        }
        return getCommandPrefixByGuild(guild.getId());
    }

    public static final String getCommandPrefixByGuild(String guild_id) {
        if (guild_id == null) {
            return getStandardCommandPrefix();
        }
        return getGuildSettings(guild_id).getProperty("command_prefix", getStandardCommandPrefix());
    }

    public static final boolean setCommandPrefixForGuild(Guild guild, String commandPrefix) {
        if (guild == null) {
            return false;
        }
        return setCommandPrefixForGuild(guild.getId(), commandPrefix);
    }

    public static final boolean setCommandPrefixForGuild(String guild_id, String commandPrefix) {
        if (guild_id == null || commandPrefix == null || commandPrefix.isEmpty() || commandPrefix.contains("\\")) {
            return false;
        }
        getGuildSettings(guild_id).setProperty("command_prefix", commandPrefix);
        return true;
    }
    //------------------------------------------------------------------------//

    //________________________________________________________________________//
    public static final String getNicknameByGuild(Guild guild) {
        if (guild == null) {
            return getNickname();
        }
        return getNicknameByGuild(guild.getId());
    }

    public static final String getNicknameByGuild(String guild_id) {
        if (guild_id == null) {
            return getNickname();
        }
        return getGuildSettings(guild_id).getProperty("nickname", getNickname());
    }

    public static final boolean setNicknameForGuild(Guild guild, String nickname) {
        if (guild == null) {
            return false;
        }
        return setNicknameForGuild(guild.getId(), nickname);
    }

    public static final boolean setNicknameForGuild(String guild_id, String nickname) {
        if (guild_id == null || nickname == null || nickname.isEmpty()) {
            return false;
        }
        getGuildSettings(guild_id).setProperty("nickname", nickname);
        final Member member = getSelfMemberByGuild(guild_id);
        if (member != null) {
            if (!nickname.equals(member.getNickname())) {
                try {
                    getJDA().getGuildById(guild_id).getController().setNickname(member, nickname).queue();
                    System.out.println("Changed nickname for \"" + guild_id + "\" to \"" + nickname + "\".");
                    return true;
                } catch (Exception ex) {
                    System.err.println("Failed to change nickname for \"" + guild_id + "\" to \"" + nickname + "\"");
                    //ex.printStackTrace();
                    return false;
                }
            } else {
                //System.err.print("Failed to change nickname for \"" + guild_id + "\" to \"" + nickname + "\": Nickname is already set!");
                return false;
            }
        } else {
            System.err.println("Failed to change nickname for \"" + guild_id + "\" to \"" + nickname + "\": Member is null!");
            return false;
        }
    }
    //------------------------------------------------------------------------//

    public static final long getAutoDeleteCommandNotFoundMessageDelayByGuild(Guild guild) {
        if (guild == null) {
            return -1;
        }
        return getAutoDeleteCommandNotFoundMessageDelayByGuild(guild.getId());
    }

    public static final long getAutoDeleteCommandNotFoundMessageDelayByGuild(String guild_id) {
        if (guild_id == null) {
            return -1;
        }
        return getGuildSettings(guild_id).getProperty("autoDeleteCommandNotFoundMessageDelay", -1);
    }

    public static final boolean setAutoDeleteCommandNotFoundMessageDelayForGuild(Guild guild, long autoDeleteCommandNotFoundMessageDelay) {
        if (guild == null) {
            return false;
        }
        return setAutoDeleteCommandNotFoundMessageDelayForGuild(guild.getId(), autoDeleteCommandNotFoundMessageDelay);
    }

    public static final boolean setAutoDeleteCommandNotFoundMessageDelayForGuild(String guild_id, long autoDeleteCommandNotFoundMessageDelay) {
        if (guild_id == null) {
            return false;
        }
        getGuildSettings(guild_id).setProperty("autoDeleteCommandNotFoundMessageDelay", autoDeleteCommandNotFoundMessageDelay);
        return true;
    }

    public static final boolean isAutoDeletingCommandByGuild(Guild guild) {
        if (guild == null) {
            return false;
        }
        return isAutoDeletingCommandByGuild(guild.getId());
    }

    public static final boolean isAutoDeletingCommandByGuild(String guild_id) {
        if (guild_id == null) {
            return false;
        }
        return getGuildSettings(guild_id).getProperty("autoDeletingCommand", false);
    }

    public static final boolean setAutoDeletingCommandForGuild(Guild guild, boolean autoDeletingCommand) {
        if (guild == null) {
            return false;
        }
        return setAutoDeletingCommandForGuild(guild.getId(), autoDeletingCommand);
    }

    public static final boolean setAutoDeletingCommandForGuild(String guild_id, boolean autoDeletingCommand) {
        if (guild_id == null) {
            return false;
        }
        getGuildSettings(guild_id).setProperty("autoDeletingCommand", autoDeletingCommand);
        return true;
    }

    public static final boolean log(Instant timestamp, Guild guild, String log_name, String settings_log_channel_id, String settings_log_text, String standard_log_text, String settings_log_date_time_format, Object... args) {
        try {
            final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild);
            final String log_channel_id = advancedGuild.getSettings().getProperty(settings_log_channel_id, null);
            if (log_channel_id != null) {
                final TextChannel channel = guild.getTextChannelById(log_channel_id);
                if (channel != null) {
                    final String log_text = advancedGuild.getSettings().getProperty(settings_log_text, standard_log_text);
                    final String log_date_time_format = advancedGuild.getSettings().getProperty(settings_log_date_time_format, Standard.STANDARD_DATE_TIME_FORMAT);
                    String date_time_formatted = null;
                    try {
                        date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                    } catch (Exception ex) {
                        date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                    }
                    final ArrayList<Object> args_ = new ArrayList<>();
                    args_.add(date_time_formatted);
                    args_.add(log_name);
                    args_.addAll(Arrays.asList(args));
                    final String message = String.format(log_text, args_.toArray());
                    channel.sendMessage(message).queue();
                    Standard.addToFile(advancedGuild.getLogFile(), message);
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //****************************************************************//
    //*********************GUILD SPECIFIC STOP************************//
    //****************************************************************//
    //****************************************************************//
    //*********************PLUGIN SPECIFIC START**********************//
    //****************************************************************//
    public static final boolean loadPlugins() {
        STANDARD_PLUGIN_MANAGER.loadPlugins(STANDARD_PLUGINS_FOLDER);
        return false;
    }

    public static final boolean isPluginAdmin(Plugin plugin) {
        if (PLUGIN_PERMISSION_ADMIN_STRING == null) {
            return false;
        }
        return PLUGIN_PERMISSION_ADMIN_STRING.equals(plugin.getPermissionID());
    }

    //****************************************************************//
    //*********************PLUGIN SPECIFIC STOP***********************//
    //****************************************************************//
    public static final Message getNoPermissionMessage(User user, String extra) {
        return new MessageBuilder().append(String.format("%s Sorry %s, you don't have the permissions to use this %s!", Emoji.WARNING, user.getAsMention(), extra)).build();
    }

    public static final MessageBuilder getNoMessage(User user, String format, Object... args) {
        return new MessageBuilder().append(String.format("%s Sorry %s, %s", Emoji.WARNING, user.getAsMention(), String.format(format, args)));
    }

    public static final EmbedBuilder getMessageEmbed(Color color, String message) {
        return new EmbedBuilder().setColor(color).setDescription(message);
    }

    public static final EmbedBuilder getMessageEmbed(Color color, String format, Object... args) {
        return getMessageEmbed(color, String.format(format, args));
    }

    public static final boolean isSuperOwner(long user_id) {
        final String super_owner = STANDARD_SETTINGS.getProperty("super_owner", null);
        if (super_owner == null) {
            return true;
        }
        return super_owner.equals("" + user_id);
    }

    public static final boolean isSuperOwner(String user_id) {
        final String super_owner = STANDARD_SETTINGS.getProperty("super_owner", null);
        if (super_owner == null) {
            return true;
        }
        return super_owner.equals(user_id);
    }

    public static final boolean isSuperOwner(User user) {
        if (user == null) {
            return false;
        }
        return isSuperOwner(user.getIdLong());
    }

    public static final boolean isSuperOwner(Member member) {
        if (member == null) {
            return false;
        }
        return isSuperOwner(member.getUser());
    }

    public static final User getUserById(long user_id) {
        if (getJDA() == null) {
            System.err.println("Failed to get User by id: JDA is null!");
            return null;
        }
        try {
            return getJDA().getUserById(user_id);
        } catch (Exception ex) {
            return null;
        }
    }

    public static final User getUserById(String user_id) {
        if (getJDA() == null) {
            System.err.println("Failed to get User by id: JDA is null!");
            return null;
        }
        try {
            return getJDA().getUserById(user_id);
        } catch (Exception ex) {
            return null;
        }
    }

    public static final Guild getGuildById(long guild_id) {
        if (getJDA() == null) {
            System.err.println("Failed to get Guild by id: JDA is null!");
            return null;
        }
        try {
            return getJDA().getGuildById(guild_id);
        } catch (Exception ex) {
            return null;
        }
    }

    public static final Guild getGuildById(String guild_id) {
        if (getJDA() == null) {
            System.err.println("Failed to get Guild by id: JDA is null!");
            return null;
        }
        try {
            return getJDA().getGuildById(guild_id);
        } catch (Exception ex) {
            return null;
        }
    }

    public static final String resolveGuildId(Guild guild, String guild_id) {
        if (guild != null && guild_id != null && guild_id.equalsIgnoreCase("this")) {
            guild_id = guild.getId();
        }
        return guild_id;
    }

    public static final List<Member> muteAll(VoiceChannel voiceChannel, boolean mute) {
        try {
            final Member self_member = getSelfMemberByGuild(voiceChannel.getGuild());
            final List<Member> members = voiceChannel.getMembers().stream().filter((member) -> {
                return (member != self_member && !self_member.equals(member));
            }).collect(Collectors.toList());
            members.stream().forEach((member) -> {
                voiceChannel.getGuild().getController().setMute(member, mute).queue();
            });
            return members;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static final List<Member> muteAll(List<Member> members, boolean mute) {
        try {
            members.stream().filter((member) -> {
                final Member self_member = Standard.getSelfMemberByGuild(member.getGuild());
                return (member != self_member && !self_member.equals(member));
            }).forEach((member) -> {
                member.getGuild().getController().setMute(member, mute).queue();
            });
            return members;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public static final String DISCORD_STYLE_ITALICS = "*%s*";
    public static final String DISCORD_STYLE_BOLD = "**%s**";
    public static final String DISCORD_STYLE_UNDERLINE = "__%s__";
    public static final String DISCORD_STYLE_STRIKETHROUGH = "~~%s~~";
    public static final String DISCORD_STYLE_UNDERLINE_ITALICS = toUnderline(DISCORD_STYLE_ITALICS);
    public static final String DISCORD_STYLE_UNDERLINE_BOLD = toUnderline(DISCORD_STYLE_BOLD);
    public static final String DISCORD_STYLE_BOLD_ITALICS = toBold(DISCORD_STYLE_ITALICS);
    public static final String DISCORD_STYLE_UNDERLINE_BOLD_ITALICS = toUnderline(DISCORD_STYLE_BOLD_ITALICS);

    public static final String toItalics(String text) {
        return String.format(DISCORD_STYLE_ITALICS, text);
    }

    public static final String toBold(String text) {
        return String.format(DISCORD_STYLE_BOLD, text);
    }

    public static final String toUnderline(String text) {
        return String.format(DISCORD_STYLE_UNDERLINE, text);
    }

    public static final String toStrikethrough(String text) {
        return String.format(DISCORD_STYLE_STRIKETHROUGH, text);
    }

    public static final String toUnderlineItalics(String text) {
        return String.format(DISCORD_STYLE_UNDERLINE_ITALICS, text);
    }

    public static final String toUnderlineBold(String text) {
        return String.format(DISCORD_STYLE_UNDERLINE_BOLD, text);
    }

    public static final String toBoldItalics(String text) {
        return String.format(DISCORD_STYLE_BOLD_ITALICS, text);
    }

    public static final String toUnderlineBoldItalics(String text) {
        return String.format(DISCORD_STYLE_UNDERLINE_BOLD_ITALICS, text);
    }

    public static final String embedLink(String text, String link) {
        return String.format("[%s](%s)", text, link);
    }

    public static final boolean addToFile(AdvancedFile file, Object toAdd) {
        return addToFile(file, toAdd, true);
    }

    public static final boolean addToFile(AdvancedFile file, Object toAdd, boolean append) {
        return addToFile(file, toAdd, true, true);
    }

    public static final boolean addToFile(AdvancedFile file, Object toAdd, boolean append, boolean newLine) {
        if (file == null || (toAdd == null && append)) {
            return false;
        }
        if (toAdd == null) {
            if (file.exists()) {
                file.delete();
                return file.createAdvancedFile();
            }
            return true;
        } else {
            try {
                if (!file.createAdvancedFile() && !file.exists()) {
                    return false;
                }
                final BufferedWriter writer = new BufferedWriter(file.getWriter(append));
                writer.write(toAdd.toString());
                if (newLine) {
                    writer.newLine();
                }
                writer.flush();
                writer.close();
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public static final boolean runShutdownHooks() {
        try {
            SHUTDOWNHOOKS.stream().forEach((shutdownHook) -> {
                try {
                    shutdownHook.run();
                } catch (Exception ex) {
                }
            });
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final MessageReaction getReaction(Message message, String emoji) {
        if (message == null || emoji == null) {
            return null;
        }
        return message.getReactions().stream().filter((reaction) -> {
            return emoji.equals(reaction.getEmote().getName());
        }).findFirst().orElse(null);
    }

    public static final boolean isReacted(Message message, String emoji) {
        if (message == null || emoji == null) {
            return false;
        }
        final MessageReaction reaction = getReaction(message, emoji);
        if (reaction == null) {
            return false;
        }
        return reaction.getCount() > 1;
    }

    public static final boolean removeReaction(Message message, String emoji) {
        if (message == null || emoji == null) {
            return false;
        }
        final MessageReaction reaction = getReaction(message, emoji);
        if (reaction == null) {
            return true;
        }
        reaction.getUsers().stream().filter((user) -> !user.isBot()).forEach((user) -> {
            reaction.removeReaction(user).complete();
        });
        return true;
    }

    public static final Message getUpdatedMessage(Message message) {
        if (message == null) {
            return null;
        }
        try {
            return message.getGuild().getTextChannelById(message.getChannel().getId()).getMessageById(message.getId()).complete();
        } catch (Exception ex) {
            return null;
        }
    }

    public static final long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_SUPER_OWNER = (permissionRole, member) -> {
        return Standard.isSuperOwner(member);
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_OWNER = (permissionRole, member) -> {
        final PermissionRole owner = PermissionRole.getPermissionRoleByName("Owner");
        if (permissionRole.isPermissionGranted(owner)) {
            return true;
        }
        return Standard.isSuperOwner(member);
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_OWNER_BOT_COMMANDER = (permissionRole, member) -> {
        final PermissionRole owner = PermissionRole.getPermissionRoleByName("Owner");
        final PermissionRole bot_commander = PermissionRole.getPermissionRoleByName("Bot_Commander");
        if (permissionRole.isPermissionGranted(owner) || permissionRole.isPermissionGranted(bot_commander)) {
            return true;
        }
        return Standard.isSuperOwner(member);
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_OWNER_ADMIN = (permissionRole, member) -> {
        final PermissionRole owner = PermissionRole.getPermissionRoleByName("Owner");
        final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
        if (permissionRole.isPermissionGranted(owner) || permissionRole.isPermissionGranted(admin)) {
            return true;
        }
        return Standard.isSuperOwner(member);
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_ADMIN = (permissionRole, member) -> {
        final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
        if (permissionRole.isPermissionGranted(admin)) {
            return true;
        }
        return Standard.isSuperOwner(member);
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_ADMIN_BOT_COMMANDER = (permissionRole, member) -> {
        final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
        final PermissionRole bot_commander = PermissionRole.getPermissionRoleByName("Bot_Commander");
        if (permissionRole.isPermissionGranted(admin) || permissionRole.isPermissionGranted(bot_commander)) {
            return true;
        }
        return Standard.isSuperOwner(member);
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_BOT_COMMANDER = (permissionRole, member) -> {
        final PermissionRole bot_commander = PermissionRole.getPermissionRoleByName("Bot_Commander");
        if (permissionRole.isPermissionGranted(bot_commander)) {
            return true;
        }
        return Standard.isSuperOwner(member);
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_MODERATOR = (permissionRole, member) -> {
        final PermissionRole admin = PermissionRole.getPermissionRoleByName("Moderator");
        if (permissionRole.isPermissionGranted(admin)) {
            return true;
        }
        return Standard.isSuperOwner(member);
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_VIP = (permissionRole, member) -> {
        final PermissionRole admin = PermissionRole.getPermissionRoleByName("VIP");
        if (permissionRole.isPermissionGranted(admin)) {
            return true;
        }
        return Standard.isSuperOwner(member);
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_NOBODY = (permissionRole, member) -> {
        return false;
    };

    public static final PermissionRoleFilter STANDARD_PERMISSIONROLEFILTER_EVERYONE = (permissionRole, member) -> {
        return true;
    };

    public static final CommandCategory COMMANDCATEGORY_NONE = new CommandCategory(null, "None", ":children_crossing:");
    public static final CommandCategory COMMANDCATEGORY_FORBIDDEN = new CommandCategory(null, "Forbidden", ":no_entry:");
    public static final CommandCategory COMMANDCATEGORY_ALL = new CommandCategory(null, "All", ":ledger:");
    public static final CommandCategory COMMANDCATEGORY_NORMAL = new CommandCategory(COMMANDCATEGORY_ALL, "Normal", Emoji.INFORMATION);
    public static final CommandCategory COMMANDCATEGORY_FUN = new CommandCategory(COMMANDCATEGORY_ALL, "Fun", Emoji.ROLL);
    public static final CommandCategory COMMANDCATEGORY_MODERATION = new CommandCategory(COMMANDCATEGORY_ALL, "Moderation", ":level_slider:");
    public static final CommandCategory COMMANDCATEGORY_MODERATION_UTIL = new CommandCategory(COMMANDCATEGORY_MODERATION, "Util", ":gear:");
    public static final CommandCategory COMMANDCATEGORY_TEST = new CommandCategory(COMMANDCATEGORY_ALL, "Test", Emoji.WARNING);
    public static final CommandCategory COMMANDCATEGORY_SECRET = new CommandCategory(COMMANDCATEGORY_ALL, "Secret", Emoji.SPY);

    public static final CommandCategory getCommandCategory(CommandCategory commandCategory) {
        return commandCategory == null ? COMMANDCATEGORY_NONE : commandCategory;
    }

    public static final String[] ULTRA_FORBIDDEN = new String[]{"token", "super_owner", "nickname", PLUGIN_PERMISSION_ADMIN_STRING_NAME, "sql_hostname", "sql_database", "sql_username", "sql_password"}; //FIXME Make nickname forbidden or super_forbidden, but not ultra_forbidden!

    public static final String[] STANDARD_ARGUMENT_PREFIXES = new String[]{"-", "/", "!"};
    public static final Argument ARGUMENT_GLOBAL = new Argument("global", STANDARD_ARGUMENT_PREFIXES, "g");
    public static final Argument ARGUMENT_DIRECT = new Argument("direct", STANDARD_ARGUMENT_PREFIXES, "d");
    public static final Argument ARGUMENT_PRIVATE = new Argument("private", STANDARD_ARGUMENT_PREFIXES, "p");
    public static final Argument ARGUMENT_THIS = new Argument("this", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_ALL = new Argument("all", STANDARD_ARGUMENT_PREFIXES, "a");
    public static final Argument ARGUMENT_END = new Argument("end", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_SETTINGS = new Argument("settings", STANDARD_ARGUMENT_PREFIXES, "setts");
    public static final Argument ARGUMENT_GUILD_SETTINGS = new Argument("guild_settings", STANDARD_ARGUMENT_PREFIXES, "gsetts");
    public static final Argument ARGUMENT_SET = new Argument("set", STANDARD_ARGUMENT_PREFIXES, "s");
    public static final Argument ARGUMENT_GET = new Argument("get", STANDARD_ARGUMENT_PREFIXES, "g");
    public static final Argument ARGUMENT_REMOVE = new Argument("remove", STANDARD_ARGUMENT_PREFIXES, "r");
    public static final Argument ARGUMENT_LIST = new Argument("list", STANDARD_ARGUMENT_PREFIXES, "l");
    public static final Argument ARGUMENT_DOWNLOAD = new Argument("download", STANDARD_ARGUMENT_PREFIXES, "dl");
    public static final Argument ARGUMENT_DEFAULT = new Argument("default", STANDARD_ARGUMENT_PREFIXES, "df");
    public static final Argument ARGUMENT_PERMISSIONS = new Argument("permissions", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_OVERRIDE = new Argument("override", STANDARD_ARGUMENT_PREFIXES, "o");
    public static final Argument ARGUMENT_START = new Argument("start", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_SAVE = new Argument("save", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_STOP = new Argument("stop", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_CREATE = new Argument("create", STANDARD_ARGUMENT_PREFIXES, "c");
    public static final Argument ARGUMENT_TOGGLE = new Argument("toggle", STANDARD_ARGUMENT_PREFIXES, "t");
    public static final Argument ARGUMENT_TOGGLE_ALL = new Argument("toggleall", STANDARD_ARGUMENT_PREFIXES, "ta");
    public static final Argument ARGUMENT_GO = new Argument("go", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_UP = new Argument("up", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_DOWN = new Argument("down", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_EDIT = new Argument("edit", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_INFO = new Argument("info", STANDARD_ARGUMENT_PREFIXES, "i", "now");
    public static final Argument ARGUMENT_MESSAGE = new Argument("message", STANDARD_ARGUMENT_PREFIXES, "msg");
    public static final Argument ARGUMENT_SERVER = new Argument("server", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_PLAY = new Argument("play", STANDARD_ARGUMENT_PREFIXES, "p", "pl", "py");
    public static final Argument ARGUMENT_PAUSE = new Argument("pause", STANDARD_ARGUMENT_PREFIXES, "ps");
    public static final Argument ARGUMENT_SKIP = new Argument("skip", STANDARD_ARGUMENT_PREFIXES, "s");
    public static final Argument ARGUMENT_SHUFFLE = new Argument("shuffle", STANDARD_ARGUMENT_PREFIXES);
    public static final Argument ARGUMENT_LOOP = new Argument("loop", STANDARD_ARGUMENT_PREFIXES, "l");
    public static final Argument ARGUMENT_QUEUE = new Argument("queue", STANDARD_ARGUMENT_PREFIXES, "q");
    public static final Argument ARGUMENT_LIVE = new Argument("live", STANDARD_ARGUMENT_PREFIXES, "l");
    public static final Argument ARGUMENT_VOLUME = new Argument("volume", STANDARD_ARGUMENT_PREFIXES, "v");
    public static final Argument ARGUMENT_ID = new Argument("id", STANDARD_ARGUMENT_PREFIXES, "i");
    public static final Argument ARGUMENT_ASMENTION = new Argument("asMention", STANDARD_ARGUMENT_PREFIXES, "aM");
    public static final Argument ARGUMENT_HERE = new Argument("here", STANDARD_ARGUMENT_PREFIXES, "h");
    public static final Argument ARGUMENT_KICK = new Argument("kick", STANDARD_ARGUMENT_PREFIXES, "k");
    public static final Argument ARGUMENT_BAN = new Argument("ban", STANDARD_ARGUMENT_PREFIXES, "b");
    public static final Argument ARGUMENT_UNBAN = new Argument("unban", STANDARD_ARGUMENT_PREFIXES, "ub");

}
