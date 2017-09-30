package de.codemakers.bot.supreme.exceptions;

import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;

/**
 * BotException
 *
 * @author Panzer1119
 */
public class BotException extends RuntimeException {
    
    public BotException() {
        super();
    }
    
    public BotException(String message) {
        super(message);
    }
    
    public BotException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BotException(Throwable cause) {
        super(cause);
    }
    
    public EmbedBuilder getMessage(Channel channel) {
        return new EmbedBuilder().setColor(Color.RED).setDescription(getMessage()).setTitle(getClass().getSimpleName());
    }

}
