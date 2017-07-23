package de.codemakers.bot.supreme.commands.impl.fun;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
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

    @Override
    public String[] getInvokes() {
        return new String[]{"tictactoe", "ttt"};
    }

    @Override
    public final boolean called(String invoke, ArgumentList arguments, MessageEvent event) {
        return arguments != null && arguments.isSize(1, -1);
    }

    @Override
    public final void action(String invoke, ArgumentList arguments, MessageEvent event) {
        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
        if (event.getMessage().getMentionedUsers().size() == 1) {
            if (advancedGuild != null) {
                final Game game = new TicTacToe();
                game.startGame(arguments, event);
                advancedGuild.setGame(game);
            }
        } else if ("end".equals(arguments.getFirst())) {
            if (advancedGuild != null && advancedGuild.getGame() != null) {
                advancedGuild.getGame().endGame(arguments, event);
            }
        } else if (advancedGuild != null && advancedGuild.getGame() != null) {
            advancedGuild.getGame().sendInput(arguments, event);
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(EmbedBuilder builder) {
        for (String invoke : getInvokes()) {
            builder.addField(invoke, "TicTacToe", false);
        }
        return builder;
    }

    @Override
    public final PermissionRoleFilter getPermissionRoleFilter() {
        return null;
    }

}
