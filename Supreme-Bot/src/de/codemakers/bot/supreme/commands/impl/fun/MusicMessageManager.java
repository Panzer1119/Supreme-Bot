package de.codemakers.bot.supreme.commands.impl.fun;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.codemakers.bot.supreme.audio.core.AudioInfo;
import de.codemakers.bot.supreme.audio.core.TrackManager;
import de.codemakers.bot.supreme.audio.util.AudioQueue;
import de.codemakers.bot.supreme.entities.AdvancedEmote;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.entities.MessageUpdater;
import de.codemakers.bot.supreme.listeners.ReactionListener;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.TimeUnit;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * MusicMessageManager
 *
 * @author Panzer1119
 */
public class MusicMessageManager extends MessageUpdater {

    private final Guild guild;
    private final TrackManager trackManager;

    public MusicMessageManager(MessageEvent event, Guild guild, VoiceChannel voiceChannel) {
        super(event.sendAndWaitMessageFormat(Standard.toBold("LIVE MUSIC INFO")), 2, TimeUnit.SECONDS);
        this.guild = guild;
        trackManager = MusicCommand.getTrackManager(guild, voiceChannel);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.NO), (reaction, emote, guild_, user) -> deleteThis(), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.REPEAT), (reaction, emote, guild_, user) -> trackManager.toggleLoopType(), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.TRACK_PREVIOUS), (reaction, emote, guild_, user) -> trackManager.playPrevious(), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.REWIND), (reaction, emote, guild_, user) -> {
            AudioTrack track = getPlayingAudioTrack();
            if (track == null) {
                deleteThis();
            } else if (track.isSeekable()) {
                track.setPosition(Math.max(0, track.getPosition() - 5_000));
            }
        }, true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.PLAY_PAUSE), (reaction, emote, guild_, user) -> MusicCommand.setPause(guild, !MusicCommand.isPaused(guild)), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.FAST_FORWARD), (reaction, emote, guild_, user) -> {
            AudioTrack track = getPlayingAudioTrack();
            if (track == null) {
                deleteThis();
            } else if (track.isSeekable()) {
                track.setPosition(Math.min(track.getPosition() + 10_000, track.getDuration()));
            }
        }, true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.TRACK_NEXT), (reaction, emote, guild_, user) -> MusicCommand.skip(guild), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.VOLUME_NONE), (reaction, emote, guild_, user) -> MusicCommand.setVolume(guild, 0), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.VOLUME_LOW), (reaction, emote, guild_, user) -> MusicCommand.setVolume(guild, MusicCommand.getVolume(guild) - 10), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.VOLUME_HIGH), (reaction, emote, guild_, user) -> MusicCommand.setVolume(guild, MusicCommand.getVolume(guild) + 10), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.SHUFFLE), (reaction, emote, guild_, user) -> trackManager.shuffleQueue(1), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.STOP), (reaction, emote, guild_, user) -> {
            deleteThis();
            MusicCommand.stop(guild, trackManager);
        }, true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.TOP), (reaction, emote, guild_, user) -> {
            deleteThis();
            new MusicMessageManager(event, guild, voiceChannel);
        }, true);
    }

    @Override
    public void update() {
        if (MusicCommand.isIdle(guild) || !MusicCommand.existsTrackManager(trackManager)) {
            deleteThis();
            return;
        }
        final AudioTrack track = trackManager.getPlayer().getPlayingTrack();
        final AudioTrackInfo audioTrackInfo = track.getInfo();
        if (audioTrackInfo == null) {
            deleteThis();
            return;
        }
        final AudioQueue queue = trackManager.getAudioQueue();
        final AudioInfo next = queue.getNext();
        message.editMessage(Standard.getMessageEmbed(null, Standard.toBold("LIVE MUSIC INFO:"))
                .addField("Title", audioTrackInfo.title, false)
                .addField("Duration", String.format("`[%s / %s]`", MusicCommand.getTimestamp(track.getPosition()), MusicCommand.getTimestamp(track.getDuration())), false)
                .addField("Author", audioTrackInfo.author, false)
                .addField("Next Track", (next != null ? next.getTrack().getInfo().title : "None"), false)
                .addField("Volume", MusicCommand.getVolume(guild) + "%", false)
                .addField("Status", String.format("%s, %s", MusicCommand.isPaused(guild) ? "Paused" : "Playing", trackManager.getLoopType().getText()), false)
                .build()).queue();
    }

    @Override
    public void delete() {
        message_first.delete().queue();
    }

    private final AudioTrack getPlayingAudioTrack() {
        AudioTrack track = trackManager.getPlayer().getPlayingTrack();
        if (track == null) {
            for (int i = 0; i < 20; i++) {
                if ((track = trackManager.getPlayer().getPlayingTrack()) != null) {
                    break;
                }
                try {
                    Thread.sleep(250);
                } catch (Exception ex) {
                }
            }
        }
        return track;
    }

}
