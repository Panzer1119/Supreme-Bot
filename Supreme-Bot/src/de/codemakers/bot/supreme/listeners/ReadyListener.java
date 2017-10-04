package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.permission.PermissionRole;
import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
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
        jda.getGuilds().stream().forEach((guild) -> {
            final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild);
        });
        MySQL.init();
        if (true) {
            return;
        }
        String out = "\nThis Bot is running on following Servers: \n";
        for (Guild guild : jda.getGuilds()) {
            out += guild.getName() + " (" + guild.getId() + ") \n";
            for (Role role : guild.getRoles()) {
                out += String.format("%nROLE: \"%s\" (ID: %s) %s%nLoaded ROLE: %s", role.getName(), role.getId(), role.getAsMention(), PermissionRole.getPermissionRolesByGuildAndRole(guild.getIdLong(), role.getIdLong()));
            }
            out += "\n\n\n";
        }
        System.out.println(out);
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
        //onReady(event);
    }

}
