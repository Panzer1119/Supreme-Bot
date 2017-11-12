package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.util.Standard;
import static de.codemakers.bot.supreme.util.Standard.getZoneId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * GuildLogger
 *
 * @author Panzer1119
 */
public class GuildLogger extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        final Instant timestamp = Instant.now();
        if (Standard.getConsoleTextChannel() != null) {
            Standard.getConsoleTextChannel().sendMessage(String.format("[%s] Joined guild %s", LocalDateTime.ofInstant(timestamp, getZoneId()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT)), Standard.getCompleteName(event.getGuild()))).queue();
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        final Instant timestamp = Instant.now();
        if (Standard.getConsoleTextChannel() != null) {
            Standard.getConsoleTextChannel().sendMessage(String.format("[%s] Left guild %s", LocalDateTime.ofInstant(timestamp, getZoneId()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT)), Standard.getCompleteName(event.getGuild()))).queue();
        }
    }

}
