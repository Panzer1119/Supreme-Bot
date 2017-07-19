package de.panzercraft.bot.supreme.commands;

import de.panzercraft.bot.supreme.permission.PermissionHandler;
import de.panzercraft.bot.supreme.permission.PermissionRole;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * CommandHandler
 *
 * @author Panzer1119
 */
public class CommandHandler {

    public static final ArrayList<Command> commands = new ArrayList<>();
    
    public static final boolean handleCommand(CommandContainer commandContainer) {
        try {
            if (commandContainer == null) {
                return false;
            }
            final Command command = getCommandByInvoke(commandContainer.invoke);
            if (command != null) {
                final PermissionRole minimum = command.getMinimumPermissionRole();
                if (!PermissionHandler.check(minimum, commandContainer.event)) {
                    return false;
                }
                final boolean safe = command.called(commandContainer.invoke, commandContainer.args, commandContainer.event);
                if (safe) {
                    command.action(commandContainer.invoke, commandContainer.args, commandContainer.event);
                } else {
                    commandContainer.event.getTextChannel().sendMessageFormat("%s, usage of %s: %s", commandContainer.event.getAuthor().getAsMention(), Arrays.toString(command.getInvokes()), command.getHelp()).queue();
                }
                command.executed(safe, commandContainer.event);
                return safe;
            } else {
                System.out.println("Command not found! \"" + commandContainer.invoke + "\"");
                return false;
            }
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            return false;
        }
    }
    
    public static final boolean existsCommand(String... invokes) {
        return getCommandByInvokes(invokes) != null;
    }
    
    public static final Command getCommandByInvokes(String... invokes) {
        for (String invoke : invokes) {
            final Command command = getCommandByInvoke(invoke);
            if (command != null) {
                return command;
            }
        }
        return null;
    }
    
    public static final Command getCommandByInvoke(String invoke) {
        if (invoke == null) {
            return null;
        }
        for (Command command : commands) {
            for (String temp : command.getInvokes()) {
                if ((temp != null) && temp.equals(invoke)) {
                    return command;
                }
            }
        }
        return null;
    }
    
    public static final boolean registerCommand(Command command) {
        String[] invokes = null;
        if (command == null || (invokes = command.getInvokes()) == null || invokes.length == 0) {
            return false;
        }
        if (!existsCommand(invokes) && !commands.contains(command)) {
            commands.add(command);
            return true;
        } else {
            return false;
        }
    }
    
    public static final boolean unregisterCommand(String... invokes) {
        return unregisterCommand(getCommandByInvokes(invokes));
    }
    
    public static final boolean unregisterCommand(Command command) {
        String[] invokes = null;
        if (command == null || (invokes = command.getInvokes()) == null || invokes.length == 0) {
            return false;
        }
        if (existsCommand(invokes) && commands.contains(command)) {
            commands.remove(command);
            return true;
        } else {
            return false;
        }
    }

}
