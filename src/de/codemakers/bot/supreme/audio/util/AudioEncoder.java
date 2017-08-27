package de.codemakers.bot.supreme.audio.util;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
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

}
