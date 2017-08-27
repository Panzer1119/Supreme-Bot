package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.audio.recording.AudioReceiveListener;
import de.codemakers.bot.supreme.audio.recording.AudioSendListener;
import de.codemakers.bot.supreme.audio.util.AudioEncoder;
import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * AudioRecorderCommand
 *
 * @author Panzer1119
 */
public class AudioRecorderCommand extends Command { //FIXME Was ist wenn Music abgespielt wird???

    public static final String RECORDER_AUDIO_AUTO_SAVE = "recorder_audio_auto_save";
    public static final String RECORDER_AUDIO_VOLUME = "recorder_audio_volume";

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("recorder", this), Invoker.createInvoker("rc", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty() || event.isPrivate()) {
            return false;
        }
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_START, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (start) {
            return arguments.isSize(1, 2); //[VoiceChannel ID]
        } else if (stop) {
            return arguments.isSize(1);
        } else {
            return false;
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_START, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (start) {
            final VoiceChannel voiceChannel = (arguments.isEmpty() ? event.getMember().getVoiceState().getChannel() : (arguments.getFirst().startsWith("#") ? event.getGuild().getVoiceChannelsByName(arguments.consumeFirst().substring("#".length()), false).stream().findFirst().orElse(null) : event.getGuild().getVoiceChannelById(arguments.consumeFirst())));
            if (voiceChannel != null) {
                if (event.getGuild().getAudioManager().isConnected()) {
                    if (event.getGuild().getAudioManager().getConnectedChannel() == voiceChannel) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, i'm already in your VoiceChannel!", Emoji.WARNING, event.getAuthor().getAsMention());
                        return;
                    } else if (advancedGuild.getSettings().getProperty(RECORDER_AUDIO_AUTO_SAVE, false)) {
                        AudioEncoder.writeToFile(event);
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, you first have to leave the other VoiceChannel!", Emoji.WARNING, event.getAuthor().getAsMention());
                        return;
                    }
                }
                try {
                    voiceChannel.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                } catch (Exception ex) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, i can't join the VoiceChannel \"%s\"!", Emoji.WARNING, event.getAuthor().getAsMention(), voiceChannel.getName());
                    return;
                }
                final double volume = advancedGuild.getSettings().getProperty(RECORDER_AUDIO_VOLUME, 0.8);
                voiceChannel.getGuild().getAudioManager().setReceivingHandler(new AudioReceiveListener(voiceChannel, volume));
            } else if (event.getMember().getVoiceState().getChannel() == null) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, you need to be in a VoiceChannel!", Emoji.WARNING, event.getAuthor().getAsMention());
            } else {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, your VoiceChannel couldn't be found!", Emoji.WARNING, event.getAuthor().getAsMention());
            }
        } else if (stop) {
            if (!event.getGuild().getAudioManager().isConnected()) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, i'm not in a VoiceChannel!", Emoji.WARNING, event.getAuthor().getAsMention());
                return;
            }
            if (advancedGuild.getSettings().getProperty(RECORDER_AUDIO_AUTO_SAVE, false)) {
                AudioEncoder.writeToFile(event);
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, you first have to leave the other VoiceChannel!", Emoji.WARNING, event.getAuthor().getAsMention());
                return;
            }
            event.getGuild().getAudioManager().closeAudioConnection();
            killAudioHandlers(event.getGuild());
        }
    }

    public static void killAudioHandlers(Guild guild) {
        final AudioReceiveListener audioReceiveListener = (AudioReceiveListener) guild.getAudioManager().getReceiveHandler();
        if (audioReceiveListener != null) {
            audioReceiveListener.canReceive = false;
            audioReceiveListener.compiledVoiceData = null;
            guild.getAudioManager().setReceivingHandler(null);
        }
        final AudioSendListener audioSendListener = (AudioSendListener) guild.getAudioManager().getSendingHandler();
        if (audioSendListener != null) {
            audioSendListener.canProvide = false;
            audioSendListener.voiceData = null;
            guild.getAudioManager().setSendingHandler(null);
        }
        System.out.println(String.format("Destroyed AudioHandlers for \"%s\"", guild.getName()));
        System.gc(); //FIXME Fraglich!
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s %s [VoiceChannel ID / #VoiceChannel Name]", invoker, Standard.ARGUMENT_START.getCompleteArgument(0, -1)), "Moves the Bot to your or the given VoiceChannel and starts recording.", false);
        builder.addField(String.format("%s %s", invoker, Standard.ARGUMENT_STOP.getCompleteArgument(0, -1)), "Removes the Bot out of the VoiceChannel and stops recording.", false);
        return builder;
    }

    @Override
    public PermissionRoleFilter getPermissionRoleFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_ADMIN_BOT_COMMANDER;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_NORMAL;
    }

}
