package de.panzercraft.bot.supreme.audio.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * TrackManager
 *
 * @author Panzer1119
 */
public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer player;
    private final Queue<AudioInfo> queue;
    private boolean loop = false;

    public TrackManager(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public final TrackManager queue(AudioTrack track, Member author) {
        if (track == null || author == null) {
            return this;
        }
        return queue(new AudioInfo(track, author));
    }

    public final TrackManager queue(AudioInfo info) {
        if (info == null) {
            return this;
        }
        queue.add(info);
        if (player.getPlayingTrack() == null) {
            player.playTrack(info.getTrack());
        }
        return this;
    }

    public final Set<AudioInfo> getQueue() {
        return new LinkedHashSet<>(queue);
    }

    public final AudioInfo getAudioInfo(AudioTrack track) {
        if (track == null) {
            return null;
        }
        return queue.stream().filter((info) -> info.getTrack().equals(track)).findFirst().orElse(null);
    }

    public final TrackManager purgeQueue() {
        queue.clear();
        return this;
    }

    public final TrackManager shuffleQueue() {
        if (queue.size() <= 2) {
            return this;
        }
        final List<AudioInfo> queue_ = new ArrayList<>(getQueue());
        final AudioInfo current = queue_.get(0);
        queue_.remove(0);
        Collections.shuffle(queue_);
        queue_.add(0, current);
        purgeQueue();
        queue.addAll(queue_);
        queue_.clear();
        return this;
    }

    public final boolean isLoop() {
        return loop;
    }

    public final TrackManager setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    @Override
    public final void onTrackStart(AudioPlayer player, AudioTrack track) {
        final AudioInfo current = queue.element();
        final VoiceChannel voiceChannel = current.getAuthor().getVoiceState().getChannel();
        if (voiceChannel == null) {
            player.stopTrack();
        } else {
            current.getAuthor().getGuild().getAudioManager().openAudioConnection(voiceChannel);
        }
    }

    @Override
    public final void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        final AudioInfo next = queue.poll();
        if (loop) {
            queue(next);
        }
        final Guild guild = next.getAuthor().getGuild();
        if (!loop && queue.isEmpty()) {
            guild.getAudioManager().closeAudioConnection();
        } else {
            player.playTrack(queue.element().getTrack());
        }
    }

}
