package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.sql.entities.TempBan;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.settings.LocalConfig;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.time.Instant;
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
        Standard.log(timestamp, event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_MEMBER, LOG_TEXT_MEMBER_JOIN, "[%1$s] [%2$s] %3$s joined this Guild", LOG_DATE_TIME_FORMAT, LocalConfig.LOCAL_CONFIG.getNameForUser(event.getUser()));
    }

    private final void onGuildMemberJoinKicked(Instant timestamp, GuildMemberJoinEvent event) {
        Standard.log(timestamp, event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_MEMBER, LOG_TEXT_MEMBER_JOIN_KICKED, "[%1$s] [%2$s] %3$s joined this Guild, but was directly kicked", LOG_DATE_TIME_FORMAT, LocalConfig.LOCAL_CONFIG.getNameForUser(event.getUser()));
    }

    @Override
    public final void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_MEMBER, LOG_TEXT_MEMBER_LEAVE, "[%1$s] [%2$s] %3$s left this Guild", LOG_DATE_TIME_FORMAT, LocalConfig.LOCAL_CONFIG.getNameForUser(event.getUser()));
    }

    @Override
    public final void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        Standard.log(timestamp, event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_MEMBER, LOG_TEXT_MEMBER_ROLE_ADD, "[%1$s] [%2$s] %3$s got added %4$s", LOG_DATE_TIME_FORMAT, LocalConfig.LOCAL_CONFIG.getNameForUser(event.getUser()), Util.rolesToString(event.getRoles(), advancedGuild.getSettings().getProperty(LOG_MEMBER_ROLES_ASMENTION, false)));
    }

    @Override
    public final void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        final Instant timestamp = Instant.now();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        Standard.log(timestamp, event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_MEMBER, LOG_TEXT_MEMBER_ROLE_REMOVE, "[%1$s] [%2$s] %3$s got removed %4$s", LOG_DATE_TIME_FORMAT, LocalConfig.LOCAL_CONFIG.getNameForUser(event.getUser()), Util.rolesToString(event.getRoles(), advancedGuild.getSettings().getProperty(LOG_MEMBER_ROLES_ASMENTION, false)));
    }

    @Override
    public final void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
        Standard.log(Instant.now(), event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_MEMBER, LOG_TEXT_MEMBER_NICK_CHANGE, "[%1$s] [%2$s] %3$s changed his Nickname from \"%4$s\" to \"%5$s\"", LOG_DATE_TIME_FORMAT, LocalConfig.LOCAL_CONFIG.getNameForUser(event.getUser()), event.getPrevNick(), event.getNewNick());
    }

}
