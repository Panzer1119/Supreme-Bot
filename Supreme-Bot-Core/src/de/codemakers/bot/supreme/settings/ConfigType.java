package de.codemakers.bot.supreme.settings;

/**
 * ConfigType
 *
 * @author Panzer1119
 */
public enum ConfigType {
    BOT_CONFIG(false, false),
    GUILD_CONFIG(true, false),
    GUILD_USER_CONFIG(true, true),
    USER_CONFIG(false, true);

    private final boolean hasGuild;
    private final boolean hasUser;

    private ConfigType(boolean hasGuild, boolean hasUser) {
        this.hasGuild = hasGuild;
        this.hasUser = hasUser;
    }

    public final boolean hasGuild() {
        return hasGuild;
    }

    public final boolean hasUser() {
        return hasUser;
    }

    public static final ConfigType of(boolean hasGuild, boolean hasUser) {
        for (ConfigType configType : values()) {
            if (configType.hasGuild == hasGuild && configType.hasUser == hasUser) {
                return configType;
            }
        }
        return null;
    }

    public static final ConfigType of(long guild_id, long user_id) {
        return of(guild_id > 0, user_id > 0);
    }
}
