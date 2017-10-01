package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.util.Standard;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.core.entities.TextChannel;
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
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_JOIN, "[%1$s] [%2$s] %3$s joined #%4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), event.getChannelJoined().getName());
                channel.sendMessage(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_MOVE, "[%1$s] [%2$s] %3$s moved from #%4$s to #%5$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), event.getChannelLeft().getName(), event.getChannelJoined().getName());
                channel.sendMessageFormat(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_LEAVE, "[%1$s] [%2$s] %3$s left #%4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), event.getChannelLeft().getName());
                channel.sendMessageFormat(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        if (!LOG_MUTES) {
            return;
        }
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_MUTE, "[%1$s] [%2$s] %3$s was %4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), (event.isMuted() ? "muted" : "unmuted"));
                channel.sendMessageFormat(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {
        if (!LOG_DEAFENS) {
            return;
        }
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_DEAFEN, "[%1$s] [%2$s] %3$s was %4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), (event.isDeafened() ? "deafened" : "undeafened"));
                channel.sendMessageFormat(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_GUILD_MUTE, "[%1$s] [%2$s] %3$s was guild %4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), (event.isGuildMuted() ? "muted" : "unmuted"));
                channel.sendMessageFormat(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_GUILD_DEAFEN, "[%1$s] [%2$s] %3$s was guild %4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), (event.isGuildDeafened() ? "deafened" : "undeafened"));
                channel.sendMessageFormat(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildVoiceSelfMute(GuildVoiceSelfMuteEvent event) {
        if (!LOG_MUTES) {
            return;
        }
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_SELF_MUTE, "[%1$s] [%2$s] %3$s was self %4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), (event.isSelfMuted() ? "muted" : "unmuted"));
                channel.sendMessageFormat(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildVoiceSelfDeafen(GuildVoiceSelfDeafenEvent event) {
        if (!LOG_DEAFENS) {
            return;
        }
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_SELF_DEAFEN, "[%1$s] [%2$s] %3$s was self %4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), (event.isSelfDeafened() ? "deafened" : "undeafened"));
                channel.sendMessageFormat(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildVoiceSuppress(GuildVoiceSuppressEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_voice = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_VOICE, null);
        if (log_channel_id_voice != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_voice);
            if (channel != null) {
                final String log_text_voice = advancedGuild.getSettings().getProperty(LOG_TEXT_VOICE_SUPPRESS, "[%1$s] [%2$s] %3$s was %4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_voice, date_time_formatted, LOG_NAME, event.getVoiceState().getMember().getAsMention(), (event.isSuppressed() ? "suppressed" : "unsuppressed"));
                channel.sendMessageFormat(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

}
