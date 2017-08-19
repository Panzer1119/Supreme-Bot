package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.core.SupremeBot;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.permission.PermissionRole;
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
        for (Guild guild : jda.getGuilds()) {
            final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild);
        }
        if (true) {
            return;
        }
        String out = "\nThis Bot is running on following Servers: \n";
        for (Guild guild : jda.getGuilds()) {
            out += guild.getName() + " (" + guild.getId() + ") \n";
            for (Role role : guild.getRoles()) {
                out += String.format("%nROLE: \"%s\" (ID: %s) %s%nLoaded ROLE: %s", role.getName(), role.getId(), role.getAsMention(), PermissionRole.getPermissionRoleByGuildAndRole(guild.getId(), role.getId()));
            }
            out += "\n\n\n";
        }
        System.out.println(out);
    }
    
    @Override
    public final void onResume(ResumedEvent event) {
        final JDA jda = event.getJDA();
        for (Guild guild : jda.getGuilds()) {
            final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild);
            advancedGuild.sayHi();
        }
    }

    @Override
    public final void onReconnect(ReconnectedEvent event) {
        //onReady(event);
    }

}
