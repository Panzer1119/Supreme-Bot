package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;

/**
 * CommandListener
 *
 * @author Panzer1119
 */
public interface CommandListener extends Listener {

    @Override
    public default Object fired(Object... data) {
        final CommandType commandType = (CommandType) data[2];
        switch (commandType) {
            case CALLED:
                return onCommandCalled((Command) data[0], (ArgumentList) data[1]);
            case ACTION:
                return onCommandAction((Command) data[0], (ArgumentList) data[1]);
            case EXECUTED:
                return onCommandExecuted((Command) data[0], (ArgumentList) data[1]);
            default:
                return false;
        }
    }

    public boolean onCommandCalled(Command command, ArgumentList arguments);

    public boolean onCommandAction(Command command, ArgumentList arguments);

    public boolean onCommandExecuted(Command command, ArgumentList arguments);

}
