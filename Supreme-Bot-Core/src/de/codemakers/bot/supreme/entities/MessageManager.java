package de.codemakers.bot.supreme.entities;

import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.updater.Updateable;
import de.codemakers.bot.supreme.util.updater.Updater;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;

/**
 * MessageManager
 *
 * @author Panzer1119
 */
public abstract class MessageManager { //TODO Add permission control for reactions!

    private static final Queue<MessageManager> MESSAGE_MANAGER = new ConcurrentLinkedQueue<>();
    private static final Updateable MESSAGE_MANAGER_UPDATER = new Updateable() {
        @Override
        public long update(long timestamp) {
            updateAll();
            if (MESSAGE_MANAGER.isEmpty()) {
                return 1000;
            }
            return 250;
        }

        @Override
        public void delete() {
            MESSAGE_MANAGER.stream().forEach((messageManager) -> messageManager.delete());
        }
    };

    private final boolean fireOnEveryUser;
    protected final Message message_first;
    protected Message message;

    static {
        Updater.addUpdateable(MESSAGE_MANAGER_UPDATER);
    }

    public MessageManager(Message message, boolean fireOnEveryUser) {
        this.fireOnEveryUser = fireOnEveryUser;
        if (message == null) {
            throw new NullPointerException("The message must not be null!");
        } else {
            this.message_first = message;
            this.message = message;
            MESSAGE_MANAGER.add(this);
        }
    }

    public final Message getMessage() {
        return message;
    }

    public final boolean updateThis() {
        try {
            final List<MessageReaction> reactions_old = message.getReactions();
            message = Standard.getUpdatedMessage(message);
            if (message == null) {
                return false;
            }
            final List<MessageReaction> reactions_new = message.getReactions();
            try {
                update();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            reactions_new.stream().filter((messageReaction) -> {
                if (messageReaction == null) {
                    return false;
                }
                if (!reactions_old.contains(messageReaction)) {
                    return true;
                }
                final int index = reactions_old.indexOf(messageReaction);
                if (index < 0) {
                    return true;
                }
                final MessageReaction temp = reactions_old.get(index);
                if (temp == null) {
                    return true;
                }
                if ((temp.getCount() != messageReaction.getCount()) || (temp.getUsers().cacheSize() != messageReaction.getUsers().cacheSize())) {
                    return true;
                }
                return !temp.getUsers().getCached().equals(messageReaction.getUsers().getCached());
            }).forEach((messageReaction) -> {
                if (fireOnEveryUser) {
                    final int index = reactions_old.indexOf(messageReaction);
                    final MessageReaction temp = (index < 0 ? null : reactions_old.get(index));
                    messageReaction.getUsers().getCached().stream().filter((user) -> {
                        if (temp == null) {
                            return true;
                        }
                        return !temp.getUsers().getCached().contains(user);
                    }).forEach((user) -> {
                        onReaction(messageReaction, user, false);
                    });
                    if (temp != null) {
                        temp.getUsers().getCached().stream().filter((user) -> {
                            return !messageReaction.getUsers().getCached().contains(user);
                        }).forEach((user) -> {
                            onReaction(messageReaction, user, true);
                        });
                    }
                } else {
                    onReaction(messageReaction);
                }
            });
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public final boolean deleteThis() {
        try {
            delete();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        MESSAGE_MANAGER.remove(this);
        return MESSAGE_MANAGER.contains(this);
    }

    /**
     * Fires every time for every reaction made on a message
     *
     * @param messageReaction Reaction on a message
     */
    public abstract void onReaction(MessageReaction messageReaction);

    /**
     * Fires every time for every reaction made from every user on a message
     *
     * @param messageReaction Reaction on a message
     * @param user User
     * @param removed If the reaction was removed
     */
    public abstract void onReaction(MessageReaction messageReaction, User user, boolean removed);

    public abstract void update();

    public abstract void delete();

    public static final boolean updateAll() {
        try {
            MESSAGE_MANAGER.stream().forEach((messageManager) -> {
                messageManager.updateThis();
            });
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public final boolean isReacted(MessageReaction messageReaction, String emoji) {
        if (messageReaction == null || emoji == null) {
            return false;
        }
        if (!emoji.equals(messageReaction.getEmote().getName())) {
            return false;
        }
        return messageReaction.getCount() > 1;
    }

    public final boolean removeReaction(MessageReaction messageReaction) {
        if (messageReaction == null) {
            return false;
        }
        messageReaction.getUsers().stream().filter((user) -> !user.isBot()).forEach((user) -> {
            messageReaction.removeReaction(user).complete();
        });
        return true;
    }

}
