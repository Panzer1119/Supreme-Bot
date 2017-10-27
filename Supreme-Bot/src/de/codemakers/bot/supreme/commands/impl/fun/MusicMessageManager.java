package de.codemakers.bot.supreme.commands.impl.fun;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.codemakers.bot.supreme.audio.core.AudioInfo;
import de.codemakers.bot.supreme.audio.core.TrackManager;
import de.codemakers.bot.supreme.audio.util.AudioQueue;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.entities.MessageManager;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import java.util.concurrent.atomic.AtomicInteger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * MusicMessageManager
 *
 * @author Panzer1119
 */
public class MusicMessageManager extends MessageManager {

    private final MessageEvent event;
    private final Guild guild;
    private final VoiceChannel voiceChannel;
    private final TrackManager trackManager;
    private final AtomicInteger counter = new AtomicInteger(-2);

    public MusicMessageManager(MessageEvent event, Guild guild, VoiceChannel voiceChannel) {
        super(event.sendAndWaitMessageFormat(Standard.toBold("LIVE MUSIC INFO")), false);
        this.event = event;
        this.guild = guild;
        this.voiceChannel = voiceChannel;
        trackManager = MusicCommand.getTrackManager(guild, voiceChannel);
        message.addReaction(Emoji.NO).queue();
        message.addReaction(Emoji.REPEAT).queue();
        message.addReaction(Emoji.TRACK_PREVIOUS).queue();
        message.addReaction(Emoji.REWIND).queue();
        message.addReaction(Emoji.PLAY_PAUSE).queue();
        message.addReaction(Emoji.FAST_FORWARD).queue();
        message.addReaction(Emoji.TRACK_NEXT).queue();
        message.addReaction(Emoji.VOLUME_NONE).queue();
        message.addReaction(Emoji.VOLUME_LOW).queue();
        message.addReaction(Emoji.VOLUME_HIGH).queue();
        message.addReaction(Emoji.SHUFFLE).queue();
        message.addReaction(Emoji.STOP).queue();
        message.addReaction(Emoji.TOP).queue();
    }

    @Override
    public void onReaction(MessageReaction messageReaction) {
        boolean remove = true;
        boolean stop = false;
        if (isReacted(messageReaction, Emoji.NO)) {
            stop = true;
        } else if (isReacted(messageReaction, Emoji.REPEAT)) {
            trackManager.toggleLoopType();
        } else if (isReacted(messageReaction, Emoji.TRACK_PREVIOUS)) {
            trackManager.playPrevious();
        } else if (isReacted(messageReaction, Emoji.PLAY_PAUSE)) {
            MusicCommand.setPause(guild, !MusicCommand.isPaused(guild));
        } else if (isReacted(messageReaction, Emoji.TRACK_NEXT)) {
            MusicCommand.skip(guild);
        } else if (isReacted(messageReaction, Emoji.VOLUME_NONE)) {
            MusicCommand.setVolume(guild, 0);
        } else if (isReacted(messageReaction, Emoji.VOLUME_LOW)) {
            MusicCommand.setVolume(guild, MusicCommand.getVolume(guild) - 10);
        } else if (isReacted(messageReaction, Emoji.VOLUME_HIGH)) {
            MusicCommand.setVolume(guild, MusicCommand.getVolume(guild) + 10);
        } else if (isReacted(messageReaction, Emoji.SHUFFLE)) {
            trackManager.shuffleQueue(1);
        } else if (isReacted(messageReaction, Emoji.STOP)) {
            stop = true;
            MusicCommand.stop(guild, trackManager);
        } else if (isReacted(messageReaction, Emoji.TOP)) {
            deleteThis();
            MusicCommand.showLiveInfo(event, guild, voiceChannel);
            return;
        }
        if (remove) {
            removeReaction(messageReaction);
        }
        if (stop) {
            deleteThis();
            return;
        }
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
        if (track == null) {
            deleteThis();
            return;
        }
        remove = true;
        stop = false;
        if (isReacted(messageReaction, Emoji.FAST_FORWARD)) {
            if (track.isSeekable()) {
                track.setPosition(Math.min(track.getPosition() + 10_000, track.getDuration()));
            }
        } else if (isReacted(messageReaction, Emoji.REWIND)) {
            if (track.isSeekable()) {
                track.setPosition(Math.max(0, track.getPosition() - 5_000));
            }
        }
        if (remove) {
            removeReaction(messageReaction);
        }
        if (stop) {
            deleteThis();
            return;
        }
    }

    @Override
    public void onReaction(MessageReaction messageReaction, User user, boolean removed) {
    }

    @Override
    public void delete() {
        message_first.delete().queue();
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
        counter.set(counter.get() + 1);
        if (counter.get() < 0 || counter.get() >= 5) {
            counter.set(0);
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
    }

}
