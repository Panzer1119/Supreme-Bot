package de.codemakers.bot.supreme.entities;

import de.codemakers.bot.supreme.util.Emoji;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;

/**
 * DeleteMessageManager
 *
 * @author Panzer1119
 */
public class DeleteMessageManager extends MessageManager {

    public DeleteMessageManager(Message message) {
        super(message, false);
        message.addReaction(Emoji.NO).queue();
    }

    @Override
    public final void onReaction(MessageReaction messageReaction) {
        if (isReacted(messageReaction, Emoji.NO)) {
            deleteThis();
        }
    }

    @Override
    public final void onReaction(MessageReaction messageReaction, User user, boolean removed) {
    }

    @Override
    public final void update() {
    }

    @Override
    public final void delete() {
        message_first.delete().queue();
    }

    public static final DeleteMessageManager monitor(Message message) {
        return new DeleteMessageManager(message);
    }

}
