package de.codemakers.bot.supreme.settings;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.sql.ConfigData;
import de.codemakers.bot.supreme.util.Emoji;
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
    public static final String KEY_BOT_MUSIC_FRAME_BUFFER_DURATION = "music_frame_buffer_duration";
    //GUILD SETTINGS
    public static final String KEY_GUILD_NICKNAME = "nickname";
    public static final String KEY_GUILD_COMMAND_PREFIX = "command_prefix";
    public static final String KEY_GUILD_LANGUAGE = "language";
    public static final String KEY_GUILD_REACTION_ON_MENTION = "reaction_on_mention";
    public static final String KEY_GUILD_AUTO_DELETE_COMMAND_NOT_FOUND_MESSAGE_DELAY = "auto_delete_command_not_found_message_delay";
    public static final String KEY_GUILD_AUTO_DELETE_COMMAND = "auto_delete_command";
    public static final String KEY_GUILD_SEND_HELP_ALWAYS_PRIVATE = "send_help_always_private";
    public static final String KEY_GUILD_MUSIC_MAX_TRACKS_PER_PAGE = "music_max_tracks_per_page";
    public static final String KEY_GUILD_SHOW_COMMAND_NOT_FOUND_MESSAGE = "show_command_not_found_message";
    public static final String KEY_GUILD_REACTION_ON_COMMAND_NOT_FOUND = "reaction_on_command_not_found";
    //GUILD USER SETTINGS
    public static final String KEY_GUILD_USER_NOT_MENTIONED_IN_LOGS = "not_mentioned_in_logs";
    //USER SETTINGS
    public static final String KEY_USER_LANGUAGE = "language";
    public static final String KEY_USER_NOT_MENTIONED_IN_LOGS = "not_mentioned_in_logs";
    public static final String KEY_USER_SHOW_COMMAND_NOT_FOUND_MESSAGE = "show_command_not_found_message";
    public static final String KEY_USER_REACTION_ON_COMMAND_NOT_FOUND = "reaction_on_command_not_found";

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

    public final int getBotMusicFrameBufferDuration() {
        return getValue(0, 0, KEY_BOT_MUSIC_FRAME_BUFFER_DURATION, 5000);
    }

    public final Config setBotMusicFrameBufferDuration(int music_frame_buffer_duration) {
        setValue(0, 0, KEY_BOT_MUSIC_FRAME_BUFFER_DURATION, music_frame_buffer_duration);
        return this;
    }

    /**
     * Returns the longest uptime in milliseconds
     *
     * @return Longest uptime milliseconds
     */
    public final long getLongestUptime() {
        return getValue(0, 0, KEY_BOT_LONGEST_UPTIME, 0L);
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

    public final boolean isGuildReactionOnMentionEmote(long guild_id) {
        final String reaction_on_mention = getValue(guild_id, 0, KEY_GUILD_REACTION_ON_MENTION, null);
        if (reaction_on_mention == null) {
            return false;
        }
        return ArgumentList.PATTERN_MARKDOWN_CUSTOM_EMOJI.matcher(reaction_on_mention).matches();
    }

    public final com.vdurmont.emoji.Emoji getGuildReactionOnMention(long guild_id) {
        String reaction_on_mention = getValue(guild_id, 0, KEY_GUILD_REACTION_ON_MENTION, null);
        final com.vdurmont.emoji.Emoji emoji = EmojiManager.getByUnicode(reaction_on_mention);
        return emoji != null ? emoji : EmojiManager.getForAlias(reaction_on_mention);
    }

    public final Emote getGuildReactionOnMention(Guild guild) {
        final String reaction_on_mention = getValue(guild.getIdLong(), 0, KEY_GUILD_REACTION_ON_MENTION, null);
        if (reaction_on_mention == null) {
            return null;
        }
        final Matcher matcher = ArgumentList.PATTERN_MARKDOWN_CUSTOM_EMOJI.matcher(reaction_on_mention);
        if (!matcher.matches()) {
            return null;
        }
        return guild.getEmoteById(matcher.group(2));
    }

    public final Config setGuildReactionOnMention(long guild_id, String reaction_on_mention) {
        if (reaction_on_mention == null) {
            final ConfigData configData = getConfigData(guild_id, 0, KEY_GUILD_REACTION_ON_MENTION);
            if (configData != null) {
                configData.delete();
            }
            return this;
        }
        if (!reaction_on_mention.startsWith(":") && !reaction_on_mention.endsWith(":")) {
            reaction_on_mention = EmojiParser.parseToAliases(reaction_on_mention);
        }
        setValue(guild_id, 0, KEY_GUILD_REACTION_ON_MENTION, reaction_on_mention);
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

    public final int getGuildMusicMaxTracksPerPage(long guild_id) {
        return getValue(guild_id, 0, KEY_GUILD_MUSIC_MAX_TRACKS_PER_PAGE, 20);
    }

    public final Config setGuildMusicMaxTracksPerPage(long guild_id, int music_max_tracks_per_page) {
        setValue(guild_id, 0, KEY_GUILD_MUSIC_MAX_TRACKS_PER_PAGE, music_max_tracks_per_page);
        return this;
    }

    public final boolean isGuildShowingCommandNotFoundMessage(long guild_id) {
        return getValue(guild_id, 0, KEY_GUILD_SHOW_COMMAND_NOT_FOUND_MESSAGE, false);
    }

    public final Config setGuildShowingCommandNotFoundMessage(long guild_id, boolean show_command_not_found_message) {
        setValue(guild_id, 0, KEY_GUILD_SHOW_COMMAND_NOT_FOUND_MESSAGE, show_command_not_found_message);
        return this;
    }

    public final boolean isGuildReactionOnCommandNotFoundEmote(long guild_id) {
        final String reaction_on_command_not_found = getValue(guild_id, 0, KEY_GUILD_REACTION_ON_COMMAND_NOT_FOUND, () -> Emoji.QUESTION_MARK);
        if (reaction_on_command_not_found == null) {
            return false;
        }
        return ArgumentList.PATTERN_MARKDOWN_CUSTOM_EMOJI.matcher(reaction_on_command_not_found).matches();
    }

    public final com.vdurmont.emoji.Emoji getGuildReactionOnCommandNotFound(long guild_id) {
        String reaction_on_command_not_found = getValue(guild_id, 0, KEY_GUILD_REACTION_ON_COMMAND_NOT_FOUND, () -> Emoji.QUESTION_MARK);
        final com.vdurmont.emoji.Emoji emoji = EmojiManager.getByUnicode(reaction_on_command_not_found);
        return emoji != null ? emoji : EmojiManager.getForAlias(reaction_on_command_not_found);
    }

    public final Emote getGuildReactionOnCommandNotFound(Guild guild) {
        final String reaction_on_command_not_found = getValue(guild.getIdLong(), 0, KEY_GUILD_REACTION_ON_COMMAND_NOT_FOUND, () -> Emoji.QUESTION_MARK);
        if (reaction_on_command_not_found == null) {
            return null;
        }
        final Matcher matcher = ArgumentList.PATTERN_MARKDOWN_CUSTOM_EMOJI.matcher(reaction_on_command_not_found);
        if (!matcher.matches()) {
            return null;
        }
        return guild.getEmoteById(matcher.group(2));
    }

    public final Config setGuildReactionOnCommandNotFound(long guild_id, String reaction_on_command_not_found) {
        if (reaction_on_command_not_found == null) {
            final ConfigData configData = getConfigData(guild_id, 0, KEY_GUILD_REACTION_ON_COMMAND_NOT_FOUND);
            if (configData != null) {
                configData.delete();
            }
            return this;
        }
        if (!reaction_on_command_not_found.startsWith(":") && !reaction_on_command_not_found.endsWith(":")) {
            reaction_on_command_not_found = EmojiParser.parseToAliases(reaction_on_command_not_found);
        }
        setValue(guild_id, 0, KEY_GUILD_REACTION_ON_COMMAND_NOT_FOUND, reaction_on_command_not_found);
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

    public final boolean isUserShowingCommandNotFoundMessage(long user_id) {
        return getValue(0, user_id, KEY_GUILD_SHOW_COMMAND_NOT_FOUND_MESSAGE, false);
    }

    public final Config setUserShowingCommandNotFoundMessage(long user_id, boolean show_command_not_found_message) {
        setValue(0, user_id, KEY_GUILD_SHOW_COMMAND_NOT_FOUND_MESSAGE, show_command_not_found_message);
        return this;
    }

    public final com.vdurmont.emoji.Emoji getUserReactionOnCommandNotFound(long user_id) {
        String reaction_on_command_not_found = getValue(0, user_id, KEY_USER_REACTION_ON_COMMAND_NOT_FOUND, () -> Emoji.QUESTION_MARK);
        final com.vdurmont.emoji.Emoji emoji = EmojiManager.getByUnicode(reaction_on_command_not_found);
        return emoji != null ? emoji : EmojiManager.getForAlias(reaction_on_command_not_found);
    }

    public final Config setUserReactionOnCommandNotFound(long user_id, String reaction_on_command_not_found) {
        if (reaction_on_command_not_found == null) {
            final ConfigData configData = getConfigData(0, user_id, KEY_USER_REACTION_ON_COMMAND_NOT_FOUND);
            if (configData != null) {
                configData.delete();
            }
            return this;
        }
        if (!reaction_on_command_not_found.startsWith(":") && !reaction_on_command_not_found.endsWith(":")) {
            reaction_on_command_not_found = EmojiParser.parseToAliases(reaction_on_command_not_found);
        }
        setValue(0, user_id, KEY_USER_REACTION_ON_COMMAND_NOT_FOUND, reaction_on_command_not_found);
        return this;
    }

    //MIXED
    public final String getUserNameForUser(User user, Guild guild, boolean withId) {
        if (user == null) {
            return null;
        }
        return (isUserNotMentionedInLogs(user.getIdLong()) || (guild != null && isGuildUserNotMentionedInLogs(guild.getIdLong(), user.getIdLong()))) ? Standard.getCompleteName(user, withId) : user.getAsMention();
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
