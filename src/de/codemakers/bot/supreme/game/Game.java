package de.codemakers.bot.supreme.game;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.entities.MessageEvent;

/**
 * Game
 *
 * @author Panzer1119
 */
public abstract class Game {

    private final String guild_id;

    public Game(String guild_id) {
        this.guild_id = guild_id;
    }

    public abstract boolean startGame();

    public abstract boolean endGame();

    public abstract boolean sendInput(ArgumentList arguments, MessageEvent event);

    public final String getGuildId() {
        return guild_id;
    }

}
