package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.util.AdvancedFile;
import de.codemakers.bot.supreme.util.Standard;
import java.time.LocalDateTime;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * ReadyListener
 *
 * @author Panzer1119
 */
public class ReadyListener extends ListenerAdapter {

    private static final AdvancedFile SERVERS_FOLDER = new AdvancedFile(Standard.STANDARD_DATA_FOLDER, "servers");

    static {
        SERVERS_FOLDER.createAdvancedFile();
    }

    @Override
    public final void onReady(ReadyEvent event) {
        final JDA jda = event.getJDA();
        loadGuilds(jda);
        Standard.initAdvancedGuilds();
    }

    @Override
    public final void onResume(ResumedEvent event) {
        MySQL.init();
        Standard.initAdvancedGuilds();
    }

    @Override
    public final void onReconnect(ReconnectedEvent event) {
        MySQL.init();
        final JDA jda = event.getJDA();
        Standard.clearGuilds();
        final StringBuilder out = new StringBuilder();
        out.append("Bot just reconnected and needs to reload everyting").append(Standard.NEW_LINE_DISCORD);
        System.out.print(out.toString());
        loadGuilds(jda);
        Standard.initAdvancedGuilds();
    }

    private final void loadGuilds(JDA jda) {
        try {
            final LocalDateTime now = LocalDateTime.now(Standard.getZoneId());
            final StringBuilder out = new StringBuilder();
            out.append("This Bot is running on this Guilds:").append(Standard.NEW_LINE);
            jda.getGuilds().stream().forEach((guild) -> {
                try {
                    final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild);
                    out.append(String.format("%s (ID: %s)", guild.getName(), guild.getId())).append(Standard.NEW_LINE);
                    out.append("Invites:").append(Standard.NEW_LINE);
                    try {
                        guild.getInvites().complete().stream().forEach((invite) -> out.append(Standard.TAB).append(String.format("Code: %s, URL: %s, Uses: %d of %d, Max Age: %d, Inviter: %s, isExpanded: %b, isTemporary: %b, Creation Time: %s", invite.getCode(), invite.getURL(), invite.getUses(), invite.getMaxUses(), invite.getMaxAge(), Standard.getCompleteName(invite.getInviter(), true), invite.isExpanded(), invite.isTemporary(), invite.getCreationTime().atZoneSameInstant(Standard.getZoneId()).format(Standard.STANDARD_DATE_TIME_FORMATTER))).append(Standard.NEW_LINE));
                    } catch (Exception ex) {
                    }
                    out.append("Roles:").append(Standard.NEW_LINE);
                    try {
                        guild.getRoles().stream().forEach((role) -> out.append(Standard.TAB).append(String.format("%s (ID: %s) (%s)", role.getName(), role.getId(), role.getAsMention())).append(Standard.NEW_LINE));
                    } catch (Exception ex) {
                    }
                    out.append(Standard.NEW_LINE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            System.out.println(out.toString());
            final AdvancedFile file = new AdvancedFile(SERVERS_FOLDER, String.format("servers_%s.txt", now.format(Standard.STANDARD_DATE_TIME_FILE_FORMATTER)));
            Standard.addToFile(file, out.toString(), false);
            if (Standard.getConsoleTextChannel() != null) {
                Standard.getConsoleTextChannel().sendFile(file.toFile(), new MessageBuilder().append("Startup Server Information for ").append(jda.getSelfUser().getAsMention()).build()).queue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
