package de.codemakers.bot.supreme.audio.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.codemakers.bot.supreme.audio.util.AudioQueue;
import de.codemakers.bot.supreme.core.SupremeBot;
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
    private final AudioQueue queue = new AudioQueue(); //FIXME Is this thread safe????
    private LoopType loopType = LoopType.NONE;
    private Guild guild = null;
    private VoiceChannel voiceChannel = null;

    public TrackManager(AudioPlayer player, Guild guild, VoiceChannel voiceChannel) {
        this.player = player;
        if (player == null) {
            throw new NullPointerException("The player must not be null!");
        }
        this.guild = guild;
        this.voiceChannel = voiceChannel;
        if (guild == null) {
            throw new NullPointerException("The guild must not be null!");
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
            playNext();
        }
        return this;
    }

    public final AudioQueue getAudioQueue() {
        return queue;
    }

    public final AudioPlayer getPlayer() {
        return player;
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

    public final Guild getGuild() {
        return guild;
    }

    public final TrackManager setVoiceChannel(VoiceChannel voiceChannel) {
        if (voiceChannel == null) {
            throw new NullPointerException("The VoiceChannel must not be null!");
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

    public final TrackManager resetQueue() {
        queue.clear();
        return this;
    }

    public final TrackManager resetFuture() {
        queue.removeFuture();
        return this;
    }

    public final TrackManager resetPast() {
        queue.removePast();
        return this;
    }

    public final TrackManager shuffleQueue(int times) {
        if (queue.size() <= 2) {
            return this;
        }
        player.setPaused(true);
        queue.shuffle(times);
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

    public final TrackManager setPlaying(boolean playing) {
        player.setPaused(!playing);
        return this;
    }

    public final boolean isPlaying() {
        return queue.isPlaying() && !player.isPaused();
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
        if (endReason != AudioTrackEndReason.REPLACED) {
            if (loopType.isLoop() && loopType.isSingle()) {
                player.playTrack(track.makeClone());
            } else {
                playNext();
            }
        }
    }

    private final boolean playNext() {
        if (!queue.hasNext()) {
            System.out.println("Stopping Music! LoopType: " + loopType);
            setPlaying(false);
            guild.getAudioManager().closeAudioConnection();
            SupremeBot.setStatus(null);
            return false;
        } else {
            final AudioInfo next = queue.playNext();
            System.out.println(String.format("Next AudioInfo: \"%s\", LoopType: %s", next, loopType));
            if (next == null) {
                return false;
            }
            player.playTrack(next.getTrack().makeClone());
            if (loopType.isLoop()) {
                queue(next);
            }
            return true;
        }
    }

    public final boolean playPrevious() {
        if (loopType.isLoop() && loopType.isSingle()) {
            player.stopTrack();
            return true;
        }
        if (!queue.hasPrevious()) {
            return false;
        } else {
            final AudioInfo next = queue.playPrevious();
            System.out.println(String.format("Previous AudioInfo: \"%s\", LoopType: %s", next, loopType));
            if (next == null) {
                return false;
            }
            player.playTrack(next.getTrack().makeClone());
            return true;
        }
    }

}
