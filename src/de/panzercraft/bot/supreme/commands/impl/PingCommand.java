package de.panzercraft.bot.supreme.commands.impl;

import de.panzercraft.bot.supreme.commands.Command;
import de.panzercraft.bot.supreme.commands.arguments.ArgumentList;
import de.panzercraft.bot.supreme.permission.PermissionRoleFilter;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * PingCommand
 * 
 * @author Panzer1119
 */
public class PingCommand implements Command {

    @Override
    public String[] getInvokes() {
        return new String[] {"ping"};
    }

    @Override
    public final boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public final void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
        event.getTextChannel().sendMessageFormat("Pong! (%d ms)", event.getJDA().getPing()).queue();
    }

    @Override
    public final void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
    }

    @Override
    public final String getHelp() {
        return null;
    }

    @Override
    public final PermissionRoleFilter getPermissionRoleFilter() {
        return null;
    }
    
}
