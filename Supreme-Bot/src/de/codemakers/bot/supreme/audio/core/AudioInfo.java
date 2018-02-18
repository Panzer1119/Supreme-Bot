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
    private final double id = Math.random();

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

    @Override
    public String toString() {
        return String.format("%s: %s", author.getEffectiveName(), track.getInfo().title);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.id) ^ (Double.doubleToLongBits(this.id) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AudioInfo other = (AudioInfo) obj;
        if (Double.doubleToLongBits(this.id) != Double.doubleToLongBits(other.id)) {
            return false;
        }
        return true;
    }

}
