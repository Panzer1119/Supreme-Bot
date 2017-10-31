package de.codemakers.bot.supreme.commands.impl.fun;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.entities.MultiObject;
import de.codemakers.bot.supreme.entities.MultiObjectHolder;
import de.codemakers.bot.supreme.game.TicTacToe;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;
import de.codemakers.bot.supreme.permission.PermissionFilter;

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
        return arguments != null && arguments.isSize(1, -1);
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final MultiObjectHolder holder = MultiObjectHolder.of(event.getGuild(), event.getAuthor(), event.getTextChannel());
        if (event.getMessage().getMentionedUsers().size() == 1) {
            if (event.getGuild() != null) {
                final TicTacToe game = new TicTacToe();
                game.startGame(arguments, event);
                final MultiObject<TicTacToe> multiObject = new MultiObject<>(game, TicTacToe.class.getName(), holder, MultiObjectHolder.of(event.getGuild(), arguments.getUserFirst(), event.getTextChannel()));
            } else {
                event.sendMessage("You can't play against me, im a bot!");
            }
        } else if (arguments.consumeFirst(Standard.ARGUMENT_END, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
            if (event.getGuild() != null) {
                final MultiObject<TicTacToe> multiObject = MultiObject.getFirstMultiObject(TicTacToe.class.getName(), holder);
                if (multiObject != null) {
                    multiObject.getData().endGame(arguments, event);
                } else {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, "There is no game running!");
                }
            } else {
                event.sendMessage("You can't play against me, im a bot!");
            }
        } else if (event.getGuild() != null) {
            final MultiObject<TicTacToe> multiObject = MultiObject.getFirstMultiObject(TicTacToe.class.getName(), holder);
            if (multiObject != null) {
                multiObject.getData().sendInput(arguments, event);
            } else {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, "There is not game running!");
            }
        } else {
            CommandHandler.sendHelpMessage(invoker, event, this, false, false);
        }
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
