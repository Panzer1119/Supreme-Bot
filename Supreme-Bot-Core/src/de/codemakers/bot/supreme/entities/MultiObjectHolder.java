package de.codemakers.bot.supreme.entities;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

/**
 * MultiObjectHolder
 *
 * @author Panzer1119
 */
public class MultiObjectHolder {

    public final long guild_id;
    public final long user_id;
    public final long channel_id;

    public MultiObjectHolder(long guild_id, long user_id, long channel_id) {
        this.guild_id = guild_id;
        this.user_id = user_id;
        this.channel_id = channel_id;
    }

    public final boolean isForAllGuilds() {
        return guild_id == 0;
    }

    public final boolean isForAllUsers() {
        return user_id == 0;
    }

    public final boolean isForAllChannels() {
        return channel_id == 0;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof MultiObjectHolder) {
            final MultiObjectHolder holder = (MultiObjectHolder) object;
            if (guild_id == holder.guild_id && user_id == holder.user_id && channel_id == holder.channel_id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final String toString() {
        return String.format("%s: guild_id = %d, user_id = %d, channel_id = %d", getClass().getSimpleName(), guild_id, user_id, channel_id);
    }

    public static final MultiObjectHolder of(Guild guild, User user, Channel channel) {
        return of(guild == null ? 0 : guild.getIdLong(), user == null ? 0 : user.getIdLong(), channel == null ? 0 : channel.getIdLong());
    }

    public static final MultiObjectHolder of(long guild_id, long user_id, long channel_id) {
        return new MultiObjectHolder(guild_id, user_id, channel_id);
    }

}
