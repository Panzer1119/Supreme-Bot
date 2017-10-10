package de.codemakers.bot.supreme.settings;

import com.vdurmont.emoji.EmojiParser;
import de.codemakers.bot.supreme.sql.LocalConfigData;
import de.codemakers.bot.supreme.util.Standard;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * LocalConfig
 *
 * @author Panzer1119
 */
public class LocalConfig extends AbstractLocalConfig {

    public static final LocalConfig LOCAL_CONFIG = new LocalConfig();

    public static final long CONFIG_ID = 0;
    public static final String KEY_GUILD_NICKNAME = "nickname";
    public static final String KEY_GUILD_COMMAND_PREFIX = "command_prefix";
    public static final String KEY_GUILD_REACT_ON_MENTION = "react_on_mention";
    public static final String KEY_GUILD_AUTO_DELETE_COMMAND_NOT_FOUND_MESSAGE_DELAY = "auto_delete_command_not_found_message_delay";
    public static final String KEY_GUILD_AUTO_DELETE_COMMAND = "auto_delete_command";
    public static final String KEY_SEND_HELP_ALWAYS_PRIVATE = "send_help_always_private";
    public static final String KEY_USER_LANGUAGE = "lang";
    public static final String KEY_USER_NOT_MENTIONED_IN_LOGS = "not_mentioned_in_logs";

    public LocalConfig() {
        super(CONFIG_ID);
    }

    public final String getNickname(long guild_id) {
        return getValue(guild_id, KEY_GUILD_NICKNAME, () -> GlobalConfig.GLOBAL_CONFIG.getNickname(), false);
    }

    public final LocalConfig setNickname(long guild_id, String nickname) {
        setValue(guild_id, KEY_GUILD_NICKNAME, nickname, false);
        return this;
    }

    public final String getCommandPrefix(long guild_id) {
        return getValue(guild_id, KEY_GUILD_COMMAND_PREFIX, () -> GlobalConfig.GLOBAL_CONFIG.getStandardCommandPrefix(), false);
    }

    public final LocalConfig setCommandPrefix(long guild_id, String command_prefix) {
        setValue(guild_id, KEY_GUILD_COMMAND_PREFIX, command_prefix, false);
        return this;
    }

    public final String getReactionOnMention(long guild_id) {
        String reaction = getValue(guild_id, KEY_GUILD_REACT_ON_MENTION, null, false);
        if (reaction != null && reaction.startsWith(":") && reaction.endsWith(":")) {
            reaction = EmojiParser.parseToUnicode(reaction);
        }
        return reaction;
    }

    public final LocalConfig setReactionOnMention(long guild_id, String reaction) {
        setValue(guild_id, KEY_GUILD_REACT_ON_MENTION, reaction, false);
        return this;
    }

    public final long getAutoDeleteCommandNotFoundMessageDelay(long guild_id) {
        return getValue(guild_id, KEY_GUILD_AUTO_DELETE_COMMAND_NOT_FOUND_MESSAGE_DELAY, -1, false);
    }

    public final LocalConfig setAutoDeleteCommandNotFoundMessageDelay(long guild_id, long auto_delete_command_not_found_message_delay) {
        setValue(guild_id, KEY_GUILD_AUTO_DELETE_COMMAND_NOT_FOUND_MESSAGE_DELAY, auto_delete_command_not_found_message_delay, false);
        return this;
    }

    public final boolean isAutoDeletingCommand(long guild_id) {
        return getValue(guild_id, KEY_GUILD_AUTO_DELETE_COMMAND, false, false);
    }

    public final LocalConfig setAutoDeletingCommand(long guild_id, boolean auto_delete_command) {
        setValue(guild_id, KEY_GUILD_AUTO_DELETE_COMMAND, auto_delete_command, false);
        return this;
    }

    public final boolean isSendingHelpAlwaysPrivate(long guild_id) {
        return getValue(guild_id, KEY_SEND_HELP_ALWAYS_PRIVATE, false, false);
    }

    public final LocalConfig setSendingHelpAlwaysPrivate(long guild_id, boolean send_help_always_private) {
        setValue(guild_id, KEY_SEND_HELP_ALWAYS_PRIVATE, send_help_always_private, false);
        return this;
    }

    public final String getLanguage(long user_id) {
        return getValue(user_id, KEY_USER_LANGUAGE, true);
    }

    public final LocalConfig setLanguage(long user_id, String lang) {
        setValue(user_id, KEY_USER_LANGUAGE, lang, true);
        return this;
    }

    public final boolean isNotMentionedInLogs(long user_id) {
        return getValue(user_id, KEY_USER_NOT_MENTIONED_IN_LOGS, false, true);
    }

    public final LocalConfig setNotMentionedInLogs(long user_id, boolean not_mentioned_in_logs) {
        setValue(user_id, KEY_USER_NOT_MENTIONED_IN_LOGS, not_mentioned_in_logs, true);
        return this;
    }

    public final String getNameForUser(User user) {
        if (user == null) {
            return null;
        }
        return isNotMentionedInLogs(user.getIdLong()) ? Standard.getCompleteName(user) : user.getAsMention();
    }

    public final List<Long> getUsersThatAreNotMentionedInLogs() {
        final List<LocalConfigData> localConfigDatas = getLocalConfigDatasByKey(KEY_USER_NOT_MENTIONED_IN_LOGS);
        return localConfigDatas == null ? new ArrayList<>() : localConfigDatas.stream().filter((localConfigData) -> (localConfigData.isUserConfig && localConfigData.value != null && localConfigData.value.equalsIgnoreCase("true"))).map((localConfigData) -> localConfigData.id).collect(Collectors.toList());
    }

    public final List<Member> getMembersThatAreNotMentionedInLogs(Guild guild) {
        if (guild == null) {
            return new ArrayList<>();
        }
        return getUsersThatAreNotMentionedInLogs().stream().map((id) -> guild.getMemberById(id)).collect(Collectors.toList());
    }

}
