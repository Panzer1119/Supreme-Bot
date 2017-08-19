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
import de.codemakers.bot.supreme.util.Timer;
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

/**
 * MusicCommand
 *
 * @author Panzer1119
 */
public class MusicCommand extends Command {

    private static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private static final HashMap<Guild, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>(); //TODO Was wenn mehrere Player laufen sollen???

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
        if (hasPlayer(guild)) {
            return players.get(guild).getValue();
        } else {
            return null;
        }
    }

    private final boolean isIdle(Guild guild) {
        return !hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null;
    }

    private final MusicCommand loadTrack(String identifier, Member author, Message message) {
        final Guild guild = author.getGuild();
        final AudioPlayer player = getPlayer(guild);
        manager.setFrameBufferDuration(5000); //FIXME Make this variable (ms)
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
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        final boolean play = arguments.isConsumed(Standard.ARGUMENT_PLAY, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean skip = arguments.isConsumed(Standard.ARGUMENT_SKIP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean shuffle = arguments.isConsumed(Standard.ARGUMENT_SHUFFLE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean loop = arguments.isConsumed(Standard.ARGUMENT_LOOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_INFO, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean queue = arguments.isConsumed(Standard.ARGUMENT_QUEUE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (play) {
            return arguments.isSize(2); //title/url
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
        } else {
            return false;
        }
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean play = arguments.isConsumed(Standard.ARGUMENT_PLAY, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean skip = arguments.isConsumed(Standard.ARGUMENT_SKIP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean shuffle = arguments.isConsumed(Standard.ARGUMENT_SHUFFLE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean loop = arguments.isConsumed(Standard.ARGUMENT_LOOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_INFO, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean queue = arguments.isConsumed(Standard.ARGUMENT_QUEUE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final Guild guild = event.getGuild();
        final TrackManager manager_ = getManager(guild);
        try {
            if (play) {
                final String input = arguments.consumeFirst();
                final boolean url = (input.startsWith("http://") || input.startsWith("https://"));
                loadTrack((url ? "" : "ytsearch: ") + input, event.getMember(), event.getMessage());
            } else if (skip) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no Tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                final boolean all = arguments.isConsumed(Standard.ARGUMENT_ALL, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                if (all) {
                    manager_.purgeQueue();
                    skip(guild);
                } else {
                    final int times = (arguments.isEmpty() ? 1 : Integer.parseInt(arguments.consumeFirst()));
                    for (int i = 0; i < times; i++) {
                        skip(guild);
                    }
                }
           } else if (stop) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no Tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                } else if (manager_ == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no Player started!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                manager_.purgeQueue();
                skip(guild);
                guild.getAudioManager().closeAudioConnection();
            } else if (shuffle) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no Tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                } else if (manager_ == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no Player started!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                int times = 1;
                try {
                    times = (arguments.isEmpty() ? 1 : Integer.parseInt(arguments.consumeFirst()));
                } catch (Exception ex) {
                }
                for (int i = 0; i < times; i++) {
                    manager_.shuffleQueue();
                }
            } else if (loop) {
                if (manager_ == null) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there is no Player started!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                if (arguments.isEmpty()) {
                    manager_.setLoop(!manager_.isLoop());
                } else {
                    try {
                        manager_.setLoop(Boolean.parseBoolean(arguments.consumeFirst()));
                    } catch (Exception ex) {
                        manager_.setLoop(!manager_.isLoop());
                    }
                }
            } else if (info) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no Tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                final boolean live = arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                final AudioPlayer player = getPlayer(guild);
                final AudioTrack track = player.getPlayingTrack();
                final AudioTrackInfo trackInfo = track.getInfo();
                if (!live) {
                    event.sendMessage(Standard.getMessageEmbed(null, "**CURRENT TRACK INFO:**").addField("Title", trackInfo.title, false).addField("Duration", String.format("`[%s / %s]`", getTimestamp(track.getPosition()), getTimestamp(track.getDuration())), false).addField("Author", trackInfo.author, false).build());
                } else {
                    final Message message = event.sendAndWaitMessage("**LIVE TRACK INFO**");
                    Util.sheduleTimerAtFixedRateAndRemove(() -> {
                        message.editMessage(Standard.getMessageEmbed(null, "**LIVE TRACK INFO:**").addField("Title", trackInfo.title, false).addField("Duration", String.format("`[%s / %s]`", getTimestamp(track.getPosition()), getTimestamp(track.getDuration())), false).addField("Author", trackInfo.author, false).build()).queue();
                    }, () -> {
                        message.delete().queue();
                    }, 0, 2000, track.getDuration() - track.getPosition());
                }
            } else if (queue) {
                if (isIdle(guild)) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, there are no Tracks waiting!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return;
                }
                int pageNumber = 1;
                try {
                    pageNumber = (arguments.isEmpty() ? 1 : Integer.parseInt(arguments.consumeFirst()));
                } catch (Exception ex) {
                }
                final ArrayList<String> tracks = new ArrayList<>();
                List<AudioInfo> infos = new ArrayList<>(manager_.getQueue());
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
                event.sendMessage(Standard.getMessageEmbed(null, "**CURRENT QUEUE \"%s\"**%n%n*[%s Tracks | Duration `[ %s ]` | Page %d / %d]*%n%n%s", queue_name, manager_.getQueue().size(), getTimestamp(length_all.get()), pageNumber, pageNumberAll, out).build());
            } else {
                return; //TODO make it usefull!
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
        builder.addField(String.format("%s %s <URL or Text>", invoker, Standard.ARGUMENT_PLAY.getCompleteArgument(0, -1)), "Plays from an URL or searches on YouTube for a video.", false);
        builder.addField(String.format("%s %s [Times/All]", invoker, Standard.ARGUMENT_SKIP.getCompleteArgument(0, -1)), "Skips 1 or more or even all tracks.", false);
        builder.addField(String.format("%s %s", invoker, Standard.ARGUMENT_STOP.getCompleteArgument(0, -1)), "Stops the music.", false);
        builder.addField(String.format("%s %s [Times]", invoker, Standard.ARGUMENT_SHUFFLE.getCompleteArgument(0, -1)), "Shuffles 1 or more times the queue.", false);
        builder.addField(String.format("%s %s [Loop]", invoker, Standard.ARGUMENT_LOOP.getCompleteArgument(0, -1)), "Toggles or sets looping.", false);
        builder.addField(String.format("%s %s [Playlist]", invoker, Standard.ARGUMENT_INFO.getCompleteArgument(0, -1)), "Shows info about current queue or playlist.", false);
        builder.addField(String.format("%s %s [Page]", invoker, Standard.ARGUMENT_QUEUE.getCompleteArgument(0, -1)), "Shows tracks in current queue and page.", false);
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
