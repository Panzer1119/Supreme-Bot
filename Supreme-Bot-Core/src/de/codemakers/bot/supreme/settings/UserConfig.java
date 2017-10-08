package de.codemakers.bot.supreme.settings;

import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.entities.User;

/**
 * UserConfig
 *
 * @author Panzer1119
 */
public class UserConfig extends AbstractUserConfig {

    public static final UserConfig USER_CONFIG = new UserConfig();

    public static final long CONFIG_ID = 0;
    public static final String KEY_LANGUAGE = "lang";
    public static final String KEY_NOT_MENTIONED_IN_LOGS = "not_mentioned_in_logs";

    public UserConfig() {
        super(CONFIG_ID);
    }

    public final String getLanguage(long user_id) {
        return getValue(user_id, KEY_LANGUAGE);
    }

    public final UserConfig setLanguage(long user_id, String lang) {
        setValue(user_id, KEY_LANGUAGE, lang);
        return this;
    }

    public final boolean isNotMentionedInLogs(long user_id) {
        return getValue(user_id, KEY_NOT_MENTIONED_IN_LOGS, false);
    }

    public final UserConfig setNotMentionedInLogs(long user_id, boolean not_mentioned_in_logs) {
        setValue(user_id, KEY_NOT_MENTIONED_IN_LOGS, not_mentioned_in_logs);
        return this;
    }

    public final String getNameForUser(User user) {
        if (user == null) {
            return null;
        }
        return isNotMentionedInLogs(user.getIdLong()) ? Standard.getCompleteName(user) : user.getAsMention();
    }

}
