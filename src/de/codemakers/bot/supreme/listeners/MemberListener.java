package de.codemakers.bot.supreme.listeners;

import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * MemberListener
 *
 * @author Panzer1119
 */
public class MemberListener extends ListenerAdapter {

    @Override
    public final void onGuildMemberJoin(GuildMemberJoinEvent event) {
        System.out.println(event.getUser().getName() + " joined!");
        event.getGuild().getTextChannels().get(0).sendMessageFormat("Welcome, %s!", event.getUser().getAsMention()).queue();
    }

}
