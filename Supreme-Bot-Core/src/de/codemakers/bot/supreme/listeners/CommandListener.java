package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.commands.Command;

/**
 * CommandListener
 *
 * @author Panzer1119
 */
public interface CommandListener extends Listener {

    @Override
    public default Object fired(Object... data) {
        final CommandType commandType = (CommandType) data[1];
        switch (commandType) {
            case CALLED:
                return onCommandCalled((Command) data[0]);
            case ACTION:
                return onCommandAction((Command) data[0]);
            case EXECUTED:
                return onCommandExecuted((Command) data[0]);
            default:
                return false;
        }
    }

    public boolean onCommandCalled(Command command);

    public boolean onCommandAction(Command command);

    public boolean onCommandExecuted(Command command);

}
