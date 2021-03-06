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
import de.codemakers.bot.supreme.audio.core.LoopType;
import de.codemakers.bot.supreme.audio.core.PlayerSendHandler;
import de.codemakers.bot.supreme.audio.core.TrackManager;
import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.core.SupremeBot;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.listeners.ReactionListener;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import de.codemakers.bot.supreme.util.updater.Updater;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.permission.ReactionPermissionFilter;
import de.codemakers.bot.supreme.settings.Config;
import de.codemakers.bot.supreme.util.TimeUnit;

/**
 * MusicCommand
 *
 * @author Panzer1119
 */
public class MusicCommand extends Command {

    private static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private static final HashMap<Guild, TrackManager> trackManagers = new HashMap<>();

    public MusicCommand() {
        AudioSourceManagers.registerRemoteSources(manager);
    }

    static final TrackManager createTrackManager(Guild guild, VoiceChannel voiceChannel) {
        if (guild == null) {
            return null;
        }
        final AudioPlayer player = manager.createPlayer();
        final TrackManager trackManager = new TrackManager(player, guild, voiceChannel);
        player.addListener(trackManager);
        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(player));
        trackManagers.put(guild, trackManager);
        return trackManager;
    }

    static final TrackManager getTrackManager(Guild guild, VoiceChannel voiceChannel) {
        if (guild == null) {
            return null;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager != null) {
            return trackManager;
        } else {
            return createTrackManager(guild, voiceChannel);
        }
    }

    static final boolean existsTrackManager(TrackManager trackManager) {
        return trackManagers.containsValue(trackManager);
    }

    static final TrackManager getTrackManager(Guild guild) {
        if (guild == null) {
            return null;
        }
        return trackManagers.get(guild);
    }

    static final boolean isIdle(Guild guild) {
        if (guild == null) {
            return true;
        }
        final TrackManager trackManager = getTrackManager(guild);
        return trackManager == null || trackManager.getPlayer() == null || trackManager.getPlayer().getPlayingTrack() == null || !guild.getAudioManager().isConnected();
    }

    private final MusicCommand loadTrack(String identifier, MessageEvent event, VoiceChannel channel, int max_tracks) {
        if (event == null) {
            return this;
        }
        final Member author = event.getMember();
        if (author == null) {
            return this;
        }
        final Guild guild = author.getGuild();
        if (guild == null) {
            return this;
        }
        final TrackManager trackManager = getTrackManager(guild, channel);
        if (trackManager == null) {
            return null;
        }
        manager.setFrameBufferDuration(Config.CONFIG.getBotMusicFrameBufferDuration());
        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                try {
                    trackManager.queue(track, author);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist == null || max_tracks == 0) {
                    return;
                }
                final List<AudioTrack> tracks = playlist.getTracks();
                if (tracks == null || tracks.isEmpty()) {
                    return;
                }
                final int max = Math.min(tracks.size(), Standard.PLAYLIST_LIMIT);
                for (int i = 0; i < max; i++) {
                    if (max_tracks != -1) {
                        if (i >= max_tracks) {
                            return;
                        }
                    }
                    trackManager.queue(tracks.get(i), author);
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

    static final boolean skip(Guild guild) {
        if (guild == null) {
            return false;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager == null || trackManager.getPlayer() == null) {
            return false;
        }
        trackManager.getPlayer().stopTrack();
        return true;
    }

    static final boolean setPause(Guild guild, boolean pause) {
        if (guild == null) {
            return false;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager == null) {
            return false;
        }
        trackManager.getPlayer().setVolume(Config.CONFIG.getGuildVolume(guild.getIdLong()));
        if (trackManager.isPlaying() == !pause) {
            return false;
        }
        trackManager.setPlaying(!pause);
        return true;
    }

    static final boolean isPaused(Guild guild) {
        if (guild == null) {
            return false;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager == null) {
            return false;
        }
        return !trackManager.isPlaying();
    }

    static final boolean setVolume(Guild guild, int volume) {
        if (guild == null) {
            return false;
        }
        volume = Math.max(0, Math.min(volume, 150));
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager == null || trackManager.getPlayer() == null) {
            return false;
        }
        if (trackManager.getPlayer().getVolume() == volume) {
            return false;
        }
        trackManager.getPlayer().setVolume(volume);
        Config.CONFIG.setGuildVolume(guild.getIdLong(), volume);
        return true;
    }

    static final int getVolume(Guild guild) {
        if (guild == null) {
            return -1;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager == null || trackManager.getPlayer() == null) {
            return -1;
        }
        return trackManager.getPlayer().getVolume();
    }

    public static final void stop(Guild guild, TrackManager trackManager) {
        try {
            trackManager.getPlayer().destroy();
            trackManager.resetQueue();
            trackManager.setPlaying(false);
            trackManagers.remove(guild);
            skip(guild);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        Updater.submit(() -> guild.getAudioManager().closeAudioConnection());
        SupremeBot.setStatus(null, null);
    }

    protected static final long[] getTimestampAsArray(long millis) {
        long seconds = millis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds -= hours * 3600;
        long minutes = Math.floorDiv(seconds, 60);
        seconds -= minutes * 60;
        return new long[]{seconds, minutes, hours};
    }

    protected static final String getTimestamp(long millis) {
        final long[] timestamp = getTimestampAsArray(millis);
        final long seconds = timestamp[0];
        final long minutes = timestamp[1];
        final long hours = timestamp[2];
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    protected static final String buildQueueMessage(AudioInfo info) {
        final AudioTrackInfo trackInfo = info.getTrack().getInfo();
        final String title = trackInfo.title;
        final long length = trackInfo.length;
        return String.format("`[ %s ]` %s%n", MusicCommand.getTimestamp(length), title);
    }

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("music", this), Invoker.createInvoker("m", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty() || event == null || event.getGuild() == null || event.getMember() == null) {
            return false;
        }
        final boolean play = arguments.isConsumed(Standard.ARGUMENT_PLAY, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean pause = arguments.isConsumed(Standard.ARGUMENT_PAUSE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean skip = arguments.isConsumed(Standard.ARGUMENT_SKIP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean shuffle = arguments.isConsumed(Standard.ARGUMENT_SHUFFLE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean loop = arguments.isConsumed(Standard.ARGUMENT_LOOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_INFO, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean queue = arguments.isConsumed(Standard.ARGUMENT_QUEUE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean volume = arguments.isConsumed(Standard.ARGUMENT_VOLUME, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean remove = arguments.isConsumed(Standard.ARGUMENT_REMOVE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (play) {
            if (arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
                return arguments.isSize(3, 4); //[VoiceChannel] title/url [max_tracks] -live
            } else {
                return arguments.isSize(1, 4); //[VoiceChannel] [title/url] [-yt/-sc] [max_tracks]
            }
        } else if (pause) {
            return arguments.isSize(1, 2); //[pause]
        } else if (skip) {
            return arguments.isSize(1, 2); //[times or all]
        } else if (stop) {
            return arguments.isSize(1);
        } else if (shuffle) {
            return arguments.isSize(1, 2); //[times]
        } else if (loop) {
            return arguments.isSize(1, 3); //[toggle]
        } else if (info) {
            if (arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
                return arguments.isSize(2, 3); //[playlist] -live
            } else {
                return arguments.isSize(1, 2); //[playlist]
            }
        } else if (queue) {
            if (arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
                return arguments.isSize(2, 3); //[pagenumber] -live
            } else {
                return arguments.isSize(1, 2); //[pagenumber]
            }
        } else if (volume) {
            return arguments.isSize(1, 2); //[New volume]
        } else {
            return false;
        }
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean play = arguments.isConsumed(Standard.ARGUMENT_PLAY, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean pause = arguments.isConsumed(Standard.ARGUMENT_PAUSE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean skip = arguments.isConsumed(Standard.ARGUMENT_SKIP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean shuffle = arguments.isConsumed(Standard.ARGUMENT_SHUFFLE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean loop = arguments.isConsumed(Standard.ARGUMENT_LOOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_INFO, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean queue = arguments.isConsumed(Standard.ARGUMENT_QUEUE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean volume = arguments.isConsumed(Standard.ARGUMENT_VOLUME, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final Guild guild = event.getGuild();
        VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        if (play) {
            voiceChannel = Util.resolveVoiceChannel(event.getGuild(), arguments.getFirst());
            if (voiceChannel != null) {
                arguments.consumeFirst();
            } else {
                voiceChannel = event.getMember().getVoiceState().getChannel();
            }
            if (voiceChannel == null) {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "you have to be in a VoiceChannel or specifiy one!").build());
                return;
            }
        }
        final TrackManager trackManager = getTrackManager(guild, voiceChannel);
        try {
            if (play) {
                final boolean live = arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                if (arguments.isEmpty()) {
                    if (isIdle(guild)) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                        return;
                    }
                    final boolean done = setPause(guild, false);
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY * 2, "%s %scontinued the music!", event.getAuthor().getAsMention(), (done ? "" : "not "));
                } else {
                    final boolean youTube = arguments.isConsumed(Standard.ARGUMENT_YOUTUBE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                    final boolean soundCloud = arguments.isConsumed(Standard.ARGUMENT_SOUNDCLOUD, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                    final String input = arguments.consumeFirst();
                    final boolean url = (input.startsWith("http://") || input.startsWith("https://"));
                    int max_tracks = (url ? -1 : 1);
                    if (!arguments.isEmpty()) {
                        max_tracks = Integer.parseInt(arguments.consumeFirst());
                    }
                    loadTrack((url ? "" : ((youTube || !soundCloud) ? "yt" : "sc") + "search: ") + input, event, voiceChannel, max_tracks);
                    if (live) {
                        final VoiceChannel channel = voiceChannel;
                        Util.sheduleTimerAndRemove(() -> {
                            new MusicMessageManager(event, guild, channel);
                        }, 3000); //TODO Make this variable (ms) ???
                    }
                    setPause(guild, false);
                }
            } else if (pause) {
                if (arguments.isEmpty()) {
                    setPause(guild, !isPaused(guild));
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %spaused the music.", event.getAuthor().getAsMention(), (isPaused(guild) ? "" : "un"));
                } else {
                    try {
                        final boolean pause_ = Boolean.parseBoolean(arguments.consumeFirst());
                        final boolean done = setPause(guild, pause_);
                        if (done) {
                            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %spaused the music.", event.getAuthor().getAsMention(), (isPaused(guild) ? "" : "un"));
                        } else {
                            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the music is already %spaused!", Emoji.WARNING, event.getAuthor().getAsMention(), (pause_ ? "" : "un"));
                        }
                    } catch (Exception ex) {
                        setPause(guild, !isPaused(guild));
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %spaused the music.", event.getAuthor().getAsMention(), (isPaused(guild) ? "" : "un"));
                    }
                }
            } else if (skip) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                final boolean all = arguments.isConsumed(Standard.ARGUMENT_ALL, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                if (all) {
                    trackManager.resetFuture();
                    skip(guild);
                } else {
                    final int times = (arguments.isEmpty() ? 1 : Integer.parseInt(arguments.consumeFirst()));
                    for (int i = 0; i < times; i++) {
                        skip(guild);
                    }
                }
            } else if (stop) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                } else if (trackManager == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no player running!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                stop(guild, trackManager);
            } else if (shuffle) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                } else if (trackManager == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no player running!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                int times = 1;
                try {
                    times = (arguments.isEmpty() ? 1 : Integer.parseInt(arguments.consumeFirst()));
                } catch (Exception ex) {
                }
                trackManager.shuffleQueue(times);
            } else if (loop) {
                if (trackManager == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no player running!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                if (arguments.isEmpty()) {
                    trackManager.toggleLoopType();
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s toggled music looping mode to \"%s\".", event.getAuthor().getAsMention(), trackManager.getLoopType());
                } else {
                    try {
                        final boolean loop_loop = Boolean.parseBoolean(arguments.consumeFirst());
                        boolean loop_single = false;
                        if (!arguments.isEmpty()) {
                            loop_single = Boolean.parseBoolean(arguments.consumeFirst());
                        }
                        trackManager.setLoopType(LoopType.of(loop_loop, loop_single));
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s setted music looping mode to \"%s\".", event.getAuthor().getAsMention(), trackManager.getLoopType());
                    } catch (Exception ex) {
                        trackManager.toggleLoopType();
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s toggled music looping mode to \"%s\".", event.getAuthor().getAsMention(), trackManager.getLoopType());
                    }
                }
            } else if (info) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                final boolean live = arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                final AudioPlayer player_ = trackManager.getPlayer();
                final AudioTrack track = player_.getPlayingTrack();
                final AudioTrackInfo trackInfo = track.getInfo();
                if (!live) {
                    ReactionListener.deleteMessageWithReaction(event.sendAndWaitMessage(Standard.getMessageEmbed(null, Standard.toBold("CURRENT TRACK INFO:")).addField("Title", trackInfo.title, false).addField("Duration", String.format("`[%s / %s]`", getTimestamp(track.getPosition()), getTimestamp(track.getDuration())), false).addField("Author", trackInfo.author, false).build()), "x", 2, TimeUnit.MINUTES, true, ReactionPermissionFilter.createUserFilter(event.getAuthor()));
                } else {
                    new MusicMessageManager(event, guild, voiceChannel);
                }
            } else if (queue) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                final boolean live = arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                if (live) {
                    new MusicQueueMessageManager(event, guild, voiceChannel);
                    return;
                }
                int pageNumber = 1;
                try {
                    pageNumber = (arguments.isEmpty() ? 1 : Integer.parseInt(arguments.consumeFirst()));
                } catch (Exception ex) {
                }
                final ArrayList<String> tracks_pre = new ArrayList<>();
                final ArrayList<String> tracks_post = new ArrayList<>();
                //List<AudioInfo> infos = new ArrayList<>(trackManager.getAudioQueue().getFuture());
                List<AudioInfo> infos_pre = new ArrayList<>(trackManager.getAudioQueue().getPast());
                List<AudioInfo> infos_post = new ArrayList<>(trackManager.getAudioQueue().getFuture());
                /*if (trackManager.getAudioQueue().isPlaying()) {
                    infos.add(0, trackManager.getAudioQueue().getNow());
                }*/
                final int MAX_TRACKS_PER_PAGE = Config.CONFIG.getGuildMusicMaxTracksPerPage(guild.getIdLong());
                //final String queue_name = "default"; //TODO Add queues ability to save them
                //TODO Alle Commands muessen methode machen mit stop, damit sie herunterfahren koennen
                final int track_size = trackManager.getAudioQueue().size();
                int size = track_size;
                final int pageNumberAll = (track_size > MAX_TRACKS_PER_PAGE ? (int) (track_size / MAX_TRACKS_PER_PAGE + 1.0) : 1);
                pageNumber = Math.max(1, Math.min(pageNumberAll, pageNumber));
                while (size > MAX_TRACKS_PER_PAGE && !infos_pre.isEmpty()) {
                    infos_pre.remove(0);
                    size = infos_pre.size() + infos_post.size();
                }
                if (size > MAX_TRACKS_PER_PAGE) {
                    infos_post = infos_post.subList(Math.max(0, (pageNumber - 1) * MAX_TRACKS_PER_PAGE), Math.min(pageNumber * MAX_TRACKS_PER_PAGE, track_size));
                    size = infos_pre.size() + infos_post.size();
                }
                AtomicLong length_all = new AtomicLong(0);
                infos_pre.stream().forEach((audioInfo) -> {
                    length_all.addAndGet(audioInfo.getTrack().getDuration());
                    tracks_pre.add(buildQueueMessage(audioInfo));
                });
                infos_post.stream().forEach((audioInfo) -> {
                    length_all.addAndGet(audioInfo.getTrack().getDuration());
                    tracks_post.add(buildQueueMessage(audioInfo));
                });
                final String out_pre = tracks_pre.stream().collect(Collectors.joining(Standard.NEW_LINE_DISCORD));
                final String out_post = tracks_post.stream().collect(Collectors.joining(Standard.NEW_LINE_DISCORD));
                event.sendMessage(Standard.getMessageEmbed(null, "**CURRENT QUEUE: **%n%n*[%d/%d Tracks | Complete Duration `[ %s ]` | Page %d / %d]*%n%n%s**Currently Playing ->** %s", size, track_size, getTimestamp(length_all.get()), pageNumber, pageNumberAll, out_pre.isEmpty() ? "" : out_pre + "\n", out_post).build());
            } else if (volume) {
                if (trackManager == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no player running!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                int volume_ = getVolume(guild);
                if (arguments.isEmpty()) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY * 2, "%s the current volume is %d.", event.getAuthor().getAsMention(), volume_);
                } else {
                    try {
                        final int times = Integer.parseInt(arguments.consumeFirst());
                        final boolean done = setVolume(guild, times);
                        volume_ = getVolume(guild);
                        if (done) {
                            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s setted the volume to %d.", event.getAuthor().getAsMention(), volume_);
                        } else {
                            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the volume is already %d!", Emoji.WARNING, event.getAuthor().getAsMention(), volume_);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s %s [VoiceChannel] [[URL or Text] [%s/%s] [Maximum Number of Tracks]] [%s]", invoker, Standard.ARGUMENT_PLAY.getCompleteArgument(0, -1), Standard.ARGUMENT_YOUTUBE.getCompleteArgument(0, -1), Standard.ARGUMENT_SOUNDCLOUD.getCompleteArgument(0, -1), Standard.ARGUMENT_LIVE.getCompleteArgument(0, -1)), "Unpauses the bot or plays from an URL or searches on YouTube for a video. If loading more than one video you can set the maximum number of videos that should be loaded, or -1 for all the bot can find. Optionally shows a live track info. Use `VoiceChannel#Number` or its id when there are multiple VoiceChannels with the same name.", false);
        builder.addField(String.format("%s %s [Pause]", invoker, Standard.ARGUMENT_PAUSE.getCompleteArgument(0, -1)), "Toggles or sets pause.", false);
        builder.addField(String.format("%s %s [Times/All]", invoker, Standard.ARGUMENT_SKIP.getCompleteArgument(0, -1)), "Skips 1 or more or even all tracks.", false);
        builder.addField(String.format("%s %s", invoker, Standard.ARGUMENT_STOP.getCompleteArgument(0, -1)), "Stops the music.", false);
        builder.addField(String.format("%s %s [Times]", invoker, Standard.ARGUMENT_SHUFFLE.getCompleteArgument(0, -1)), "Shuffles 1 or more times the queue.", false);
        builder.addField(String.format("%s %s [Loop]", invoker, Standard.ARGUMENT_LOOP.getCompleteArgument(0, -1)), "Toggles or sets looping.", false);
        builder.addField(String.format("%s %s [Playlist]", invoker, Standard.ARGUMENT_INFO.getCompleteArgument(0, -1)), "Shows info about current queue or playlist.", false);
        builder.addField(String.format("%s %s [Page]", invoker, Standard.ARGUMENT_QUEUE.getCompleteArgument(0, -1)), "Shows tracks in current queue and page.", false);
        builder.addField(String.format("%s %s [Volume]", invoker, Standard.ARGUMENT_VOLUME.getCompleteArgument(0, -1)), "Sets or returns the current volume.", false);
        return builder;
    }

    @Override
    public final PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_BOTH_VIP;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_FUN;
    }

}
