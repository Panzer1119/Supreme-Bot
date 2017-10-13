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
}
