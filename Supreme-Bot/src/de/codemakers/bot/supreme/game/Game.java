package de.codemakers.bot.supreme.game;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.entities.MessageEvent;

/**
 * Game
 *
 * @author Panzer1119
 */
public abstract class Game {

    public abstract boolean startGame(ArgumentList arguments, MessageEvent event);

    public abstract boolean endGame(ArgumentList arguments, MessageEvent event);

    public abstract boolean sendInput(ArgumentList arguments, MessageEvent event);

}
