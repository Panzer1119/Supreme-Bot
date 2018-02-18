package de.codemakers.bot.supreme.commands.impl.fun;

import de.codemakers.bot.supreme.audio.core.AudioInfo;
import de.codemakers.bot.supreme.audio.core.TrackManager;
import de.codemakers.bot.supreme.entities.AdvancedEmote;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.entities.MessageUpdater;
import de.codemakers.bot.supreme.listeners.ReactionListener;
import de.codemakers.bot.supreme.settings.Config;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * MusicQueueMessageManager
 *
 * @author Panzer1119
 */
public class MusicQueueMessageManager extends MessageUpdater {

    private final Guild guild;
    private final TrackManager trackManager;
    private final AtomicInteger page = new AtomicInteger(1);
    private final List<String> tracks = new ArrayList<>();
    private List<AudioInfo> infos_pre = null;
    private List<AudioInfo> infos_post = null;

    public MusicQueueMessageManager(MessageEvent event, Guild guild, VoiceChannel voiceChannel) {
        super(event.sendAndWaitMessageFormat(Standard.toBold("LIVE QUEUE:")), 5, TimeUnit.SECONDS);
        this.guild = guild;
        trackManager = MusicCommand.getTrackManager(guild, voiceChannel);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.NO), (reaction, emote, guild_, user) -> deleteThis(), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.REPEAT), (reaction, emote, guild_, user) -> update(), true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.TRACK_PREVIOUS), (reaction, emote, guild_, user) -> {
            page.set(1);
            update();
        }, true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.REWIND), (reaction, emote, guild_, user) -> {
            page.set(Math.max(1, page.get() - 1));
            update();
        }, true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.FAST_FORWARD), (reaction, emote, guild_, user) -> {
            page.set(Math.min(page.get() + 1, getMaxPageNumber()));
            update();
        }, true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.TRACK_NEXT), (reaction, emote, guild_, user) -> {
            page.set(getMaxPageNumber());
            update();
        }, true);
        ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.TOP), (reaction, emote, guild_, user) -> {
            deleteThis();
            new MusicQueueMessageManager(event, guild, voiceChannel);
        }, true);
    }

    private final int getMaxPageNumber() {
        final int track_size = trackManager.getAudioQueue().size();
        final int MAX_TRACKS_PER_PAGE = Config.CONFIG.getGuildMusicMaxTracksPerPage(guild.getIdLong());
        return (track_size > MAX_TRACKS_PER_PAGE ? (int) (track_size / MAX_TRACKS_PER_PAGE + 1.0) : 1);
    }

    @Override
    public void update() {
        if (MusicCommand.isIdle(guild) || !MusicCommand.existsTrackManager(trackManager)) {
            deleteThis();
            return;
        }
        page.set(Math.max(1, Math.min(page.get(), getMaxPageNumber())));
        final int pageNumber = page.get();
        tracks.clear();
        infos_pre = new ArrayList<>(trackManager.getAudioQueue().getPast());
        infos_post = new ArrayList<>(trackManager.getAudioQueue().getFuture());
        final int MAX_TRACKS_PER_PAGE = Config.CONFIG.getGuildMusicMaxTracksPerPage(guild.getIdLong());
        final int track_size = trackManager.getAudioQueue().size();
        int size = track_size;
        final int pageNumberAll = (track_size > MAX_TRACKS_PER_PAGE ? (int) (track_size / MAX_TRACKS_PER_PAGE + 1.0) : 1);
        final AudioInfo current = infos_post.get(0);
        List<AudioInfo> infos = new ArrayList<>(infos_pre);
        infos.addAll(infos_post);
        if (size > MAX_TRACKS_PER_PAGE) {
            infos = infos.subList(Math.max(1, (pageNumber - 1) * MAX_TRACKS_PER_PAGE) - 1, Math.min(pageNumber * MAX_TRACKS_PER_PAGE, size));
            size = infos.size();
        }
        AtomicLong length_all = new AtomicLong(0);
        infos.forEach((audioInfo) -> {
            length_all.addAndGet(audioInfo.getTrack().getDuration());
            if (Objects.equals(current, audioInfo)) {
                tracks.add(Standard.toBold("Currently Playing ->") + " " + MusicCommand.buildQueueMessage(audioInfo));
            } else {
                tracks.add(MusicCommand.buildQueueMessage(audioInfo));
            }
        });
        final String out = tracks.stream().collect(Collectors.joining(Standard.NEW_LINE_DISCORD));
        message.editMessage(Standard.getMessageEmbed(null, "**LIVE QUEUE: **%n%n*[%d-%d/%d Tracks | Complete Duration `[ %s ]` | Page %d / %d]*%n%n%s", Math.max(0, (pageNumber - 1) * MAX_TRACKS_PER_PAGE) + 1, Math.max(1, (pageNumber - 1) * MAX_TRACKS_PER_PAGE) + size - 1, track_size, MusicCommand.getTimestamp(length_all.get()), pageNumber, pageNumberAll, out).build()).queue();
    }

    @Override
    public void delete() {
        message_first.delete().queue();
    }

}
