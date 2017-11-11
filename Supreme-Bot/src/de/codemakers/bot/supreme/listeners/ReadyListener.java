package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.JDA;
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

    @Override
    public final void onReady(ReadyEvent event) {
        final JDA jda = event.getJDA();
        final StringBuilder out = new StringBuilder();
        loadGuilds(jda, out, false);
        System.out.print(out.toString());
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
        loadGuilds(jda, out, false);
        System.out.print(out.toString());
    }

    private final void loadGuilds(JDA jda, StringBuilder out, boolean withRoles) {
        out.append("This Bot is running on this Guilds:").append(Standard.NEW_LINE_DISCORD);
        jda.getGuilds().stream().forEach((guild) -> {
            final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild);
            out.append(String.format("%s (ID: %s)", guild.getName(), guild.getId())).append(Standard.NEW_LINE_DISCORD);
            if (withRoles) {
                guild.getRoles().stream().forEach((role) -> {
                    out.append(Standard.TAB).append(String.format("%s (ID: %s) (%s)", role.getName(), role.getId(), role.getAsMention())).append(Standard.NEW_LINE_DISCORD);
                });
                out.append(Standard.NEW_LINE_DISCORD);
            }
        });
    }

}
