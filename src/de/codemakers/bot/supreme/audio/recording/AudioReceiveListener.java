package de.codemakers.bot.supreme.audio.recording;

import de.codemakers.bot.supreme.audio.util.AudioEncoder;
import java.util.Arrays;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * AudioReceiveListener
 *
 * @author Panzer1119
 */
public class AudioReceiveListener implements AudioReceiveHandler {

    public static final double BYTES_PER_ARRAY = 3840;
    public static final double ARRAY_PER_SECOND = 50;
    public static final double SECONDS = 60;

    public static final double STARTING_MB = 0.5;
    public static final int CAP_MB = 16;
    public static final double PCM_MINS = 2.0;
    public boolean canReceive = true;
    public double volume = 1.0;
    private final VoiceChannel voiceChannel;

    public byte[] uncompiledVoiceData = new byte[(int) (BYTES_PER_ARRAY * ARRAY_PER_SECOND * SECONDS * PCM_MINS)]; //3840 byte/array * 50 arrays/second * 60 second * 2 = 2 MINS
    public int uncompiledVoiceDataIndex = 0;

    public byte[] compiledVoiceData = new byte[(int) (1024 * 1024 * STARTING_MB)]; //Start with 0.5 MB
    public int compiledVoiceDataIndex = 0;

    public boolean overwriting = false;

    public AudioReceiveListener(VoiceChannel voiceChannel, double volume) {
        this.voiceChannel = voiceChannel;
        this.volume = volume;
    }

    @Override
    public final boolean canReceiveCombined() {
        return canReceive;
    }

    @Override
    public final boolean canReceiveUser() {
        return false;
    }

    @Override
    public final void handleCombinedAudio(CombinedAudio combinedAudio) {
        if ((uncompiledVoiceDataIndex == (uncompiledVoiceData.length / 2)) || (uncompiledVoiceDataIndex == uncompiledVoiceData.length)) {
            final int uncompiledVoiceDataIndex_final = uncompiledVoiceDataIndex;
            final Thread thread = new Thread(() -> {
                if (uncompiledVoiceDataIndex_final < (uncompiledVoiceData.length / 2)) { //1. Half
                    addCompiledVoiceData(AudioEncoder.encodePcmToMp3(Arrays.copyOfRange(uncompiledVoiceData, 0, uncompiledVoiceData.length / 2)));
                } else { //2. Half
                    addCompiledVoiceData(AudioEncoder.encodePcmToMp3(Arrays.copyOfRange(uncompiledVoiceData, uncompiledVoiceData.length / 2, uncompiledVoiceData.length)));
                }
            });
            thread.start();
            if (uncompiledVoiceDataIndex == uncompiledVoiceData.length) {
                uncompiledVoiceDataIndex = 0;
            }
        }
        for (byte b : combinedAudio.getAudioData(volume)) {
            uncompiledVoiceData[uncompiledVoiceDataIndex++] = b;
        }
    }

    public final byte[] getVoiceData() {
        canReceive = false;
        final byte[] remaining = new byte[uncompiledVoiceDataIndex];
        final int start = ((uncompiledVoiceDataIndex < (uncompiledVoiceData.length / 2)) ? 0 : uncompiledVoiceData.length / 2);
        for (int i = 0; i < (uncompiledVoiceDataIndex - start); i++) {
            remaining[i] = uncompiledVoiceData[start + i];
        }
        addCompiledVoiceData(AudioEncoder.encodePcmToMp3(remaining));
        byte[] orderedVoiceData;
        if (overwriting) {
            orderedVoiceData = new byte[compiledVoiceData.length];
        } else {
            orderedVoiceData = new byte[compiledVoiceDataIndex + 1];
            compiledVoiceDataIndex = 0;
        }
        for (int i = 0; i < orderedVoiceData.length; i++) {
            if ((compiledVoiceDataIndex + i) < orderedVoiceData.length) {
                orderedVoiceData[i] = compiledVoiceData[compiledVoiceDataIndex + i];
            } else {
                orderedVoiceData[i] = compiledVoiceData[compiledVoiceDataIndex + i - orderedVoiceData.length];
            }
        }
        wipeMemory();
        canReceive = true;
        return orderedVoiceData;
    }

    public final void addCompiledVoiceData(byte[] compressed) {
        for (byte b : compressed) {
            if ((compiledVoiceDataIndex >= compiledVoiceData.length) && (compiledVoiceData.length != (1024 * 1024 * CAP_MB))) { //cap at 16MB
                final byte[] temp = new byte[compiledVoiceData.length * 2];
                for (int i = 0; i < compiledVoiceData.length; i++) {
                    temp[i] = compiledVoiceData[i];
                }
                compiledVoiceData = temp;
            } else if ((compiledVoiceDataIndex >= compiledVoiceData.length) && (compiledVoiceData.length == (1024 * 1024 * CAP_MB))) {
                compiledVoiceDataIndex = 0;
                if (!overwriting) {
                    overwriting = true;
                    System.err.println(String.format("Hit compressed storage cap in \"%s\" on \"%s\"", voiceChannel.getName(), voiceChannel.getGuild().getName()));
                }
            }
            compiledVoiceData[compiledVoiceDataIndex++] = b;
        }
    }

    public final void wipeMemory() {
        System.out.println(String.format("Deleted recorded Data from \"%s\" on \"%s\"", voiceChannel.getName(), voiceChannel.getGuild().getName()));
        uncompiledVoiceDataIndex = 0;
        compiledVoiceDataIndex = 0;
        compiledVoiceData = new byte[(int) (1024 * 1024 * STARTING_MB)];
        System.gc(); //TODO Fraglich!
    }

    public final byte[] getUncompiledVoiceData(int time) {
        canReceive = false;
        if (time > (PCM_MINS * 60 * 2)) { //2 Minutes
            time = (int) (PCM_MINS * 60 * 2); //FIXME Make this variable
        }
        final int requestSize = (int) (BYTES_PER_ARRAY * ARRAY_PER_SECOND * time);
        final byte[] voiceData = new byte[requestSize];
        for (int i = 0; i < voiceData.length; i++) {
            if ((uncompiledVoiceDataIndex + i) < voiceData.length) {
                voiceData[i] = uncompiledVoiceData[uncompiledVoiceDataIndex + i];
            } else {
                voiceData[i] = uncompiledVoiceData[uncompiledVoiceDataIndex + i - voiceData.length];
            }
        }
        wipeMemory();
        canReceive = true;
        return null;
    }

    @Override
    public final void handleUserAudio(UserAudio userAudio) {
    }

}
