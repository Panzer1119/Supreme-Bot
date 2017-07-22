package de.codemakers.bot.supreme.audio.core;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Member;

/**
 * AudioInfo
 *
 * @author Panzer1119
 */
public class AudioInfo {

    private final AudioTrack track;
    private final Member author;

    public AudioInfo(AudioTrack track, Member author) {
        this.track = track;
        this.author = author;
    }

    public final AudioTrack getTrack() {
        return track;
    }

    public final Member getAuthor() {
        return author;
    }

}
