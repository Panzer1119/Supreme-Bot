package de.codemakers.bot.supreme.game;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.entities.MessageEvent;

/**
 * Game
 *
 * @author Panzer1119
 */
public abstract class Game {

    private long guild_id;

    public Game() {
        this(0);
    }

    public Game(long guild_id) {
        this.guild_id = guild_id;
    }

    public abstract boolean startGame(ArgumentList arguments, MessageEvent event);

    public abstract boolean endGame(ArgumentList arguments, MessageEvent event);

    public abstract boolean sendInput(ArgumentList arguments, MessageEvent event);

    public final String getGuildId() {
        return "" + guild_id;
    }

    public final long getGuildIdLong() {
        return guild_id;
    }

    protected final Game setGuildId(long guild_id) {
        this.guild_id = guild_id;
        return this;
    }

}
