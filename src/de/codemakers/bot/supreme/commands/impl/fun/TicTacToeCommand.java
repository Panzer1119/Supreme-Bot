package de.codemakers.bot.supreme.commands.impl.fun;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.commands.arguments.Argument;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.game.Game;
import de.codemakers.bot.supreme.game.TicTacToe;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * TicTacToeCommand
 *
 * @author Panzer1119
 */
public class TicTacToeCommand extends Command {
    
    public static final Argument ARGUMENT_END = new Argument("end", Standard.STANDARD_ARGUMENT_PREFIXES);

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
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        if (event.getMessage().getMentionedUsers().size() == 1) {
            if (advancedGuild != null) {
                final Game game = new TicTacToe();
                game.startGame(arguments, event);
                advancedGuild.setGame(game);
            } else {
                event.sendMessage("You can't play against me, im a bot!");
            }
        } else if (arguments.consumeFirst(ARGUMENT_END, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
            if (advancedGuild != null) {
                if (advancedGuild.getGame() != null) {
                    if (advancedGuild.getGame() instanceof TicTacToe) {
                        advancedGuild.getGame().endGame(arguments, event);
                    } else {
                        event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, "This game isn't TicTacToe!");
                    }
                } else {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, "There is not game running!");
                }
            } else {
                event.sendMessage("You can't play against me, im a bot!");
            }
        } else if (advancedGuild != null && advancedGuild.getGame() != null) {
            advancedGuild.getGame().sendInput(arguments, event);
        } else {
            CommandHandler.sendHelpMessage(invoker, event, this, false);
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
        builder.addField(String.format("%s %s", invoker, ARGUMENT_END.getCompleteArgument(0)), "Stops the running TicTacToe game.", false);
        return builder;
    }

    @Override
    public final PermissionRoleFilter getPermissionRoleFilter() {
        return null;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

}
