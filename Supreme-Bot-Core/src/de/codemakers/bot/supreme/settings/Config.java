package de.codemakers.bot.supreme.settings;

import com.vdurmont.emoji.EmojiParser;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.sql.ConfigData;
import de.codemakers.bot.supreme.util.Standard;
import java.time.Instant;
import java.util.regex.Matcher;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

/**
 * Config
 *
 * @author Panzer1119
 */
public class Config extends AbstractConfig {

    public static final Config CONFIG = new Config();

    public static final long CONFIG_ID = 0;
    //BOT SETTINGS
    public static final String KEY_BOT_NICKNAME = "nickname";
    public static final String KEY_BOT_STANDARD_COMMAND_PREFIX = "standard_command_prefix";
    public static final String KEY_BOT_LOG_FILE_SHOW_COUNT = "log_file_show_count";
    public static final String KEY_BOT_LONGEST_UPTIME = "longest_uptime";
    public static final String KEY_BOT_LONGEST_UPTIME_START = "longest_uptime_start";
    //GUILD SETTINGS
    public static final String KEY_GUILD_NICKNAME = "nickname";
    public static final String KEY_GUILD_COMMAND_PREFIX = "command_prefix";
    public static final String KEY_GUILD_LANGUAGE = "language";
    public static final String KEY_GUILD_REACT_ON_MENTION = "react_on_mention";
    public static final String KEY_GUILD_AUTO_DELETE_COMMAND_NOT_FOUND_MESSAGE_DELAY = "auto_delete_command_not_found_message_delay";
    public static final String KEY_GUILD_AUTO_DELETE_COMMAND = "auto_delete_command";
    public static final String KEY_GUILD_SEND_HELP_ALWAYS_PRIVATE = "send_help_always_private";
    //GUILD USER SETTINGS
    public static final String KEY_GUILD_USER_NOT_MENTIONED_IN_LOGS = "not_mentioned_in_logs";
    //USER SETTINGS
    public static final String KEY_USER_LANGUAGE = "language";
    public static final String KEY_USER_NOT_MENTIONED_IN_LOGS = "not_mentioned_in_logs";

    public Config() {
        super(CONFIG_ID);
    }

    //BOT SETTINGS
    public final String getBotNickname() {
        return getValue(0, 0, KEY_BOT_NICKNAME, () -> Standard.STANDARD_NAME);
    }

    public final Config setBotNickname(String nickname) {
        setValue(0, 0, KEY_BOT_NICKNAME, nickname);
        return this;
    }

    public final String getBotCommandPrefix() {
        return getValue(0, 0, KEY_BOT_STANDARD_COMMAND_PREFIX, () -> Standard.STANDARD_COMMAND_PREFIX);
    }

    public final Config setBotCommandPrefix(String standard_command_prefix) {
        setValue(0, 0, KEY_BOT_STANDARD_COMMAND_PREFIX, standard_command_prefix);
        return this;
    }

    public final int getBotLogFileShowCount() {
        return getValue(0, 0, KEY_BOT_LOG_FILE_SHOW_COUNT, 25);
    }

    public final Config setBotLogFileShowCount(int log_file_show_count) {
        setValue(0, 0, KEY_BOT_LOG_FILE_SHOW_COUNT, log_file_show_count);
        return this;
    }

    /**
     * Returns the longest uptime in milliseconds
     *
     * @return Longest uptime milliseconds
     */
    public final long getLongestUptime() {
        return getValue(0, 0, KEY_BOT_LONGEST_UPTIME, 0);
    }

    /**
     * Sets a new longest uptime in milliseconds
     *
     * @param uptime New longest uptime in milliseconds
     */
    public final Config setLongestUptime(long uptime) {
        if (getLongestUptime() >= uptime) {
            return this;
        }
        setValue(0, 0, KEY_BOT_LONGEST_UPTIME, uptime);
        return this;
    }

    public final Instant getLongestUptimeStart(long uptime) {
        return Instant.ofEpochMilli(getValue(0, 0, KEY_BOT_LONGEST_UPTIME_START, Instant.now().toEpochMilli() - getLongestUptime() + uptime));
    }

    public final Config setLongestUptimeStart(Instant timestamp) {
        if (timestamp == null) {
            final ConfigData configData = getConfigData(0, 0, KEY_BOT_LONGEST_UPTIME_START);
            if (configData != null) {
                configData.delete();
            }
            return this;
        }
        setValue(0, 0, KEY_BOT_LONGEST_UPTIME_START, timestamp.toEpochMilli());
        return this;
    }

    //GUILD SETTINGS
    public final String getGuildNickname(long guild_id) {
        return getValue(guild_id, 0, KEY_GUILD_NICKNAME, () -> getBotNickname());
    }

    public final Config setGuildNickname(long guild_id, String nickname) {
        setValue(guild_id, 0, KEY_GUILD_NICKNAME, nickname);
        return this;
    }

    public final String getGuildCommandPrefix(long guild_id) {
        return getValue(guild_id, 0, KEY_GUILD_COMMAND_PREFIX, () -> getBotCommandPrefix());
    }

    public final Config setGuildCommandPrefix(long guild_id, String command_prefix) {
        setValue(guild_id, 0, KEY_GUILD_COMMAND_PREFIX, command_prefix);
        return this;
    }

    public final String getGuildLanguage(long guild_id) {
        return getValue(guild_id, 0, KEY_GUILD_LANGUAGE);
    }

