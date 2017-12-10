package de.codemakers.bot.supreme.audio.util;

import de.codemakers.bot.supreme.audio.recording.AudioReceiveListener;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import de.codemakers.io.file.AdvancedFile;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import javax.sound.sampled.AudioFormat;
import net.dv8tion.jda.core.entities.Guild;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;

/**
 * AudioEncoder
 *
 * @author Panzer1119
 */
public class AudioEncoder {

    /**
     * Encodes the passed array of PCM (uncompressed) audio to mp3 audio data
     *
     * @param pcm Input
     * @return Output
     */
    public static byte[] encodePcmToMp3(byte[] pcm) {
        final LameEncoder encoder = new LameEncoder(new AudioFormat(48000.0f, 16, 2, true, true), 128, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);
        final ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        final byte[] buffer = new byte[encoder.getPCMBufferSize()];
        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
            currentPcmPosition += bytesToTransfer;
            bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);
            mp3.write(buffer, 0, bytesWritten);
        }
        encoder.close();
        return mp3.toByteArray();
    }

    public static void writeToFile(MessageEvent event) {
        writeToFile(event, -1);
    }

    public static void writeToFile(MessageEvent event, int time) {
        if (event == null) {
            return;
        }
        final Guild guild = event.getGuild();
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild);
        final AudioReceiveListener audioReceiveListener = (AudioReceiveListener) guild.getAudioManager().getReceiveHandler();
        if (audioReceiveListener == null) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, i wasn't recording!", Emoji.WARNING, event.getAuthor().getAsMention());
            return;
        }
        final AdvancedFile destination = Util.generateRandomAdvancedFile(advancedGuild.getFolder(), 13, "", ".mp3");
        try {
            byte[] voiceData;
            if (time > 0 && time <= AudioReceiveListener.PCM_MINS * 60 * 2) {
                voiceData = audioReceiveListener.getUncompiledVoiceData(time);
                voiceData = encodePcmToMp3(voiceData);
            } else {
                voiceData = audioReceiveListener.getVoiceData();
            }
            FileOutputStream fos = (FileOutputStream) destination.createOutputstream(false);
            fos.write(voiceData);
            fos.close();
            final double length = destination.toFile().length() / 1000.0 / 1000.0; //FIXME WTF THIS WAS 1024, but Megabyte == 1.000.000 Byte
            System.out.println(String.format("Saving Audio File \"%s\" from \"%s\" on \"%s\" (Size: %f MB)", destination.getName(), guild.getAudioManager().getConnectedChannel().getName(), guild.getName(), length));
            if (length < 8) { //FIXME Is the 8 right?
                event.sendFile(destination.toFile(), destination.getName(), null);
                final Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(1000 * 20);
                    } catch (Exception ex) {
                    }    //20 second life for files set to discord (no need to save)
                    //destination.delete(); //FIXME Need?
                });
                thread.start();
            } else {
                /**
                 * sendMessage(tc, "http://DiscordEcho.com/" + dest.getName());
                 * new Thread(() -> { try { sleep(1000 * 60 * 60); } catch
                 * (Exception ex) { } //1 hour life for files stored on web
                 * server dest.delete(); System.out.println("\tDeleting file " +
                 * dest.getName() + "..."); }).start();
                 */
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, some Error occurred!", Emoji.WARNING, event.getAuthor().getAsMention());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
