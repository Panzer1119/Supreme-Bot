package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.settings.UserConfig;
import de.codemakers.bot.supreme.util.Standard;
import java.time.Instant;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSelfDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSelfMuteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSuppressEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * GuildVoiceLogger
 *
 * @author Panzer1119
 */
public class GuildVoiceLogger extends ListenerAdapter {

    public static final String LOG_NAME = "VoiceChannel";
    public static final String LOG_CHANNEL_ID_VOICE = "log_channel_id_voice";
    public static final String LOG_DATE_TIME_FORMAT = "log_date_time_format";
    public static final String LOG_TEXT_VOICE_JOIN = "log_text_voice_join";
    public static final String LOG_TEXT_VOICE_MOVE = "log_text_voice_move";
    public static final String LOG_TEXT_VOICE_LEAVE = "log_text_voice_leave";
    public static final String LOG_TEXT_VOICE_MUTE = "log_text_voice_mute";
    public static final String LOG_TEXT_VOICE_DEAFEN = "log_text_voice_deafen";
    public static final String LOG_TEXT_VOICE_GUILD_MUTE = "log_text_voice_guild_mute";
    public static final String LOG_TEXT_VOICE_GUILD_DEAFEN = "log_text_voice_guild_deafen";
    public static final String LOG_TEXT_VOICE_SELF_MUTE = "log_text_voice_self_mute";
    public static final String LOG_TEXT_VOICE_SELF_DEAFEN = "log_text_voice_self_deafen";
    public static final String LOG_TEXT_VOICE_SUPPRESS = "log_text_voice_suppress";

    private static boolean LOG_MUTES = false;
    private static boolean LOG_DEAFENS = false;

    @Override
    public final void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_JOIN, "[%1$s] [%2$s] %3$s joined #%4$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), event.getChannelJoined().getName());
    }

    @Override
    public final void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_MOVE, "[%1$s] [%2$s] %3$s moved from #%4$s to #%5$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), event.getChannelLeft().getName(), event.getChannelJoined().getName());
    }

    @Override
    public final void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_LEAVE, "[%1$s] [%2$s] %3$s left #%4$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), event.getChannelLeft().getName());
    }

    @Override
    public final void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        if (!LOG_MUTES) {
            return;
        }
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_MUTE, "[%1$s] [%2$s] %3$s was %4$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), (event.isMuted() ? "muted" : "unmuted"));
    }

    @Override
    public final void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {
        if (!LOG_DEAFENS) {
            return;
        }
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_DEAFEN, "[%1$s] [%2$s] %3$s was %4$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), (event.isDeafened() ? "deafened" : "undeafened"));
    }

    @Override
    public final void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event) {
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_GUILD_MUTE, "[%1$s] [%2$s] %3$s was guild %4$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), (event.isGuildMuted() ? "muted" : "unmuted"));
    }

    @Override
    public final void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) {
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_GUILD_DEAFEN, "[%1$s] [%2$s] %3$s was guild %4$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), (event.isGuildDeafened() ? "deafened" : "undeafened"));
    }

    @Override
    public final void onGuildVoiceSelfMute(GuildVoiceSelfMuteEvent event) {
        if (!LOG_MUTES) {
            return;
        }
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_SELF_MUTE, "[%1$s] [%2$s] %3$s was self %4$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), (event.isSelfMuted() ? "muted" : "unmuted"));
    }

    @Override
    public final void onGuildVoiceSelfDeafen(GuildVoiceSelfDeafenEvent event) {
        if (!LOG_DEAFENS) {
            return;
        }
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_SELF_DEAFEN, "[%1$s] [%2$s] %3$s was self %4$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), (event.isSelfDeafened() ? "deafened" : "undeafened"));
    }

    @Override
    public final void onGuildVoiceSuppress(GuildVoiceSuppressEvent event) {
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_VOICE, LOG_TEXT_VOICE_SUPPRESS, "[%1$s] [%2$s] %3$s was %4$s", LOG_DATE_TIME_FORMAT, UserConfig.USER_CONFIG.getNameForUser(event.getMember().getUser()), (event.isSuppressed() ? "suppressed" : "unsuppressed"));
    }

}
