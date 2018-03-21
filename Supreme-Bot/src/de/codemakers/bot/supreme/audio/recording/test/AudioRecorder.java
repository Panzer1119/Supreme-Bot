/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.codemakers.bot.supreme.audio.recording.test;

import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.logger.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 * AudioRecorder
 *
 * @author Panzer1119
 */
public class AudioRecorder implements AudioReceiveHandler {

    public static final File folder = new File(Standard.STANDARD_DATA_FOLDER.getPath() + File.separator + "recordings");

    public static final int BYTES_PER_ARRAY = 3840;
    public static final double ARRAYS_PER_SECOND = 1000.0 / 20.0;
    public static final double SECONDS = 5.0;
    public static final int BUFFER_LENGTH = (int) (BYTES_PER_ARRAY * ARRAYS_PER_SECOND * SECONDS);
    public static final int STEP_LENGTH = BYTES_PER_ARRAY;
    public static final int STEPS = BUFFER_LENGTH / STEP_LENGTH;

    private final byte[] buffer = new byte[BUFFER_LENGTH];
    private int counter = 0;
    private final File file = new File(folder.getPath() + File.separator + "audio_recording.pcm");
    private FileOutputStream fos;

    static {
        folder.mkdir();
    }

    public AudioRecorder() {
        try {
            fos = new FileOutputStream(file, false);
        } catch (FileNotFoundException ex) {
            Logger.logErr("FOS: " + ex, ex);
        }
    }

    public boolean recording = true;

    public void close() {
        try {
            fos.close();
        } catch (Exception ex) {
            Logger.logErr("Closing: " + ex, ex);
        }
    }

    @Override
    public boolean canReceiveCombined() {
        return recording;
    }

    @Override
    public boolean canReceiveUser() {
        return recording;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        try {
            fos.write(combinedAudio.getAudioData(1.0));
        } catch (Exception ex) {
            Logger.logErr("...." + ex, ex);
        }
        /*
        if (counter >= STEPS) {
            return;
        }
        if (counter == STEPS - 1) {
            try {
                final ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                final AudioInputStream ais = new AudioInputStream(bais, OUTPUT_FORMAT, buffer.length);
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
                ais.close();
                Logger.log("audio recording saved to %s", file);
                counter++;
                recording = false;
            } catch (Exception ex) {
                Logger.logErr("Error while handling combined audio: " + ex, ex);
            }
            return;
        }
        final byte[] data = combinedAudio.getAudioData(1.0);
        long temp = 0;
        for (byte b : data) {
            temp += b;
        }
        if (temp > 0) {
            Logger.log(Arrays.toString(data));
        }
        System.arraycopy(data, 0, buffer, counter * STEP_LENGTH, data.length);
        counter++;
         */
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {
        Logger.log("User: %s -> %s", userAudio.getUser(), Arrays.toString(userAudio.getAudioData(1.0)));
    }

}
