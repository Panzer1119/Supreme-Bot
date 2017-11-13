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
import net.dv8tion.jda.core.requests.RestAction;

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
        } else if (emote.isEmoji()) {
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

    public static ReactionContainer unregisterListener(Message message, AdvancedEmote emote, boolean removeReactions) {
        if (message == null || emote == null) {
            return null;
        }
        final Map<AdvancedEmote, ReactionContainer> containers = LISTENERS.get(message);
        if (containers == null) {
            return null;
        }
        final ReactionContainer container = containers.remove(emote);
        if (containers.isEmpty()) {
            LISTENERS.remove(message);
        }
        if (removeReactions) {
            try {
                Standard.getUpdatedMessage(message).queue((message_) -> message_.getReactions().stream().filter((reaction) -> emote.equals(reaction)).forEach((reaction) -> reaction.getUsers().stream().map(reaction::removeReaction).forEach(RestAction::queue)));
            } catch (Exception ex) {
                System.err.println("ReactionListener unregisterListener1 error: " + ex);
            }
        }
        return container;
    }

    public static boolean unregisterListener(Message message, boolean removeReactions) {
        if (message == null) {
            return false;
        }
        final Map<AdvancedEmote, ReactionContainer> containers = LISTENERS.get(message);
        if (containers == null) {
            return true;
        }
        LISTENERS.remove(message);
        if (removeReactions) {
            try {
                Standard.getUpdatedMessage(message).queue((message_) -> message_.clearReactions().queue());
            } catch (Exception ex) {
                System.err.println("ReactionListener unregisterListener2 error: " + ex);
            }
        }
        return true;
    }

    static void unregisterAll() {
        LISTENERS.entrySet().stream().forEach((containers) -> containers.getValue().keySet().stream().forEach((emote) -> unregisterListener(containers.getKey(), emote, true)));
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
            //event.getReaction().removeReaction(event.getUser()).queue(); //FIXME Currently not supported, because this would trigger the reaction again
        }
        if (container.filter != null && !container.filter.isPermissionGranted(event.getGuild(), event.getUser())) {
            return;
        }
        if (container.timeout.isTimeout()) {
            unregisterListener(message, emote, false);
            container.timeout.runAfterTimeout();
            return;
        }
        container.listener.onReaction(event.getReaction(), emote, event.getGuild(), event.getUser());
    }

    static void update(Instant instant, boolean delete) {
        new ConcurrentHashMap<>(LISTENERS).entrySet().stream().forEach((containers) -> {
            containers.getValue().entrySet().stream().filter((container) -> (delete || (container.getValue().timeout != null && container.getValue().timeout.isTimeout(instant)))).forEach((container) -> {
                try {
                    if (container.getValue().timeout.isRunningAfterTimeout()) {
                        container.getValue().timeout.runAfterTimeout();
                    } else {
                        unregisterListener(containers.getKey(), container.getKey(), false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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

    public static boolean deleteMessageWithReaction(Message message, String emote_name, long amount, TimeUnit unit, boolean removeAllListeners) {
        return deleteMessageWithReaction(message, emote_name, amount, unit, removeAllListeners, null);
    }

    public static boolean deleteMessageWithReaction(Message message, String emote_name, long amount, TimeUnit unit, boolean removeAllListeners, ReactionPermissionFilter filter) {
        return ReactionListener.registerListener(message, AdvancedEmote.parse(emote_name), (reaction, emote, guild, user) -> {
            if (removeAllListeners) {
                unregisterListener(message, message.getGuild() != null);
            }
            message.delete().queue();
        }, new Timeout(amount, unit, () -> {
            if (removeAllListeners) {
                unregisterListener(message, message.getGuild() != null);
            } else {
                unregisterListener(message, AdvancedEmote.parse(emote_name), message.getGuild() != null);
            }
            message.delete().queue();
        }), filter, true);
    }

}
