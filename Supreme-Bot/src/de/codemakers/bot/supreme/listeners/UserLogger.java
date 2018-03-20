package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.settings.Config;
import de.codemakers.bot.supreme.util.Standard;
import java.time.Instant;
import net.dv8tion.jda.core.events.user.UserAvatarUpdateEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * UserLogger
 *
 * @author Panzer1119
 */
public class UserLogger extends ListenerAdapter {

    public static final String LOG_NAME = "User";
    public static final String LOG_CHANNEL_ID_USER = "log_channel_id_user";
    public static final String LOG_DATE_TIME_FORMAT = "log_date_time_format";
    public static final String LOG_TEXT_USER_ONLINE_STATUS_UPDATE = "log_text_user_online_status_update";
    public static final String LOG_TEXT_USER_GAME_UPDATE = "log_text_user_game_update";

    @Override
    public final void onUserNameUpdate(UserNameUpdateEvent event) {
    }

    @Override
    public final void onUserAvatarUpdate(UserAvatarUpdateEvent event) {
    }

    @Override
    public final void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
        if (!event.isRelationshipUpdate()) {
            final Instant timestamp = Instant.now();
            Standard.log(timestamp, event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_USER, LOG_TEXT_USER_ONLINE_STATUS_UPDATE, "[%1$s] [%2$s] %3$s changed online status from %4$s to %5$s", LOG_DATE_TIME_FORMAT, Config.CONFIG.getUserNameForUser(event.getUser(), event.getGuild(), true), event.getPreviousOnlineStatus(), event.getCurrentOnlineStatus());
        }
    }

    @Override
    public final void onUserGameUpdate(UserGameUpdateEvent event) {
        if (!event.isRelationshipUpdate()) {
            final Instant timestamp = Instant.now();
            Standard.log(timestamp, event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_USER, LOG_TEXT_USER_GAME_UPDATE, "[%1$s] [%2$s] %3$s changed game from \"%4$s\" to \"%5$s\"", LOG_DATE_TIME_FORMAT, Config.CONFIG.getUserNameForUser(event.getUser(), event.getGuild(), true), event.getPreviousGame().getName(), event.getCurrentGame().getName());
        }
    }

    @Override
    public final void onUserTyping(UserTypingEvent event) {
    }

}
