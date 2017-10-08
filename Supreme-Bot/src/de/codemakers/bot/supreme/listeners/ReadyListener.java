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
        MySQL.init();
        final StringBuilder out = new StringBuilder();
        out.append("This Bot is running on this Guilds:").append(Standard.NEW_LINE_DISCORD);
        jda.getGuilds().stream().forEach((guild) -> {
            final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild);
            out.append(String.format("%s (ID: %s)", guild.getName(), guild.getId())).append(Standard.NEW_LINE_DISCORD);
            /*
            guild.getRoles().stream().forEach((role) -> {
                out.append(Standard.TAB).append(String.format("%s (ID: %s) (%s)", role.getName(), role.getId(), role.getAsMention())).append(Standard.NEW_LINE_DISCORD);
            });
            out.append(Standard.NEW_LINE_DISCORD);
             */
        });
        System.out.print(out.toString());
    }

    @Override
    public final void onResume(ResumedEvent event) {
        MySQL.init();
        final JDA jda = event.getJDA();
        jda.getGuilds().stream().map((guild) -> Standard.getAdvancedGuild(guild)).forEach((advancedGuild) -> {
            advancedGuild.sayHi();
        });
    }

    @Override
    public final void onReconnect(ReconnectedEvent event) {
        //Reload User Objects etc...
    }

}
