package de.codemakers.bot.supreme.audio.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.codemakers.bot.supreme.core.SupremeBot;
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
    private LoopType loopType = LoopType.NONE;
    Guild guild = null;
    VoiceChannel voiceChannel = null;

    public TrackManager(AudioPlayer player, Guild guild, VoiceChannel voiceChannel) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guild = guild;
        this.voiceChannel = voiceChannel;
        if (guild == null) {
            throw new NullPointerException("The guild can never be null!");
        }
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
            player.playTrack(info.getTrack().makeClone());
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

    public final VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }

    public final TrackManager setVoiceChannel(VoiceChannel voiceChannel) {
        if (voiceChannel == null) {
            throw new NullPointerException("The voicechannel can never be null!");
        }
        if ((this.voiceChannel != null && !this.voiceChannel.equals(voiceChannel)) || (guild.getAudioManager().isConnected() && !voiceChannel.equals(guild.getAudioManager().getConnectedChannel()))) {
            guild.getAudioManager().closeAudioConnection();
        }
        if (this.voiceChannel == null || (!guild.getAudioManager().isConnected() || !voiceChannel.equals(guild.getAudioManager().getConnectedChannel()))) {
            guild.getAudioManager().openAudioConnection(voiceChannel);
        }
        this.voiceChannel = voiceChannel;
        return this;
    }

    public final TrackManager purgeQueue() {
        queue.clear();
        return this;
    }

    public final TrackManager shuffleQueue() { //TODO Test it
        if (queue.size() <= 2) {
            return this;
        }
        player.setPaused(true);
        final List<AudioInfo> queue_ = new ArrayList<>(getQueue());
        final AudioInfo current = queue_.get(0);
        queue_.remove(0);
        Collections.shuffle(queue_);
        queue_.add(0, current);
        purgeQueue();
        queue.addAll(queue_);
        queue_.clear();
        player.setPaused(false);
        return this;
    }

    public final LoopType getLoopType() {
        return loopType;
    }

    public final TrackManager setLoopType(LoopType loopType) {
        this.loopType = loopType;
        if (this.loopType == null) {
            this.loopType = LoopType.NONE;
        }
        return this;
    }

    public final TrackManager toggleLoopType() {
        switch (loopType) {
            case NONE:
                setLoopType(LoopType.LOOP);
                break;
            case LOOP:
                setLoopType(LoopType.LOOP_SINGLE);
                break;
            case LOOP_SINGLE:
                setLoopType(LoopType.NONE);
                break;
            default:
                setLoopType(null);
                break;
        }
        return this;
    }

    @Override
    public final void onTrackStart(AudioPlayer player, AudioTrack track) {
        SupremeBot.setStatus(track.getInfo().title);
        System.out.println("voiceChannel: " + voiceChannel);
        if (voiceChannel == null) { //FIXME Wtf why would this happen??!
            try {
                if (player.getPlayingTrack() != null) {
                    player.stopTrack();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            setVoiceChannel(voiceChannel);
        }
    }

    @Override
    public final void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (loopType.isLoop() && loopType.isSingle()) {
            player.playTrack(track.makeClone());
        } else {
            AudioInfo next = queue.poll();
            System.out.println(String.format("Next AudioInfo: \"%s\", LoopType: %s", next, loopType.toString()));
            if (next != null && loopType.isLoop()) {
                queue(next);
            }
            if (next == null || /*!loop && */ queue.isEmpty()) { //FIXME Selbst wenn loop ist und die queue empty muss trotzdem abgebrochen werden?
                guild.getAudioManager().closeAudioConnection();
                SupremeBot.setStatus(null);
            } else {
                player.playTrack(queue.element().getTrack().makeClone());
            }
        }
    }

}