    public final Config setGuildLanguage(long guild_id, String lang) {
        setValue(guild_id, 0, KEY_GUILD_LANGUAGE, lang);
        return this;
    }

    public final boolean isGuildReactionEmote(long guild_id) {
        final String reaction = getValue(guild_id, 0, KEY_GUILD_REACT_ON_MENTION, null);
        if (reaction == null) {
            return false;
        }
        return ArgumentList.PATTERN_MARKDOWN_CUSTOM_EMOJI.matcher(reaction).matches();
    }

    public final String getGuildReactionOnMention(long guild_id) {
        String reaction = getValue(guild_id, 0, KEY_GUILD_REACT_ON_MENTION, null);
        if (reaction != null) {
            reaction = EmojiParser.parseToUnicode((reaction.startsWith(":") ? "" : ":") + reaction + (reaction.endsWith(":") ? "" : ":"));
        }
        return reaction;
    }

    public final Emote getGuildReactionOnMention(Guild guild) {
        final String reaction = getValue(guild.getIdLong(), 0, KEY_GUILD_REACT_ON_MENTION, null);
        if (reaction == null) {
            return null;
        }
        final Matcher matcher = ArgumentList.PATTERN_MARKDOWN_CUSTOM_EMOJI.matcher(reaction);
        if (!matcher.matches()) {
            return null;
        }
        return guild.getEmoteById(matcher.group(2));
    }

    public final Config setGuildReactionOnMention(long guild_id, String reaction) {
        if (reaction == null) {
            final ConfigData configData = getConfigData(guild_id, 0, KEY_GUILD_REACT_ON_MENTION);
            if (configData != null) {
                configData.delete();
            }
            return this;
        }
        if (!reaction.startsWith(":") && !reaction.endsWith(":")) {
            reaction = EmojiParser.parseToAliases(reaction);
        }
        setValue(guild_id, 0, KEY_GUILD_REACT_ON_MENTION, reaction);
        return this;
    }

    public final long getGuildAutoDeleteCommandNotFoundMessageDelay(long guild_id) {
        return getValue(guild_id, 0, KEY_GUILD_AUTO_DELETE_COMMAND_NOT_FOUND_MESSAGE_DELAY, -1);
    }

    public final Config setGuildAutoDeleteCommandNotFoundMessageDelay(long guild_id, long auto_delete_command_not_found_message_delay) {
        setValue(guild_id, 0, KEY_GUILD_AUTO_DELETE_COMMAND_NOT_FOUND_MESSAGE_DELAY, auto_delete_command_not_found_message_delay);
        return this;
    }

    public final boolean isGuildAutoDeletingCommand(long guild_id) {
        return getValue(guild_id, 0, KEY_GUILD_AUTO_DELETE_COMMAND, false);
    }

    public final Config setGuildAutoDeletingCommand(long guild_id, boolean auto_delete_command) {
        setValue(guild_id, 0, KEY_GUILD_AUTO_DELETE_COMMAND, auto_delete_command);
        return this;
    }

    public final boolean isGuildSendingHelpAlwaysPrivate(long guild_id) {
        return getValue(guild_id, 0, KEY_GUILD_SEND_HELP_ALWAYS_PRIVATE, false);
    }

    public final Config setGuildSendingHelpAlwaysPrivate(long guild_id, boolean send_help_always_private) {
        setValue(guild_id, 0, KEY_GUILD_SEND_HELP_ALWAYS_PRIVATE, send_help_always_private);
        return this;
    }

    //GUILD USER SETTINGS
    public final boolean isGuildUserNotMentionedInLogs(long guild_id, long user_id) {
        return getValue(guild_id, user_id, KEY_USER_NOT_MENTIONED_IN_LOGS, false);
    }

    public final Config setGuildUserNotMentionedInLogs(long guild_id, long user_id, boolean not_mentioned_in_logs) {
        setValue(guild_id, user_id, KEY_USER_NOT_MENTIONED_IN_LOGS, not_mentioned_in_logs);
        return this;
    }

    //USER SETTINGS
    public final String getUserLanguage(long user_id) {
        return getValue(0, user_id, KEY_USER_LANGUAGE);
    }

    public final Config setUserLanguage(long user_id, String lang) {
        setValue(0, user_id, KEY_USER_LANGUAGE, lang);
        return this;
    }

    public final boolean isUserNotMentionedInLogs(long user_id) {
        return getValue(0, user_id, KEY_USER_NOT_MENTIONED_IN_LOGS, false);
    }

    public final Config setUserNotMentionedInLogs(long user_id, boolean not_mentioned_in_logs) {
        setValue(0, user_id, KEY_USER_NOT_MENTIONED_IN_LOGS, not_mentioned_in_logs);
        return this;
    }

    //MIXED
    public final String getUserNameForUser(User user, Guild guild) {
        if (user == null) {
            return null;
        }
        return (isUserNotMentionedInLogs(user.getIdLong()) || (guild != null && isGuildUserNotMentionedInLogs(guild.getIdLong(), user.getIdLong()))) ? Standard.getCompleteName(user) : user.getAsMention();
    }

    public final String getRoleNameForRole(Role role) {
        if (role == null) {
            return null;
        }
        if (role.getGuild().getMembersWithRoles(role).stream().anyMatch((member) -> isGuildUserNotMentionedInLogs(role.getGuild().getIdLong(), member.getUser().getIdLong()) || isUserNotMentionedInLogs(member.getUser().getIdLong()))) {
            return Standard.getCompleteName(role);
        }
        return role.getAsMention();
    }
}
