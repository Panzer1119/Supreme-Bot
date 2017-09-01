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
import de.codemakers.bot.supreme.audio.util.AudioQueue;
import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.core.SupremeBot;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import de.codemakers.bot.supreme.util.updater.Updateable;
import de.codemakers.bot.supreme.util.updater.Updater;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

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

    private final TrackManager createTrackManager(Guild guild, VoiceChannel channel) {
        if (guild == null) {
            return null;
        }
        final AudioPlayer player = manager.createPlayer();
        final TrackManager trackManager = new TrackManager(player, guild, channel);
        player.addListener(trackManager);
        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(player));
        trackManagers.put(guild, trackManager);
        return trackManager;
    }

    private final TrackManager getTrackManager(Guild guild, VoiceChannel channel) {
        if (guild == null) {
            return null;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager != null) {
            return trackManager;
        } else {
            return createTrackManager(guild, channel);
        }
    }

    private final TrackManager getTrackManager(Guild guild) {
        if (guild == null) {
            return null;
        }
        return trackManagers.get(guild);
    }

    private final boolean isIdle(Guild guild) {
        if (guild == null) {
            return true;
        }
        final TrackManager trackManager = getTrackManager(guild);
        return trackManager == null || trackManager.getPlayer() == null || trackManager.getPlayer().getPlayingTrack() == null;
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
        manager.setFrameBufferDuration(5000); //FIXME Make this variable (ms)
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
                final List<AudioTrack> tracks = playlist.getTracks();
                final int max = Math.min(tracks.size(), Standard.PLAYLIST_LIMIT);
                for (int i = 0; i < max; i++) {
                    if (max_tracks >= 0) {
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

    private final MusicCommand skip(Guild guild) {
        if (guild == null) {
            return this;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager == null || trackManager.getPlayer() == null) {
            return this;
        }
        trackManager.getPlayer().stopTrack();
        return this;
    }

    private final boolean setPause(Guild guild, boolean pause) {
        if (guild == null) {
            return false;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager == null) {
            return false;
        }
        if (trackManager.isPlaying() == !pause) {
            return false;
        }
        trackManager.setPlaying(!pause);
        return true;
    }

    private final boolean isPaused(Guild guild) {
        if (guild == null) {
            return false;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager == null) {
            return false;
        }
        return !trackManager.isPlaying();
    }

    private final boolean setVolume(Guild guild, int volume) {
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
        return true;
    }

    private final int getVolume(Guild guild) {
        if (guild == null) {
            return -1;
        }
        final TrackManager trackManager = getTrackManager(guild);
        if (trackManager == null || trackManager.getPlayer() == null) {
            return -1;
        }
        return trackManager.getPlayer().getVolume();
    }

    private final void stop(Guild guild, TrackManager trackManager) {
        trackManager.resetQueue();
        trackManager.setPlaying(false);
        skip(guild);
        guild.getAudioManager().closeAudioConnection();
        SupremeBot.setStatus(null);
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
        if (arguments == null || arguments.isEmpty() || event == null || event.getMember() == null) {
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
        if (play) {
            if (arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
                return arguments.isSize(3, 4); //title/url [max_tracks] -live
            } else {
                return arguments.isSize(1, 3); //[title/url] [max_tracks]
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
                return arguments.isSize(2, 3); //-live [playlist]
            } else {
                return arguments.isSize(1, 2); //[playlist]
            }
        } else if (queue) {
            return arguments.isSize(1, 2); //[pagenumber]
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
        final VoiceChannel channel = event.getMember().getVoiceState().getChannel(); //TODO Make the channel selectable
        final TrackManager trackManager = getTrackManager(guild, channel);
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
                    final String input = arguments.consumeFirst();
                    final boolean url = (input.startsWith("http://") || input.startsWith("https://"));
                    int max_tracks = (url ? -1 : 1);
                    if (!arguments.isEmpty()) {
                        max_tracks = Integer.parseInt(arguments.consumeFirst());
                    }
                    loadTrack((url ? "" : "ytsearch: ") + input, event, channel, max_tracks);
                    if (live) {
                        Util.sheduleTimerAndRemove(() -> {
                            showLiveInfo(event, guild, channel);
                        }, 2500); //TODO Make this variable (ms) ???
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
                    event.sendMessage(Standard.getMessageEmbed(null, Standard.toBold("CURRENT TRACK INFO:")).addField("Title", trackInfo.title, false).addField("Duration", String.format("`[%s / %s]`", getTimestamp(track.getPosition()), getTimestamp(track.getDuration())), false).addField("Author", trackInfo.author, false).build());
                } else {
                    showLiveInfo(event, guild, channel);
                }
            } else if (queue) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                int pageNumber = 1;
                try {
                    pageNumber = (arguments.isEmpty() ? 1 : Integer.parseInt(arguments.consumeFirst()));
                } catch (Exception ex) {
                }
                final ArrayList<String> tracks = new ArrayList<>();
                List<AudioInfo> infos = new ArrayList<>(trackManager.getAudioQueue().getFuture());
                if (trackManager.getAudioQueue().isPlaying()) {
                    infos.add(0, trackManager.getAudioQueue().getNow());
                }
                final int MAX_TRACKS_PER_PAGE = 20; //FIXME Make this variable (Anzahl Tracks pro Seite)
                final String queue_name = "default"; //TODO Add queues ability to save them
                //TODO Alle Commands muessen methode machen mit stop, damit sie herunterfahren koennen
                final int track_size = infos.size();
                final int pageNumberAll = (track_size > MAX_TRACKS_PER_PAGE ? (int) (track_size / MAX_TRACKS_PER_PAGE + 1.0) : 1);
                pageNumber = Math.max(1, Math.min(pageNumberAll, pageNumber));
                if (infos.size() > MAX_TRACKS_PER_PAGE) {
                    infos = infos.subList(Math.max(0, (pageNumber - 1) * MAX_TRACKS_PER_PAGE), Math.min(pageNumber * MAX_TRACKS_PER_PAGE, track_size));
                }
                AtomicLong length_all = new AtomicLong(0);
                infos.stream().forEach((audioInfo) -> {
                    length_all.addAndGet(audioInfo.getTrack().getDuration());
                    tracks.add(buildQueueMessage(audioInfo));
                });
                final String out = tracks.stream().collect(Collectors.joining("\n"));
                tracks.clear();
                infos.clear();
                event.sendMessage(Standard.getMessageEmbed(null, "**CURRENT QUEUE: \"%s\"**%n%n*[%s Tracks | Complete Duration `[ %s ]` | Page %d / %d]*%n%n**Currently Playing ->** %s", queue_name, trackManager.getAudioQueue().size(), getTimestamp(length_all.get()), pageNumber, pageNumberAll, out).build());
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
            } else {
                return; //TODO make it usefull!
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final Message showLiveInfo(MessageEvent event, Guild guild, VoiceChannel channel) { //TODO Add permission control for reactions!
        final TrackManager trackManager = getTrackManager(guild, channel);
        final Message message = event.sendAndWaitMessageFormat(Standard.toBold("LIVE MUSIC INFO"));
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
        final AtomicInteger counter = new AtomicInteger(0);
        final Updateable updateable = new Updateable() {
            @Override
            public long update(long timestamp) {
                final TrackManager trackManager_ = getTrackManager(guild, channel);
                if (trackManager_ == null || trackManager_.getPlayer() == null) {
                    message.delete().queue();
                    return -1;
                }
                final Message message_ = Standard.getUpdatedMessage(message);
                if (message_ == null) {
                    return -1;
                }
                final boolean kill = Standard.isReacted(message_, Emoji.NO);
                final boolean repeat = Standard.isReacted(message_, Emoji.REPEAT);
                final boolean track_previous = Standard.isReacted(message_, Emoji.TRACK_PREVIOUS);
                final boolean rewind = Standard.isReacted(message_, Emoji.REWIND);
                final boolean play_pause = Standard.isReacted(message_, Emoji.PLAY_PAUSE);
                final boolean fast_forward = Standard.isReacted(message_, Emoji.FAST_FORWARD);
                final boolean track_next = Standard.isReacted(message_, Emoji.TRACK_NEXT);
                final boolean volume_none = Standard.isReacted(message_, Emoji.VOLUME_NONE);
                final boolean volume_low = Standard.isReacted(message_, Emoji.VOLUME_LOW);
                final boolean volume_high = Standard.isReacted(message_, Emoji.VOLUME_HIGH);
                final boolean shuffle = Standard.isReacted(message_, Emoji.SHUFFLE);
                final boolean stop = Standard.isReacted(message_, Emoji.STOP);
                final boolean top = Standard.isReacted(message_, Emoji.TOP);
                if (kill) {
                    Standard.removeReaction(message_, Emoji.NO);
                    message.delete().queue();
                    return -1;
                }
                if (stop) {
                    Standard.removeReaction(message_, Emoji.STOP);
                    stop(guild, trackManager);
                    message.delete().queue();
                    return -1;
                }
                if (repeat) {
                    Standard.removeReaction(message_, Emoji.REPEAT);
                    trackManager.toggleLoopType();
                }
                if (play_pause) {
                    Standard.removeReaction(message_, Emoji.PLAY_PAUSE);
                    setPause(guild, !isPaused(guild));
                }
                if (track_next) {
                    Standard.removeReaction(message_, Emoji.TRACK_NEXT);
                    skip(guild);
                } else if (track_previous) {
                    Standard.removeReaction(message_, Emoji.TRACK_PREVIOUS);
                    trackManager.playPrevious();
                }
                if (volume_none) {
                    Standard.removeReaction(message_, Emoji.VOLUME_NONE);
                    setVolume(guild, 0);
                }
                if (volume_low) {
                    Standard.removeReaction(message_, Emoji.VOLUME_LOW);
                    setVolume(guild, getVolume(guild) - 10);
                }
                if (volume_high) {
                    Standard.removeReaction(message_, Emoji.VOLUME_HIGH);
                    setVolume(guild, getVolume(guild) + 10);
                }
                if (shuffle) {
                    Standard.removeReaction(message_, Emoji.SHUFFLE);
                    trackManager.shuffleQueue(1);
                }
                if (top) {
                    Standard.removeReaction(message_, Emoji.TOP);
                    message.delete().queue();
                    showLiveInfo(event, guild, channel);
                    return -1;
                }
                counter.set(counter.get() + 1);
                if (counter.get() >= 5) {
                    counter.set(0);
                } else {
                    return 250;
                }
                AudioTrack track__ = trackManager.getPlayer().getPlayingTrack();
                if (track__ == null) {
                    for (int i = 0; i < 20; i++) {
                        if ((track__ = trackManager.getPlayer().getPlayingTrack()) != null) {
                            break;
                        }
                        try {
                            Thread.sleep(250);
                        } catch (Exception ex) {
                        }
                    }
                }
                if (track__ == null) {
                    message.delete().queue();
                    return -1;
                }
                if (fast_forward) {
                    Standard.removeReaction(message_, Emoji.FAST_FORWARD);
                    if (track__.isSeekable()) {
                        track__.setPosition(Math.min(track__.getPosition() + 10000, track__.getDuration()));
                    }
                } else if (rewind) {
                    Standard.removeReaction(message_, Emoji.REWIND);
                    if (track__.isSeekable()) {
                        track__.setPosition(Math.max(0, track__.getPosition() - 5000));
                    }
                }
                final AudioTrackInfo audioTrackInfo__ = track__.getInfo();
                if (audioTrackInfo__ == null) {
                    message.delete().queue();
                    return -1;
                }
                final AudioQueue queue = trackManager.getAudioQueue();
                final AudioInfo next = queue.getNext();
                message.editMessage(Standard.getMessageEmbed(null, Standard.toBold("LIVE MUSIC INFO:"))
                        .addField("Title", audioTrackInfo__.title, false)
                        .addField("Duration", String.format("`[%s / %s]`", getTimestamp(track__.getPosition()), getTimestamp(track__.getDuration())), false)
                        .addField("Author", audioTrackInfo__.author, false)
                        .addField("Next Track", (next != null ? next.getTrack().getInfo().title : "None"), false)
                        .addField("Volume", getVolume(guild) + "%", false)
                        .addField("Status", String.format("%s, %s", isPaused(guild) ? "Paused" : "Playing", trackManager.getLoopType().getText()), false)
                        .build()).queue();
                return 250;
            }

            @Override
            public void delete() {
                message.delete().complete();
            }
        };
        Updater.addUpdateable(updateable);
        return message;
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s %s [[URL or Text] [Maximum Number of Tracks]] [%s]", invoker, Standard.ARGUMENT_PLAY.getCompleteArgument(0, -1), Standard.ARGUMENT_LIVE.getCompleteArgument(0, -1)), "Unpauses the bot or plays from an URL or searches on YouTube for a video. If loading more than one video you can set the maximum number of videos that should be loaded, or -1 for all the bot can find. Optionally shows a live track info.", false);
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
    public final PermissionRoleFilter getPermissionRoleFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_VIP;
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
