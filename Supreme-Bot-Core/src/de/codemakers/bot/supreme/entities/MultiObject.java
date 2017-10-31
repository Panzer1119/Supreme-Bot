package de.codemakers.bot.supreme.entities;

import de.codemakers.bot.supreme.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

/**
 * MultiObject
 *
 * @author Panzer1119
 */
public class MultiObject<T> {

    public static final Queue<MultiObject> MULTI_OBJECTS = new ConcurrentLinkedQueue<>();
    private static final ArrayList<Long> IDS = new ArrayList<>();

    private long id = 0;
    private final String name;
    private T data;
    private final ArrayList<MultiObjectHolder> holders = new ArrayList<MultiObjectHolder>() {
        @Override
        public final boolean addAll(Collection<? extends MultiObjectHolder> info) {
            return super.addAll(info.stream().filter((info_) -> !contains(info_)).collect(Collectors.toList()));
        }

        @Override
        public final boolean add(MultiObjectHolder info) {
            if (contains(info)) {
                return false;
            }
            return super.add(info);
        }
    };

    public MultiObject(T data, String name, MultiObjectHolder... holders_) {
        this.name = name;
        if (name == null) {
            throw new NullPointerException("The name must not be null!");
        }
        this.data = data;
        addHolders(holders_);
        register();
    }

    public final String getName() {
        return name;
    }

    public final long getId() {
        return id;
    }

    public final boolean hasData() {
        return data != null;
    }

    public final T getData() {
        return data;
    }

    public final MultiObject<T> setData(T data) {
        this.data = data;
        return this;
    }

    public final boolean hasHolders() {
        return !holders.isEmpty();
    }

    public final ArrayList<MultiObjectHolder> getHolders() {
        return holders;
    }

    public final List<MultiObjectHolder> getHolders(long guild_id, long user_id, long channel_id) {
        return holders.stream().filter((holder) -> ((guild_id == -1 || holder.guild_id == guild_id) && (user_id == -1 || holder.user_id == user_id) && (channel_id == -1 || holder.channel_id == channel_id))).collect(Collectors.toList());
    }

    public final boolean containsHolder(long guild_id, long user_id, long channel_id) {
        return holders.stream().filter((holder) -> ((guild_id == -1 || holder.guild_id == guild_id) && (user_id == -1 || holder.user_id == user_id) && (channel_id == -1 || holder.channel_id == channel_id))).count() > 0;
    }

    public final boolean hasAccess(Guild guild, User user, Channel channel) {
        return containsHolder(guild == null ? 0 : guild.getIdLong(), user == null ? 0 : user.getIdLong(), channel == null ? 0 : channel.getIdLong());
    }

    public final MultiObject<T> addHolders(MultiObjectHolder... holders_) {
        if (holders_ == null || holders_.length == 0) {
            return this;
        }
        holders.addAll(Arrays.asList(holders_));
        return this;
    }

    public final MultiObject<T> removeHolders(MultiObjectHolder... holders_) {
        if (holders_ == null || holders_.length == 0) {
            return this;
        }
        holders.removeAll(Arrays.asList(holders_));
        return this;
    }

    public final boolean isForAllGuilds() {
        return holders.stream().allMatch((holder) -> holder.isForAllGuilds());
    }

    public final boolean isForAllUsers() {
        return holders.stream().allMatch((holder) -> holder.isForAllUsers());
    }

    public final boolean isForAllChannels() {
        return holders.stream().allMatch((holder) -> holder.isForAllChannels());
    }

    public final boolean register() {
        if (id == 0) {
            this.id = Util.getRandomLong(IDS.toArray(new Long[IDS.size()]));
            IDS.add(id);
        }
        if (MULTI_OBJECTS.contains(this)) {
            return false;
        }
        if (id != 0 && IDS.contains(id)) {
            this.id = Util.getRandomLong(IDS.toArray(new Long[IDS.size()]));
            IDS.add(id);
        }
        return MULTI_OBJECTS.add(this);
    }

    public final boolean unregister() {
        if (!MULTI_OBJECTS.contains(this)) {
            return false;
        }
        MULTI_OBJECTS.remove(this);
        if (!MULTI_OBJECTS.contains(this)) {
            IDS.remove(id);
            this.id = 0;
            return true;
        }
        return false;
    }

    @Override
    public final String toString() {
        return String.format("%s: id = %d, data = %s, holders = %s", getClass().getSimpleName(), id, data, holders.stream().map((holder) -> holder.toString()).collect(Collectors.joining("; ", "[", "]")));
    }

    public static final List<MultiObject> getMultiObjectsExact(MultiObjectHolder... holders) {
        return getMultiObjectsExact(null, holders);
    }

    public static final List<MultiObject> getMultiObjectsExact(String name, MultiObjectHolder... holders) {
        final List<MultiObjectHolder> holders_ = Arrays.asList(holders);
        return MULTI_OBJECTS.stream().filter((multiObject) -> ((name == null || name.equals(multiObject.getName())) && (multiObject.getHolders().size() == holders_.size() && multiObject.getHolders().containsAll(holders_)))).collect(Collectors.toList());
    }

    public static final List<MultiObject> getMultiObjects(String name, MultiObjectHolder... holders) {
        return MULTI_OBJECTS.stream().filter((multiObject) -> ((name == null || name.equals(multiObject.getName())) && multiObject.getHolders().stream().anyMatch((holder) -> Util.contains(holders, holder)))).collect(Collectors.toList());
    }

    public static final MultiObject getFirstMultiObject(String name, MultiObjectHolder... holders) {
        return getMultiObjects(name, holders).stream().findFirst().orElse(null);
    }

    public static final <T> MultiObject<T> getMultiObjectById(long id) {
        return MULTI_OBJECTS.stream().filter((multiObject) -> multiObject.getId() == id).findFirst().orElse(null);
    }

    public static final <K, V> MultiObject<HashMap<K, V>> asHashMap(String name, Class<? extends K> clazz_key, Class<? extends V> clazz_value) {
        return new MultiObject<>(new HashMap<>(), name);
    }

    public static final <T> MultiObject<ArrayList<T>> asArrayList(String name, Class<? extends T> clazz) {
        return new MultiObject<>(new ArrayList<>(), name);
    }

}
