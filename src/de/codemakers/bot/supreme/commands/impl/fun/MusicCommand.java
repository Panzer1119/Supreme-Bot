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
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final HashMap<Guild, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>();

    public MusicCommand() {
        AudioSourceManagers.registerRemoteSources(manager);
    }

    private final Map.Entry<AudioPlayer, TrackManager> createPlayer(Guild guild, VoiceChannel channel) {
        if (guild == null) {
            return null;
        }
        final AudioPlayer player = manager.createPlayer();
        final TrackManager trackManager = new TrackManager(player, guild, channel);
        final Map.Entry<AudioPlayer, TrackManager> player_ = new AbstractMap.SimpleEntry<>(player, trackManager);
        player.addListener(trackManager);
        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(player));
        players.put(guild, player_);
        return player_;
    }
    
    private final Map.Entry<AudioPlayer, TrackManager> getPlayer(Guild guild, VoiceChannel channel) {
        if (guild == null) {
            return null;
        }
        final Map.Entry<AudioPlayer, TrackManager> player = getPlayer(guild);
        if (player != null) {
            return player;
        } else {
            return createPlayer(guild, channel);
        }
    }
    
    private final Map.Entry<AudioPlayer, TrackManager> getPlayer(Guild guild) {
        if (guild == null) {
            return null;
        }
        return players.get(guild);
    }

    private final boolean isIdle(Guild guild) {
        if (guild == null) {
            return true;
        }
        final Map.Entry<AudioPlayer, TrackManager> player = getPlayer(guild);
        return player == null || player.getKey() == null || player.getKey().getPlayingTrack() == null;
    }

    private final MusicCommand loadTrack(String identifier, MessageEvent event, VoiceChannel channel) {
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
        final Map.Entry<AudioPlayer, TrackManager> player = getPlayer(guild, channel);
        if (player == null) {
            return null;
        }
        manager.setFrameBufferDuration(5000); //FIXME Make this variable (ms)
        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                try {
                    player.getValue().queue(track, author);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                final int max = Math.min(tracks.size(), Standard.PLAYLIST_LIMIT);
                final TrackManager manager_ = player.getValue();
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
        if (guild == null) {
            return this;
        }
        final Map.Entry<AudioPlayer, TrackManager> player = getPlayer(guild);
        if (player == null || player.getKey() == null) {
            return this;
        }
        player.getKey().stopTrack();
        return this;
    }
    
    private final boolean setPause(Guild guild, boolean pause) {
        if (guild == null) {
            return false;
        }
        final Map.Entry<AudioPlayer, TrackManager> player = getPlayer(guild);
        if (player == null || player.getKey() == null) {
            return false;
        }
        if (player.getKey().isPaused() == pause) {
            return false;
        }
        player.getKey().setPaused(pause);
        return true;
    }
    
    private final boolean isPaused(Guild guild) {
        if (guild == null) {
            return false;
        }
        final Map.Entry<AudioPlayer, TrackManager> player = getPlayer(guild);
        if (player == null || player.getKey() == null) {
            return false;
        }
        return player.getKey().isPaused();
    }
    
    private final boolean setVolume(Guild guild, int volume) {
        if (guild == null) {
            return false;
        }
        final Map.Entry<AudioPlayer, TrackManager> player = getPlayer(guild);
        if (player == null || player.getKey() == null) {
            return false;
        }
        if (player.getKey().getVolume() == volume) {
            return false;
        }
        player.getKey().setVolume(volume);
        return true;
    }
    
    private final int getVolume(Guild guild) {
        if (guild == null) {
            return -1;
        }
        final Map.Entry<AudioPlayer, TrackManager> player = getPlayer(guild);
        if (player == null || player.getKey() == null) {
            return -1;
        }
        return player.getKey().getVolume();
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
                return arguments.isSize(3); //title/url -live
            } else {
                return arguments.isSize(1, 2); //[title/url]
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
            return arguments.isSize(1, 2); //[toggle]
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
        final Map.Entry<AudioPlayer, TrackManager> player = getPlayer(guild, channel);
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
                    loadTrack((url ? "" : "ytsearch: ") + input, event, channel);
                    if (live) {
                        Util.sheduleTimerAndRemove(() -> {
                            final AudioPlayer player_ = player.getKey();
                            final AudioTrack track = player_.getPlayingTrack();
                            final AudioTrackInfo trackInfo = track.getInfo();
                            showLiveInfo(invoker, event, guild, channel, track, trackInfo);
                        }, 5000); //TODO Make this variable (ms) ???
                    }
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
                    player.getValue().purgeQueue();
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
                } else if (player.getValue() == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no player running!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                player.getValue().purgeQueue();
                skip(guild);
                guild.getAudioManager().closeAudioConnection();
            } else if (shuffle) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                } else if (player.getValue() == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no player running!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                int times = 1;
                try {
                    times = (arguments.isEmpty() ? 1 : Integer.parseInt(arguments.consumeFirst()));
                } catch (Exception ex) {
                }
                for (int i = 0; i < times; i++) {
                    player.getValue().shuffleQueue();
                }
            } else if (loop) {
                if (player.getValue() == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no player running!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                if (arguments.isEmpty()) {
                    player.getValue().setLoop(!player.getValue().isLoop());
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s toggled music looping mode to \"%b\".", event.getAuthor().getAsMention(), player.getValue().isLoop());
                } else {
                    try {
                        player.getValue().setLoop(Boolean.parseBoolean(arguments.consumeFirst()));
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s setted music looping mode to \"%b\".", event.getAuthor().getAsMention(), player.getValue().isLoop());
                    } catch (Exception ex) {
                        player.getValue().setLoop(!player.getValue().isLoop());
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s toggled music looping mode to \"%b\".", event.getAuthor().getAsMention(),  player.getValue().isLoop());
                    }
                }
            } else if (info) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                final boolean live = arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                final AudioPlayer player_ = player.getKey();
                final AudioTrack track = player_.getPlayingTrack();
                final AudioTrackInfo trackInfo = track.getInfo();
                if (!live) {
                    event.sendMessage(Standard.getMessageEmbed(null, "**CURRENT TRACK INFO:**").addField("Title", trackInfo.title, false).addField("Duration", String.format("`[%s / %s]`", getTimestamp(track.getPosition()), getTimestamp(track.getDuration())), false).addField("Author", trackInfo.author, false).build());
                } else {
                    showLiveInfo(invoker, event, guild, channel, track, trackInfo);
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
                List<AudioInfo> infos = new ArrayList<>(player.getValue().getQueue());
                final int MAX_TRACKS_PER_PAGE = 20; //FIXME Make this variable (Anzahl Tracks pro Seite)
                final String queue_name = "default"; //TODO Add queues ability to save them
                //TODO Alle Commands muessen methode machen mit stop, damit sie herunterfahren koennen
                if (infos.size() > MAX_TRACKS_PER_PAGE) {
                    infos = infos.subList((pageNumber - 1) * MAX_TRACKS_PER_PAGE, pageNumber * MAX_TRACKS_PER_PAGE);
                }
                AtomicLong length_all = new AtomicLong(0);
                infos.stream().forEach((audioInfo) -> {
                    length_all.addAndGet(audioInfo.getTrack().getDuration());
                    tracks.add(buildQueueMessage(audioInfo));
                });
                final String out = tracks.stream().collect(Collectors.joining("\n"));
                final int pageNumberAll = tracks.size() >= MAX_TRACKS_PER_PAGE ? tracks.size() / MAX_TRACKS_PER_PAGE : 1;
                tracks.clear();
                infos.clear();
                event.sendMessage(Standard.getMessageEmbed(null, "**CURRENT QUEUE: \"%s\"**%n%n*[%s Tracks | Duration `[ %s ]` | Page %d / %d]*%n%n%s", queue_name, player.getValue().getQueue().size(), getTimestamp(length_all.get()), pageNumber, pageNumberAll, out).build());
            } else if (volume) {
                if (player.getValue() == null) {
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
    
    private final Message showLiveInfo(Invoker invoker, MessageEvent event, Guild guild, VoiceChannel channel, AudioTrack track, AudioTrackInfo trackInfo) {
        final Message message = event.sendAndWaitMessageFormat("**LIVE TRACK INFO**"); //TODO Add some option for showing this forever and then to kill it
        Util.sheduleTimerAtFixedRateAndRemove(() -> {
            final AudioPlayer player__ = getPlayer(guild, channel).getKey();
            if (player__ == null) {
                return false;
            }
            final AudioTrack track_ = player__.getPlayingTrack();
            if (track_ == null) {
                return false;
            }
            final AudioTrackInfo trackInfo_ = track.getInfo();
            if (trackInfo_ == null) {
                return false;
            }
            message.editMessage(Standard.getMessageEmbed(null, "**LIVE TRACK INFO:**").addField("Title", trackInfo.title, false).addField("Duration", String.format("`[%s / %s]`", getTimestamp(track_.getPosition()), getTimestamp(track_.getDuration())), false).addField("Author", trackInfo_.author, false).build()).queue();
            return true;
        }, () -> {
            message.delete().queue();
            try {
                Thread.sleep(1000);
                if (!isIdle(guild)) {
                    action(invoker, new ArgumentList(Standard.ARGUMENT_INFO.getCompleteArgument(0, -1), Standard.ARGUMENT_LIVE.getCompleteArgument(0, -1)), event); //FIXME Fix this
                }
            } catch (Exception ex) {
                ex.printStackTrace(); //FIXME REMOVE THIS LINE!!!
            }
        }, 0, 2000, track.getDuration() - track.getPosition());
        //TODO Add volume control to the live track info
        return message;
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s %s [URL or Text] [%s]", invoker, Standard.ARGUMENT_PLAY.getCompleteArgument(0, -1), Standard.ARGUMENT_LIVE.getCompleteArgument(0, -1)), "Unpauses the bot or plays from an URL or searches on YouTube for a video. Optionally shows a live track info.", false);
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
        return null; //FIXME Change this
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

}
