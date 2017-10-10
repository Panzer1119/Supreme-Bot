package de.codemakers.bot.supreme.settings;

import de.codemakers.bot.supreme.util.Standard;

/**
 * GlobalConfig
 *
 * @author Panzer1119
 */
public class GlobalConfig extends AbstractGlobalConfig {

    public static final GlobalConfig GLOBAL_CONFIG = new GlobalConfig();

    public static final long CONFIG_ID = 0;
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_STANDARD_COMMAND_PREFIX = "standard_command_prefix";
    public static final String KEY_LOG_FILE_SHOW_COUNT = "log_file_show_count";

    public GlobalConfig() {
        super(CONFIG_ID);
    }

    public final String getNickname() {
        return getValue(KEY_NICKNAME, () -> Standard.NAME);
    }

    public final GlobalConfig setNickname(String nickname) {
        setValue(KEY_NICKNAME, nickname);
        return this;
    }

    public final String getStandardCommandPrefix() {
        return getValue(KEY_STANDARD_COMMAND_PREFIX, () -> "!");
    }

    public final GlobalConfig setStandardCommandPrefix(String standard_command_prefix) {
        setValue(KEY_STANDARD_COMMAND_PREFIX, standard_command_prefix);
        return this;
    }

    public final int getLogFileShowCount() {
        return getValue(KEY_LOG_FILE_SHOW_COUNT, 25);
    }

    public final GlobalConfig setLogFileShowCount(int log_file_show_count) {
        setValue(KEY_LOG_FILE_SHOW_COUNT, log_file_show_count);
        return this;
    }

}
