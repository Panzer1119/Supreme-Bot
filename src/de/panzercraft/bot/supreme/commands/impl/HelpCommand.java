package de.panzercraft.bot.supreme.commands.impl;

import de.panzercraft.bot.supreme.commands.Command;
import de.panzercraft.bot.supreme.commands.CommandHandler;
import de.panzercraft.bot.supreme.commands.arguments.ArgumentList;
import de.panzercraft.bot.supreme.permission.PermissionRoleFilter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * HelpCommand
 * 
 * @author Panzer1119
 */
public class HelpCommand implements Command {

    @Override
    public String[] getInvokes() {
        return new String[] {"help", "h"};
    }

    @Override
    public boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) { //TODO Vielleicht spaeter machen, dass nur Commands aufgelistet werden, die auch Rechtemaessig ausgefuehrt werden koennen
        if (arguments != null && arguments.isSize(1, -1)) {
            while (arguments.hasArguments()) {
                final String command_invoke = arguments.consumeFirst();
                final Command command = CommandHandler.getCommandByInvoke(command_invoke);
                CommandHandler.sendHelpMessage(event, command);
            }
        } else {
            CommandHandler.sendHelpMessage(event, this);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(EmbedBuilder builder) {
        for (String invoke : getInvokes()) {
            builder.addField(invoke + " [Command 1] [Command 2] [Command 3]...", "Returns the help for the given commands.", false);
        }
        return builder;
    }

    @Override
    public PermissionRoleFilter getPermissionRoleFilter() {
        return null;
    }
    
}
