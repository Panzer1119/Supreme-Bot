package de.panzercraft.bot.supreme.commands;

import de.panzercraft.bot.supreme.permission.PermissionHandler;
import de.panzercraft.bot.supreme.permission.PermissionRole;
import de.panzercraft.bot.supreme.permission.PermissionRoleFilter;
import de.panzercraft.bot.supreme.util.Standard;
import de.panzercraft.bot.supreme.util.Util;
import java.util.ArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
            if (Standard.isAutoDeletingCommand()) {
                commandContainer.event.getMessage().delete().queue();
            }
            if (command != null) {
                if (!PermissionHandler.check(command.getPermissionRoleFilter(), commandContainer.event, true)) {
                    return false;
                }
                final boolean safe = command.called(commandContainer.invoke, commandContainer.arguments, commandContainer.event);
                if (safe) {
                    command.action(commandContainer.invoke, commandContainer.arguments, commandContainer.event);
                } else {
                    sendHelpMessage(commandContainer.event, command, false);
                }
                command.executed(safe, commandContainer.event);
                return safe;
            } else {
                final Message message = commandContainer.event.getTextChannel().sendMessageFormat(":warning: Sorry %s, the command \"%s\" wasn't found!", commandContainer.event.getAuthor().getAsMention(), commandContainer.invoke).complete();
                final long delay = Standard.getAutoDeleteCommandNotFoundMessageDelay();
                if (delay != -1) {
                    Util.deleteMessage(message, delay);
                }
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
                if ((temp != null) && (temp.equals(invoke) || temp.equalsIgnoreCase(invoke))) {
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
    
    public static final boolean sendHelpMessage(MessageReceivedEvent event, Command command, boolean sendPrivate) {
        if (event == null || command == null) {
            return false;
        }
        final PermissionRoleFilter filter = command.getPermissionRoleFilter();
        if (filter != null && !PermissionHandler.check(filter, event, true)) {
            return false;
        }
        if (sendPrivate || Standard.getGuildSettings(event.getGuild()).getProperty("send_help_always_private", false)) {
            Util.sendPrivateMessage(event.getAuthor(), command.getHelp(new EmbedBuilder().setDescription(event.getAuthor().getAsMention())).build());
        } else {
            final Guild guild = event.getGuild();
            if (!PermissionHandler.check(filter, guild, event.getTextChannel())) {
                Util.sendPrivateMessage(event.getAuthor(), command.getHelp(new EmbedBuilder().setDescription(event.getAuthor().getAsMention())).build());
                return false;
            }
            event.getTextChannel().sendMessage(command.getHelp(new EmbedBuilder().setDescription(event.getAuthor().getAsMention())).build()).queue();
        }
        return true;
    }

}
