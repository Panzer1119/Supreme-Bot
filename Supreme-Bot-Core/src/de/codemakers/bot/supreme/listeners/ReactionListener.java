package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.entities.AdvancedEmote;
import de.codemakers.bot.supreme.entities.ReactionContainer;
import de.codemakers.bot.supreme.permission.ReactionPermissionFilter;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.TimeUnit;
import de.codemakers.bot.supreme.util.Timeout;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;

/**
 * ReactionListener
 *
 * @author Panzer1119
 */
public interface ReactionListener {

    static final Map<Message, Map<AdvancedEmote, ReactionContainer>> LISTENERS = new ConcurrentHashMap<>();

    void onReaction(MessageReaction reaction, AdvancedEmote emote, Guild guild, User user);

    public static boolean registerListener(Message message, AdvancedEmote emote, ReactionListener listener, boolean removeReaction) {
        return registerListener(message, emote, listener, null, null, removeReaction);
    }

    public static boolean registerListener(Message message, AdvancedEmote emote, ReactionListener listener, Timeout timeout, ReactionPermissionFilter filter, boolean removeReaction) {
        if (message == null || emote == null || listener == null) {
            return false;
        }
        if (timeout == null) {
            timeout = new Timeout(-1, TimeUnit.MILLISECONDS, null);
        }
        if (emote.isCustom()) {
            message.addReaction(emote.getEmote()).queue();
        } else {
            message.addReaction(emote.getEmoji().getUnicode()).queue();
        }
        final ReactionContainer container = new ReactionContainer(timeout, listener, filter, removeReaction);
        LISTENERS.computeIfAbsent(message, message_ -> new ConcurrentHashMap<AdvancedEmote, ReactionContainer>() {
            @Override
            public ReactionContainer get(Object emote) {
                final ReactionContainer temp = super.get(emote);
                if (temp != null) {
                    return temp;
                }
                return entrySet().stream().filter((containers) -> containers.getKey().equals(emote)).findFirst().orElse(new AbstractMap.SimpleEntry<>(null, null)).getValue();
            }

        }).computeIfAbsent(emote, emote_ -> container);
        return true;
    }

    public static ReactionContainer unregisterListener(Message message, AdvancedEmote emote) {
        if (message == null || emote == null) {
            return null;
        }
        final Map<AdvancedEmote, ReactionContainer> containers = LISTENERS.get(message);
        if (containers == null) {
            return null;
        }
        Standard.getUpdatedMessage(message).getReactions().stream().filter((reaction) -> emote.equals(AdvancedEmote.ofReactionEmote(reaction.getEmote()))).forEach((reaction) -> reaction.removeReaction().queue());
        final ReactionContainer container = containers.remove(emote);
        if (containers.isEmpty()) {
            LISTENERS.remove(message);
        }
        return container;
    }

    static void unregisterAll() {
        LISTENERS.entrySet().stream().forEach((containers) -> containers.getValue().keySet().stream().forEach((emote) -> unregisterListener(containers.getKey(), emote)));
        LISTENERS.clear();
    }

    public static void handle(GenericMessageReactionEvent event) {
        if (event == null || Standard.getSelfUser().getIdLong() == event.getUser().getIdLong()) {
            return;
        }
        final Message message = (event.getTextChannel() != null ? event.getTextChannel().getMessageById(event.getMessageIdLong()).complete() : (event.getPrivateChannel() != null ? event.getPrivateChannel().getMessageById(event.getMessageIdLong()).complete() : null));
        if (message == null) {
            return;
        }
        final Map<AdvancedEmote, ReactionContainer> containers = LISTENERS.get(message);
        if (containers == null) {
            return;
        }
        final AdvancedEmote emote = AdvancedEmote.ofReactionEmote(event.getReactionEmote());
        if (emote == null) {
            return;
        }
        final ReactionContainer container = containers.get(emote);
        if (container == null) {
            return;
        }
        if (container.removeReaction) {
            //event.getReaction().removeReaction(event.getUser()).queue(); //FIXME Currently not supported
        }
        if (container.filter != null && !container.filter.isPermissionGranted(event.getGuild(), event.getUser())) {
            return;
        }
        if (container.timeout.isTimeout()) {
            unregisterListener(message, emote);
            container.timeout.runAfterTimeout();
            return;
        }
        container.listener.onReaction(event.getReaction(), emote, event.getGuild(), event.getUser());
    }

    static void update(Instant instant) {
        new ConcurrentHashMap<>(LISTENERS).entrySet().stream().forEach((containers) -> {
            containers.getValue().entrySet().stream().filter((container) -> container.getValue().timeout.isTimeout(instant)).forEach((container) -> {
                unregisterListener(containers.getKey(), container.getKey());
                container.getValue().timeout.runAfterTimeout();
            });
        });
    }

    public static boolean isReacted(MessageReaction messageReaction, String emoji) {
        if (messageReaction == null || emoji == null) {
            return false;
        }
        if (!emoji.equals(messageReaction.getEmote().getName())) {
            return false;
        }
        return messageReaction.getCount() > 1;
    }

    public static boolean removeReaction(MessageReaction messageReaction) {
        if (messageReaction == null) {
            return false;
        }
        final User self = Standard.getSelfUser();
        messageReaction.getUsers().stream().filter((user) -> self.getIdLong() == user.getIdLong()).forEach((user) -> {
            messageReaction.removeReaction(user).complete();
        });
        return true;
    }

    public static boolean deleteMessageWithReaction(Message message, long amount, TimeUnit unit) {
        return deleteMessageWithReaction(message, amount, unit, null);
    }

    public static boolean deleteMessageWithReaction(Message message, long amount, TimeUnit unit, ReactionPermissionFilter filter) {
        return ReactionListener.registerListener(message, AdvancedEmote.parse("x"), (reaction, emote, guild, user) -> {
            message.delete().queue();
        }, new Timeout(amount, unit, () -> message.delete().queue()), filter, true);
    }

}
