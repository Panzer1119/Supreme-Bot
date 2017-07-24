package de.codemakers.bot.supreme.commands.impl.fun;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.codemakers.bot.supreme.audio.core.AudioInfo;
import de.codemakers.bot.supreme.audio.core.PlayerSendHandler;
import de.codemakers.bot.supreme.audio.core.TrackManager;
import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

/**
 * MusicCommand
 *
 * @author Panzer1119
 */
public class MusicCommand extends Command {

    private static Guild guild;
    private static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private static final HashMap<Guild, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>();

    public MusicCommand() {
        AudioSourceManagers.registerRemoteSources(manager);
    }

    private final AudioPlayer createPlayer(Guild guild) {
        final AudioPlayer player = manager.createPlayer();
        final TrackManager trackManager = new TrackManager(player);
        player.addListener(trackManager);
        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(player));
        players.put(guild, new AbstractMap.SimpleEntry<>(player, trackManager));
        return player;
    }

    private final boolean hasPlayer(Guild guild) {
        return players.containsKey(guild);
    }

    private final AudioPlayer getPlayer(Guild guild) {
        if (hasPlayer(guild)) {
            return players.get(guild).getKey();
        } else {
            return createPlayer(guild);
        }
    }

    private final TrackManager getManager(Guild guild) {
        return players.get(guild).getValue();
    }

    private final boolean isIdle(Guild guild) {
        return !hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null;
    }

    private final MusicCommand loadTrack(String identifier, Member author, Message message) {
        final Guild guild = author.getGuild();
        final AudioPlayer player = getPlayer(guild);
        manager.setFrameBufferDuration(1000); //FIXME Make this variable (ms)
        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                getManager(guild).queue(track, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                final int max = Math.min(tracks.size(), Standard.PLAYLIST_LIMIT);
                final TrackManager manager_ = getManager(guild);
                for (int i = 0; i < max; i++) {
                    manager_.queue(tracks.get(i), author);
                }
            }

            @Override
            public void noMatches() {
            }

            @Override
            public void loadFailed(FriendlyException exception) {
            }
        });
        return this;
    }

    private final MusicCommand skip(Guild guild) {
        getPlayer(guild).stopTrack();
        return this;
    }

    private final long[] getTimestampAsArray(long millis) {
        long seconds = millis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds -= hours * 3600;
        long minutes = Math.floorDiv(seconds, 60);
        seconds -= minutes * 60;
        return new long[]{seconds, minutes, hours};
    }

    private final String getTimestamp(long millis) {
        final long[] timestamp = getTimestampAsArray(millis);
        final long seconds = timestamp[0];
        final long minutes = timestamp[1];
        final long hours = timestamp[2];
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    private final String buildQueueMessage(AudioInfo info) {
        final AudioTrackInfo trackInfo = info.getTrack().getInfo();
        final String title = trackInfo.title;
        final long length = trackInfo.length;
        return String.format("`[ %s ]` %s%n", getTimestamp(length), title);
    }

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("music", this), Invoker.createInvoker("m", this));
    }
    
    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return arguments != null && arguments.size() >= 1;
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        TrackManager manager_;
        try {
            guild = event.getGuild();
            switch (arguments.get(0)) {
                case "play":
                case "p":
                    String input = arguments.stream().skip(1).map(s -> " " + s).collect(Collectors.joining()).substring(1);
                    if (!input.startsWith("http://") && input.startsWith("https://")) {
                        input = "ytsearch: " + input;
                    }
                    System.out.println("Loading: " + input);
                    loadTrack(input, event.getMember(), event.getMessage());
                    System.out.println("Loaded: " + input);
                    break;
                case "skip":
                case "s":
                    if (isIdle(guild)) {
                        return;
                    }
                    for (int i = (arguments.size() > 1 ? Integer.parseInt(arguments.get(1)) : 1); i == 1; i--) {
                        skip(guild);
                    }
                    break;
                case "stop":
                    if (isIdle(guild)) {
                        return;
                    }
                    getManager(guild).purgeQueue();
                    skip(guild);
                    guild.getAudioManager().closeAudioConnection();
                    break;
                case "shuffle":
                    if (isIdle(guild)) {
                        return;
                    }
                    getManager(guild).shuffleQueue();
                    break;
                case "loop":
                case "l":
                    manager_ = getManager(guild);
                    if (arguments.size() >= 2) {
                        manager_.setLoop(Boolean.parseBoolean(arguments.get(1)));
                    } else {
                        manager_.setLoop(!manager_.isLoop());
                    }
                    break;
                case "now":
                case "info":
                    if (isIdle(guild)) {
                        return;
                    }
                    final AudioTrack track = getPlayer(guild).getPlayingTrack();
                    final AudioTrackInfo trackInfo = track.getInfo();
                    event.sendMessage(Standard.getMessageEmbed(null, "**CURRENT TRACK INFO:**").addField("Title", trackInfo.title, false).addField("Duration", String.format("`[%s / %s]`", getTimestamp(track.getPosition()), getTimestamp(track.getDuration())), false).addField("Author", trackInfo.author, false).build());
                    break;
                case "queue":
                    if (isIdle(guild)) {
                        return;
                    }
                    manager_ = getManager(guild);
                    final int sideNumber = arguments.size() > 1 ? Integer.parseInt(arguments.get(1)) : 1;
                    final ArrayList<String> tracks = new ArrayList<>();
                    List<String> tracksSublist;
                    manager_.getQueue().forEach((audioInfo) -> {
                        tracks.add(buildQueueMessage(audioInfo));
                    });
                    if (tracks.size() > 20) { //FIXME Make this variable (Anzahl Tracks pro Seite)
                        tracksSublist = tracks.subList((sideNumber - 1) * 20, sideNumber * 20); //FIXME Make this variable (Anzahl Tracks pro Seite)
                    } else {
                        tracksSublist = tracks;
                    }
                    final String out = tracksSublist.stream().collect(Collectors.joining("\n"));
                    final int sideNumberAll = tracks.size() >= 20 ? tracks.size() / 20 : 1; //FIXME Make this variable (Anzahl Tracks pro Seite)
                    tracks.clear();
                    tracksSublist.clear();
                    event.sendMessage(Standard.getMessageEmbed(null, "**CURRENT QUEUE:**%n*[%s Tracks | Side %d / %d]*%s", manager_.getQueue().size(), sideNumber, sideNumberAll, out).build());
                    break;
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {

    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        return builder;
    }

    @Override
    public final PermissionRoleFilter getPermissionRoleFilter() {
        return null; //FIXME Change this
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

}
