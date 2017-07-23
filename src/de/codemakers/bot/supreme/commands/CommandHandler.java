package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.commands.arguments.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.util.ArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

/**
 * CommandHandler
 *
 * @author Panzer1119
 */
public class CommandHandler {

    public static final ArrayList<Command> COMMANDS = new ArrayList<>();

    public static final boolean handleCommand(CommandContainer commandContainer) {
        try {
            if (commandContainer == null) {
                return false;
            }
            final Command command = commandContainer.invoker.getCommand();
            if (Standard.isAutoDeletingCommandByGuild(commandContainer.event.getGuild())) {
                commandContainer.event.getMessage().delete().queue();
            }
            if (command != null) {
                if (!PermissionHandler.check(command.getPermissionRoleFilter(), commandContainer.event, true)) {
                    return false;
                }
                final boolean safe = command.called(commandContainer.invoker, commandContainer.arguments, commandContainer.event);
                if (safe) {
                    command.action(commandContainer.invoker, commandContainer.arguments, commandContainer.event);
                } else {
                    sendHelpMessage(commandContainer.event, command, false);
                }
                command.executed(safe, commandContainer.event);
                return safe;
            } else {
                final Message message = commandContainer.event.sendAndWaitMessageFormat(":warning: Sorry %s, the command \"%s\" wasn't found!", commandContainer.event.getAuthor().getAsMention(), commandContainer.invoker);
                final long delay = Standard.getAutoDeleteCommandNotFoundMessageDelayByGuild(commandContainer.event.getGuild());
                if (delay != -1) {
                    Util.deleteMessage(message, delay);
                }
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean existsCommand(Invoker... invokers) {
        return getCommandByInvokers(invokers) != null;
    }

    public static final Command getCommandByInvokers(Invoker... invokers) {
        if (invokers == null || invokers.length == 0) {
            return null;
        }
        for (Command command : COMMANDS) {
            if (command.containsInvokers(invokers)) {
                return command;
            }
        }
        return null;
    }

    public static final boolean registerCommand(Command command) {
        if (!COMMANDS.contains(command)) {
            COMMANDS.add(command);
            return true;
        } else {
            return false;
        }
    }

    public static final boolean unregisterCommand(Command command) {
        if (COMMANDS.contains(command)) {
            COMMANDS.remove(command);
            return true;
        } else {
            return false;
        }
    }
    
    public static final boolean sendHelpMessage(MessageEvent event, Command command, boolean sendPrivate) {
        if (event == null || command == null) {
            return false;
        }
        final PermissionRoleFilter filter = command.getPermissionRoleFilter();
        if (filter != null && !PermissionHandler.check(filter, event, true)) {
            return false;
        }
        if (!sendPrivate && event.isPrivate()) {
            sendPrivate = true;
        }
        if (sendPrivate || Standard.getGuildSettings(event.getGuild()).getProperty("send_help_always_private", false)) {
            Util.sendPrivateMessage(event.getAuthor(), command.getHelp(new EmbedBuilder().setDescription(event.getAuthor().getAsMention())).build());
        } else {
            final Guild guild = event.getGuild();
            if (!PermissionHandler.check(filter, guild, event.getTextChannel())) {
                Util.sendPrivateMessage(event.getAuthor(), command.getHelp(new EmbedBuilder().setDescription(event.getAuthor().getAsMention())).build());
                return false;
            }
            event.sendMessage(command.getHelp(new EmbedBuilder().setDescription(event.getAuthor().getAsMention())).build());
        }
        return true;
    }

}
