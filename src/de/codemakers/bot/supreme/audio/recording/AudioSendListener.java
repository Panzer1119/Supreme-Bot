package de.codemakers.bot.supreme.audio.recording;

import java.util.Arrays;
import net.dv8tion.jda.core.audio.AudioSendHandler;

/**
 * AudioSendListener
 *
 * @author Panzer1119
 */
public class AudioSendListener implements AudioSendHandler {

    public byte[][] voiceData;
    public boolean canProvide = true;
    int index = 0;

    public AudioSendListener(byte[] data) {
        voiceData = new byte[(int) (data.length / AudioReceiveListener.BYTES_PER_ARRAY)][(int) AudioReceiveListener.BYTES_PER_ARRAY];
        for (int i = 0; i < voiceData.length; i++) {
            voiceData[i] = Arrays.copyOfRange(data, (int) (i * AudioReceiveListener.BYTES_PER_ARRAY), (int) (i * AudioReceiveListener.BYTES_PER_ARRAY + AudioReceiveListener.BYTES_PER_ARRAY));
        }
    }

    @Override
    public boolean canProvide() {
        return canProvide;
    }

    @Override
    public byte[] provide20MsAudio() {
        if (index == voiceData.length - 1) {
            canProvide = false;
        }
        return voiceData[index++];
    }

    @Override
    public boolean isOpus() {
        return false;
    }

}
