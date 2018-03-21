package de.codemakers.bot.supreme.audio.util;

import de.codemakers.bot.supreme.audio.core.AudioInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * AudioQueue
 *
 * @author Panzer1119
 */
public abstract class AudioQueue {

    private final List<AudioInfo> queue = Collections.synchronizedList(new ArrayList<>());
    private int pointer = -1;

    public AudioQueue(AudioInfo... infos) {
        add(infos);
    }

    public abstract boolean isPlaying();

    public final boolean hasNext() {
        return correctPointer() && ((pointer + 1) < queue.size());
    }

    public final boolean hasTrack() {
        return queue.size() > 0;
    }

    public final boolean hasPrevious() {
        return correctPointer() && (pointer >= 0);
    }

    public final boolean isStart() {
        return correctPointer() && (pointer == -1);
    }

    public final boolean isEnd() {
        return !correctPointer() || ((pointer + 1) >= queue.size());
    }

    public final boolean isEmpty() {
        return queue.isEmpty();
    }

    public final int size() {
        return queue.size();
    }

    public final boolean setPointer(int pointer) {
        this.pointer = pointer;
        return correctPointer();
    }

    public final boolean reset() {
        return setPointer(-1);
    }

    /**
     * Returns an AudioInfo
     *
     * @param delta (0 = Now, positive = future, negative = past)
     * @return AudioInfo
     */
    public final AudioInfo get(int delta) {
        if (correctPointer(pointer + delta) != (pointer + delta)) {
            return null;
        }
        if (!correctPointer()) {
            return null;
        }
        return queue.get(pointer + delta);
    }

    /**
     * This is only for getting the next AudioInfo, for playing it use playNext
     *
     * @return AudioInfo
     */
    public final AudioInfo getNext() {
        return get(1);
    }

    public final AudioInfo getNow() {
        return get(0);
    }

    /**
     * This is only for getting the previous AudioInfo, for playing it use
     * playPrevious
     *
     * @return AudioInfo
     */
    public final AudioInfo getPrevious() {
        return get(-1);
    }

    public final AudioInfo play(int delta) {
        pointer += delta;
        return getNow();
    }

    public final AudioInfo playNext() {
        return play(1);
    }

    public final AudioInfo playPrevious() {
        return play(-1);
    }

    public final AudioQueue add(AudioInfo... infos) {
        return add(false, infos);
    }

    public final AudioQueue add(boolean shuffle, AudioInfo... infos) {
        if (infos == null || infos.length == 0) {
            return this;
        }
        return add(shuffle, Arrays.asList(infos));
    }

    public final AudioQueue add(boolean shuffle, List<AudioInfo> infos) {
        if (infos == null || infos.isEmpty()) {
            return this;
        }
        if (shuffle) {
            infos = new ArrayList<>(infos);
            Collections.shuffle(infos);
        }
        queue.addAll(infos);
        return this;
    }

    public final AudioQueue add(List<AudioInfo> infos) {
        return add(false, infos);
    }

    public final AudioQueue add(boolean shuffle, int index, List<AudioInfo> infos) {
        if (infos == null || infos.isEmpty()) {
            return this;
        }
        if (shuffle) {
            infos = new ArrayList<>(infos);
            Collections.shuffle(infos);
        }
        queue.addAll(index, infos);
        return this;
    }

    public final AudioQueue clear() {
        queue.clear();
        correctPointer();
        return this;
    }

    public final AudioInfo remove(int delta) {
        if (!correctPointer()) {
            return null;
        }
        final int pointer_ = correctPointer(pointer + delta);
        final AudioInfo temp = get(delta);
        if (temp != null) {
            queue.remove(pointer_);
            if (pointer_ <= pointer) {
                pointer--;
            }
            correctPointer();
            return temp;
        }
        return null;
    }

    public final AudioInfo removeNow() {
        return remove(0);
    }

    public final AudioInfo removeNext() {
        return remove(1);
    }

    public final AudioInfo removePrevious() {
        return remove(-1);
    }

    public final AudioQueue removeOldest(int max) {
        if (isStart() || isEmpty()) {
            return this;
        }
        int i = 0;
        while (!isStart() && !isEmpty()) {
            if (max != -1) {
                if (i >= max) {
                    return this;
                }
                i++;
            }
            remove(-pointer);
        }
        correctPointer();
        return this;
    }

    public final AudioQueue removePast() {
        return removeOldest(-1);
    }

    public final AudioQueue removeNewest(int max) {
        if (isEnd() || isEmpty()) {
            return this;
        }
        int i = 0;
        while (!isEnd() && !isEmpty()) {
            if (max != -1) {
                if (i >= max) {
                    return this;
                }
                i++;
            }
            remove(queue.size() - 1 - pointer);
        }
        correctPointer();
        return this;
    }

    public final AudioQueue removeFuture() {
        return removeNewest(-1);
    }

    public final AudioQueue shuffle(int times) {
        if (times < 1) {
            return this;
        }
        for (int i = 0; i < times; i++) {
            final List<AudioInfo> temp = getFuture();
            removeFuture();
            add(true, temp);
        }
        return this;
    }

    public final AudioQueue shuffle() {
        return shuffle(1);
    }

    public final List<AudioInfo> getQueue() {
        return new ArrayList<>(queue);
    }

    public final List<AudioInfo> getFuture() {
        return queue.subList(Math.max(0, pointer), queue.size());
    }

    public final List<AudioInfo> getPast() {
        return queue.subList(0, Math.max(0, Math.min(pointer, queue.size())));
    }

    public final Stream<AudioInfo> stream() {
        return queue.stream();
    }

    public final Stream<AudioInfo> parallelStream() {
        return queue.parallelStream();
    }

    private final boolean correctPointer() {
        pointer = correctPointer(pointer);
        return !queue.isEmpty();
    }

    private final int correctPointer(int pointer) {
        return correctPointer(pointer, queue.size() - 1);
    }

    public static final int correctPointer(int pointer, int max) {
        return Math.max(-1, Math.min(pointer, max));
    }

    @Override
    public String toString() {
        return "AudioQueue{" + "queue=" + queue + ", pointer=" + pointer + '}';
    }

}
