package de.codemakers.bot.supreme.commands.impl.fun;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedEmote;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.entities.MultiObject;
import de.codemakers.bot.supreme.entities.MultiObjectHolder;
import de.codemakers.bot.supreme.game.TicTacToe;
import de.codemakers.bot.supreme.listeners.ReactionListener;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.permission.ReactionPermissionFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.TimeUnit;
import de.codemakers.bot.supreme.util.Timeout;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * TicTacToeCommand
 *
 * @author Panzer1119
 */
public class TicTacToeCommand extends Command {

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("tictactoe", this), Invoker.createInvoker("ttt", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return arguments != null && arguments.isSize(1, -1) && !event.isPrivate();
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final MultiObjectHolder holder = MultiObjectHolder.of(event.getGuild(), event.getAuthor(), event.getTextChannel());
        final User opponent = arguments.getUserFirst();
        if (opponent != null) {
            if (event.getGuild() != null) {
                if (MultiObject.getFirstMultiObject(TicTacToe.class.getName(), holder) != null) {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "you are already playing TicTacToe!").build());
                    return;
                }
                final Message message = event.sendAndWaitMessage(String.format("Hey %s, %s invited you for a round of TicTacToe!%nAccept with %s or decline with %s.", opponent.getAsMention(), event.getAuthor().getAsMention(), Emoji.CHECK_MARK, Emoji.MARK_MULTIPLICATION_SIGN));
                ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.CHECK_MARK), (reaction, emote, guild, user) -> {
                    if (!startGame(arguments, event, holder, opponent)) {
                        reaction.removeReaction(user).queue();
                    } else {
                        ReactionListener.unregisterListener(message);
                        message.delete().queue();
                    }
                }, null, ReactionPermissionFilter.createUserFilter(opponent), true);
                ReactionListener.deleteMessageWithReaction(message, Emoji.MARK_MULTIPLICATION_SIGN, 1, TimeUnit.MINUTES, true, ReactionPermissionFilter.createUsersFilter(opponent, event.getAuthor()));
            } else {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "you can't play against me, im a bot!").build());
            }
        } else if (arguments.consumeFirst(Standard.ARGUMENT_END, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
            if (event.getGuild() != null) {
                final MultiObject<TicTacToe> multiObject = MultiObject.getFirstMultiObject(TicTacToe.class.getName(), holder);
                if (multiObject != null) {
                    multiObject.getData().endGame(arguments, event);
                } else {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "there is no game running!").build());
                }
            } else {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "you can't play against me, im a bot!").build());
            }
        } else if (event.getGuild() != null) {
            final MultiObject<TicTacToe> multiObject = MultiObject.getFirstMultiObject(TicTacToe.class.getName(), holder);
            if (multiObject != null) {
                multiObject.getData().sendInput(arguments, event);
            } else {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "there is no game running!").build());
            }
        } else {
            CommandHandler.sendHelpMessage(invoker, event, this, false, false);
        }
    }

    private final boolean startGame(ArgumentList arguments, MessageEvent event, MultiObjectHolder holder, User opponent) {
        final MultiObjectHolder holder_opponent = MultiObjectHolder.of(event.getGuild(), opponent, event.getTextChannel());
        if (MultiObject.getFirstMultiObject(TicTacToe.class.getName(), holder_opponent) != null) {
            return false;
        }
        final TicTacToe game = new TicTacToe();
        game.startGame(arguments, event);
        final MultiObject<TicTacToe> multiObject = new MultiObject<>(game, TicTacToe.class.getName(), holder, holder_opponent);
        return true;
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s <User @Mention>", invoker), "Starts TicTacToe against the mentioned user.", false);
        builder.addField(String.format("%s <Field as Number>", invoker), "Tooks the given field in the TicTacToe board.", false);
        builder.addField(String.format("%s %s", invoker, Standard.ARGUMENT_END.getCompleteArgument(0, -1)), "Stops the running TicTacToe game.", false);
        return builder;
    }

    @Override
    public final PermissionFilter getPermissionFilter() {
        return null;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_FUN;
    }

}
