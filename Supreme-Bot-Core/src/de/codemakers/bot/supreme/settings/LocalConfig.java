package de.codemakers.bot.supreme.settings;

import com.vdurmont.emoji.EmojiParser;
import de.codemakers.bot.supreme.util.Standard;
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

}
