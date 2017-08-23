package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.entities.MessageEvent;

/**
 * MessageListener
 *
 * @author Panzer1119
 */
public interface MessageListener extends Listener {

    @Override
    public default Object fired(Object... data) {
        return messageReceived((MessageEvent) data[0]);
    }
    
    public boolean messageReceived(MessageEvent event);

}
