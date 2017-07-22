package de.codemakers.bot.supreme.listeners;

import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * VoiceListener
 *
 * @author Panzer1119
 */
public class VoiceListener extends ListenerAdapter {

    @Override
    public final void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        event.getGuild().getTextChannels().get(0).sendMessage(event.getVoiceState().getMember().getUser().getName() + " joined VoiceChannel #" + event.getChannelJoined().getName()).queue();
    }

}
