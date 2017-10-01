package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.commands.impl.moderation.TempBan;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * GuildMemberLogger
 *
 * @author Panzer1119
 */
public class GuildMemberLogger extends ListenerAdapter {

    public static final String LOG_NAME = "Member";
    public static final String LOG_CHANNEL_ID_MEMBER = "log_channel_id_member";
    public static final String LOG_DATE_TIME_FORMAT = "log_date_time_format";
    public static final String LOG_MEMBER_ROLES_ASMENTION = "log_member_roles_asMention";
    public static final String LOG_TEXT_MEMBER_JOIN = "log_text_member_join";
    public static final String LOG_TEXT_MEMBER_JOIN_KICKED = "log_text_member_join_kicked";
    public static final String LOG_TEXT_MEMBER_LEAVE = "log_text_member_leave";
    public static final String LOG_TEXT_MEMBER_ROLE_ADD = "log_text_member_role_add";
    public static final String LOG_TEXT_MEMBER_ROLE_REMOVE = "log_text_member_role_remove";
    public static final String LOG_TEXT_MEMBER_NICK_CHANGE = "log_text_member_nick_change";

    @Override
    public final void onGuildMemberJoin(GuildMemberJoinEvent event) {
        final Instant timestamp = Instant.now();
        if (!TempBan.isAllowedToJoin(event.getMember())) {
            event.getGuild().getController().kick(event.getMember(), TempBan.getReason(event.getMember())).queue();
            onGuildMemberJoinKicked(timestamp, event);
            return;
        }
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_member = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_MEMBER, null);
        if (log_channel_id_member != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_member);
            if (channel != null) {
                final String log_text_member = advancedGuild.getSettings().getProperty(LOG_TEXT_MEMBER_JOIN, "[%1$s] [%2$s] %3$s joined this Guild");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_member, date_time_formatted, LOG_NAME, event.getMember().getAsMention());
                channel.sendMessage(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    private final void onGuildMemberJoinKicked(Instant timestamp, GuildMemberJoinEvent event) {
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_member = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_MEMBER, null);
        if (log_channel_id_member != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_member);
            if (channel != null) {
                final String log_text_member = advancedGuild.getSettings().getProperty(LOG_TEXT_MEMBER_JOIN_KICKED, "[%1$s] [%2$s] %3$s joined this Guild, but was directly kicked");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_member, date_time_formatted, LOG_NAME, event.getMember().getAsMention());
                channel.sendMessage(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_member = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_MEMBER, null);
        if (log_channel_id_member != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_member);
            if (channel != null) {
                final String log_text_member = advancedGuild.getSettings().getProperty(LOG_TEXT_MEMBER_LEAVE, "[%1$s] [%2$s] %3$s left this Guild");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_member, date_time_formatted, LOG_NAME, event.getMember().getAsMention());
                channel.sendMessage(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_member = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_MEMBER, null);
        if (log_channel_id_member != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_member);
            if (channel != null) {
                final String log_text_member = advancedGuild.getSettings().getProperty(LOG_TEXT_MEMBER_ROLE_ADD, "[%1$s] [%2$s] %3$s got added %4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final boolean log_member_roles_asMention = advancedGuild.getSettings().getProperty(LOG_MEMBER_ROLES_ASMENTION, false);
                final String message = String.format(log_text_member, date_time_formatted, LOG_NAME, event.getMember().getAsMention(), Util.rolesToString(event.getRoles(), log_member_roles_asMention));
                channel.sendMessage(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_member = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_MEMBER, null);
        if (log_channel_id_member != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_member);
            if (channel != null) {
                final String log_text_member = advancedGuild.getSettings().getProperty(LOG_TEXT_MEMBER_ROLE_REMOVE, "[%1$s] [%2$s] %3$s got removed %4$s");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final boolean log_member_roles_asMention = advancedGuild.getSettings().getProperty(LOG_MEMBER_ROLES_ASMENTION, false);
                final String message = String.format(log_text_member, date_time_formatted, LOG_NAME, event.getMember().getAsMention(), Util.rolesToString(event.getRoles(), log_member_roles_asMention));
                channel.sendMessage(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

    @Override
    public final void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final String log_channel_id_member = advancedGuild.getSettings().getProperty(LOG_CHANNEL_ID_MEMBER, null);
        if (log_channel_id_member != null) {
            final TextChannel channel = event.getGuild().getTextChannelById(log_channel_id_member);
            if (channel != null) {
                final String log_text_member = advancedGuild.getSettings().getProperty(LOG_TEXT_MEMBER_NICK_CHANGE, "[%1$s] [%2$s] %3$s changed his Nickname from \"%4$s\" to \"%5$s\"");
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted = null;
                try {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                final String message = String.format(log_text_member, date_time_formatted, LOG_NAME, event.getMember().getAsMention(), event.getPrevNick(), event.getNewNick());
                channel.sendMessage(message).queue();
                Standard.addToFile(advancedGuild.getLogFile(), message);
            }
        }
    }

}
